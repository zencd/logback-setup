# Per-method logging with logback

A principal example showing how to setup <a href="https://logback.qos.ch/manual/index.html">logback</a> to log each method to separate file.

MT-safety not guaranteed. Java 1.8+.

## Goals

- ✓ Separate log file per method
- ✓ Custom pattern per method
- ✓ Custom level threshold per method
- ✓ Custom variables in pattern
- ✓ Ability to change it all in runtime
- ✓ Pure Java config
- ✓ Application of the config to thread worker


## Run

Run `Main.java`. Files `method1.log`, `method1x.log`, `method2.log` and `method2x.log` must appear in the current directory.