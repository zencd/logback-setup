# Per-method logging with logback

A principal example showing how to setup [logback](https://logback.qos.ch/manual/index.html) to log each Java method to a separate file.

MT-safety and extra efficiency not guaranteed. Java 1.8+. Written by zencd, Aug 2019.

[My article at Habr.com](https://habr.com/ru/post/463601/) (in Russian).

## Goals

- ✓ Separate log file per method
- ✓ Custom pattern per method
- ✓ Custom level threshold per method
- ✓ Custom variables in pattern
- ✓ Applicable to thread workers
- ✓ Reconfigurable in runtime
- ✓ Pure Java config

## Run

Run `Main.java`.

Expect files `method1.log`, `method1x.log`, `method2.log` and `method2x.log` in the current directory.
