// ffp: FFP-AVF-CORE concurrent-daemon rust
mod memory_manifold;
mod mission_manifold;

use std::env;

const VERSION: &str = "2.1.0";

fn status_json() -> String {
    let mem = memory_manifold::engage_ksm_and_zram();
    let missions = mission_manifold::active_missions().join(",");
    format!(
        "{{\"name\":\"avf_nomad_core\",\"version\":\"{}\",\"ksm_present\":{},\"zram_present\":{},\"tuned\":{},\"missions\":\"{}\"}}",
        VERSION, mem.ksm_present, mem.zram_present, mem.tuned, missions
    )
}

fn main() {
    let args: Vec<String> = env::args().skip(1).collect();
    if args.iter().any(|a| a == "--version" || a == "-V") {
        println!("avf_nomad_core v{}", VERSION);
        return;
    }
    println!("{}", status_json());
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_status_json_well_formed() {
        let s = status_json();
        assert!(s.contains("\"avf_nomad_core\""));
        assert!(s.contains("AetherMesh-Telemetry-Sync"));
    }
}
