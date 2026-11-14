// ffp: FFP-AVF-MEMORY edge-agent rust
use std::fs::OpenOptions;
use std::io::Write;
use std::path::Path;

pub struct MemoryManifoldStatus {
    pub ksm_present: bool,
    pub zram_present: bool,
    pub tuned: bool,
}

pub fn engage_ksm_and_zram() -> MemoryManifoldStatus {
    let ksm_path = Path::new("/sys/kernel/mm/ksm/run");
    let zram_path = Path::new("/sys/block/zram0/comp_algorithm");

    let ksm_present = ksm_path.exists();
    let zram_present = zram_path.exists();

    let ksm_ok = OpenOptions::new()
        .write(true)
        .open(ksm_path)
        .and_then(|mut f| f.write_all(b"1"))
        .is_ok();
    let _ = OpenOptions::new()
        .write(true)
        .open("/sys/kernel/mm/ksm/sleep_millisecs")
        .and_then(|mut f| f.write_all(b"500"));
    let zram_ok = OpenOptions::new()
        .write(true)
        .open(zram_path)
        .and_then(|mut f| f.write_all(b"zstd"))
        .is_ok();

    MemoryManifoldStatus {
        ksm_present,
        zram_present,
        tuned: ksm_ok || zram_ok,
    }
}
