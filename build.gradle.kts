// ffp: FFP-MOBILE-GRADLE declarative kotlin
import java.io.File
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

val externalBuildRoot = project.findProperty("buildDirOverride")?.toString()
    ?: System.getenv("TIANXIA_MOBILE_BUILD_DIR")
if (!externalBuildRoot.isNullOrBlank()) {
    layout.buildDirectory.set(File(externalBuildRoot))
}

fun resolveSdkDir(): File {
    val candidates = listOfNotNull(
        System.getenv("ANDROID_HOME"),
        System.getenv("ANDROID_SDK_ROOT"),
        "${System.getProperty("user.home")}/Android/Sdk",
        "/usr/local/lib/android/sdk"
    ).map { File(it) }
    return candidates.firstOrNull { File(it, "platforms/android-34/android.jar").exists() }
        ?: error("Android SDK platform android-34 not found in ANDROID_HOME / ANDROID_SDK_ROOT")
}

fun resolveAapt2(sdkDir: File): String {
    val overrideBin = project.findProperty("android.aapt2FromMavenOverride")?.toString()
    if (!overrideBin.isNullOrBlank() && File(overrideBin).canExecute()) {
        return overrideBin
    }
    val sysAapt2 = File(System.getenv("PREFIX") ?: "/usr/local", "bin/aapt2")
    if (sysAapt2.canExecute()) {
        return sysAapt2.absolutePath
    }
    val btDir = File(sdkDir, "build-tools")
    val found = btDir.listFiles()?.sortedDescending()?.map { File(it, "aapt2") }?.firstOrNull { it.canExecute() }
    return found?.absolutePath ?: "aapt2"
}

fun resolveD8Jar(sdkDir: File): File {
    val btDir = File(sdkDir, "build-tools")
    val found = btDir.listFiles()?.sortedDescending()?.map { File(it, "lib/d8.jar") }?.firstOrNull { it.isFile }
    return found ?: error("d8.jar not found under ${btDir.absolutePath}")
}

fun runTool(cmd: List<String>) {
    val proc = ProcessBuilder(cmd)
        .redirectErrorStream(true)
        .start()
    val out = proc.inputStream.bufferedReader().readText()
    val code = proc.waitFor()
    if (code != 0) {
        error("Command failed ($code): ${cmd.joinToString(" ")}\n$out")
    }
}

fun writeStoredEntry(zos: ZipOutputStream, entryName: String, bytes: ByteArray, align4: Boolean, currentOffset: Long): Long {
    val entry = ZipEntry(entryName)
    entry.method = ZipEntry.STORED
    entry.size = bytes.size.toLong()
    entry.compressedSize = bytes.size.toLong()
    val crc = CRC32()
    crc.update(bytes)
    entry.crc = crc.value

    val nameBytes = entryName.toByteArray(Charsets.UTF_8)
    // Local file header fixed size = 30 bytes
    val headerEnd = currentOffset + 30L + nameBytes.size.toLong()
    var extraLen = 0
    if (align4) {
        val mod = (headerEnd % 4L).toInt()
        if (mod != 0) {
            extraLen = 4 - mod
            entry.extra = ByteArray(extraLen)
        }
    }
    zos.putNextEntry(entry)
    zos.write(bytes)
    zos.closeEntry()
    return headerEnd + extraLen.toLong() + bytes.size.toLong()
}

