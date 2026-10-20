# LOG to LGD Converter

Telegram Data Mining for ATO Gateway Computer conversion tool.

## Ubuntu CLI

Build:

```bash
gcc -O2 -Wall -Wextra -o dvr dvr.c
```

Convert:

```bash
./dvr
```

Then enter only the last two digits:

```text
55
```

The converter will look for `S1155.log` first, then `S1155.LOG`.

Default output:

```text
1155.lgd
```

You can also provide the two digits or full file name as an argument:

```bash
./dvr 55
./dvr S1155.log 1155.lgd
```

## Android APK

Debug APK:

```text
dist/log-to-lgd-debug.apk
```

Build:

```bash
JAVA_HOME=/home/e6420/download/.tools/jdk-17.0.19+10 \
GRADLE_USER_HOME=/home/e6420/download/.gradle-home \
ANDROID_HOME=/home/e6420/download/.tools/android-sdk \
ANDROID_SDK_ROOT=/home/e6420/download/.tools/android-sdk \
PATH=/home/e6420/download/.tools/jdk-17.0.19+10/bin:/home/e6420/download/.tools/gradle-8.7/bin:/home/e6420/download/.tools/android-sdk/cmdline-tools/latest/bin:$PATH \
gradle assembleDebug
```

The APK uses Android's file picker:

1. Select a `.log` file, such as `S1155.log`.
2. Tap convert and save.
3. The suggested output name is `1155.lgd`.

---

## Train ATP Telegram Record Analysis (atp-entw)

Siemens LZB700M / ATP-entw telegram and event diagnostics tools and archives are located in the [atp-entw](atp-entw/atp-entw-readme.md) directory:

- **Portable Desktop Package**: [`atp-entw/atp-entw-Win10-MacOS.zip`](atp-entw/atp-entw-Win10-MacOS.zip) (Windows 10/11 & macOS native standalone binaries)
- **Android APK**: [`atp-entw/atp-entw-analysis.apk`](atp-entw/atp-entw-analysis.apk) (Native Android diagnostic application)
- **Sample Dataset**: [`atp-entw/0401-1.zip`](atp-entw/0401-1.zip) (Raw Siemens DAT sample dumps)
- **Historical EMU Data**: [`atp-entw/emu-atp.zip`](atp-entw/emu-atp.zip) (EMU ATP archives)
- **Detailed Documentation**: See [atp-entw/atp-entw-readme.md](atp-entw/atp-entw-readme.md)