tasks.register("assembleRelease") {
    group = "build"
    description = "Deterministically compiles Android ARM64 release APK using Android SDK 34 tools"

    doLast {
        val sdkDir = resolveSdkDir()
        val androidJar = File(sdkDir, "platforms/android-34/android.jar")
        val aapt2 = resolveAapt2(sdkDir)
        val d8Jar = resolveD8Jar(sdkDir)

        val workDir = layout.buildDirectory.dir("apk_work").get().asFile
        val compiledResZip = File(workDir, "compiled_res.zip")
        val genJavaDir = File(workDir, "gen_java")
        val classesDir = File(workDir, "classes")
        val dexDir = File(workDir, "dex")
        val baseResApk = File(workDir, "base_resources.apk")
        val outApkDir = layout.buildDirectory.dir("outputs/apk/release").get().asFile
        val unsignedApk = File(outApkDir, "tianxia-mobile-v2.0.0-unsigned.apk")

        workDir.deleteRecursively()
        genJavaDir.mkdirs()
        classesDir.mkdirs()
        dexDir.mkdirs()
        outApkDir.mkdirs()

        // 1. Compile & link Android resources + manifest via aapt2
        runTool(listOf(aapt2, "compile", "--dir", file("src/main/res").absolutePath, "-o", compiledResZip.absolutePath))
        runTool(
            listOf(
                aapt2, "link",
                "-I", androidJar.absolutePath,
                "--manifest", file("src/main/AndroidManifest.xml").absolutePath,
                "--java", genJavaDir.absolutePath,
                "-o", baseResApk.absolutePath,
                compiledResZip.absolutePath
            )
        )

        // 2. Compile Java sources + generated R.java against Android SDK 34
        val javaSources = (fileTree("src/main/java") { include("**/*.java") }.files +
            fileTree(genJavaDir) { include("**/*.java") }.files)
            .map { it.absolutePath }
            .sorted()
        val lambdaStubs = File(d8Jar.parentFile.parentFile, "core-lambda-stubs.jar")
        val bootCp = if (lambdaStubs.isFile) {
            "${androidJar.absolutePath}${File.pathSeparator}${lambdaStubs.absolutePath}"
        } else {
            androidJar.absolutePath
        }
        runTool(
            listOf(
                "javac",
                "-source", "8",
                "-target", "8",
                "-Xlint:-options",
                "-encoding", "UTF-8",
                "-bootclasspath", bootCp,
                "-d", classesDir.absolutePath
            ) + javaSources
        )

        // 3. Convert .class files to classes.dex via D8 (portable across Linux, macOS, Windows & Termux)
        val javaBin = File(System.getProperty("java.home"), "bin/java").absolutePath
        val classFiles = fileTree(classesDir) { include("**/*.class") }.files.map { it.absolutePath }.sorted()
        runTool(
            listOf(
                javaBin,
                "-cp",
                d8Jar.absolutePath,
                "com.android.tools.r8.D8",
                "--release",
                "--min-api", "26",
                "--lib", androidJar.absolutePath,
                "--output", dexDir.absolutePath
            ) + classFiles
        )

        // 4. Assemble aligned APK (resources.arsc 4-byte aligned STORED for Android R+ V2/V3 verification)
        if (unsignedApk.exists()) unsignedApk.delete()
        ZipOutputStream(unsignedApk.outputStream().buffered()).use { zos ->
            var offset = 0L
            ZipFile(baseResApk).use { baseZip ->
                val entries = baseZip.entries().asSequence().toList().sortedBy { it.name }
                for (e in entries) {
                    if (e.isDirectory) continue
                    val data = baseZip.getInputStream(e).readBytes()
                    val align = e.name == "resources.arsc"
                    offset = writeStoredEntry(zos, e.name, data, align, offset)
                }
            }

            val dexBytes = File(dexDir, "classes.dex").readBytes()
            offset = writeStoredEntry(zos, "classes.dex", dexBytes, true, offset)

            val assetsRoot = file("src/main/assets")
            if (assetsRoot.exists()) {
                assetsRoot.walkTopDown().filter { it.isFile }.sortedBy { it.relativeTo(assetsRoot).invariantSeparatorsPath }.forEach { f ->
                    val rel = "assets/" + f.relativeTo(assetsRoot).invariantSeparatorsPath
                    offset = writeStoredEntry(zos, rel, f.readBytes(), false, offset)
                }
            }

            val aetherBinPath = project.findProperty("aetherMeshBin")?.toString()
                ?: System.getenv("AETHER_MESH_BIN")
            if (!aetherBinPath.isNullOrBlank() && File(aetherBinPath).isFile) {
                val binBytes = File(aetherBinPath).readBytes()
                offset = writeStoredEntry(zos, "assets/modules/aether_mesh", binBytes, true, offset)
            }
        }
        println("[tianxia-mobile] Built aligned unsigned APK: ${unsignedApk.absolutePath} (${unsignedApk.length()} bytes)")
    }
}
