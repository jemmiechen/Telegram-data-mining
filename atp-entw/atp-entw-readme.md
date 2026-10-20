# Train ATP Telegram Record Analysis & Application (LZB700M / ATP-entw)

[![Version: 1.0.2](https://img.shields.io/badge/Version-1.0.2-blueviolet.svg)](atp-entw-readme.md)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](../README.md)
[![Android: 6.0+](https://img.shields.io/badge/Android-6.0+-brightgreen.svg)](atp-entw-analysis.apk)
[![Windows & macOS](https://img.shields.io/badge/Desktop-Win10%20%7C%20macOS-blue.svg)](atp-entw-Win10-MacOS.zip)
[![Pure C Native](https://img.shields.io/badge/Native%20Engine-Zero%20Dependency-orange.svg)](atp-entw-Win10-MacOS.zip)

A high-performance, platform-independent reverse-engineering, diagnostic, and track circuit analytics suite for **Metro Rail Vehicle Onboard Automatic Train Protection (Siemens LZB700M / ATP)** download archives (`.zip`, `.zdf`, `.err`, `.dat`).

This directory bundles the standalone native execution packages, Android application, sample dataset, and historical EMU ATP telegram archives for rapid deployment and offline diagnostics.

---

## 📦 Packages & Data Archives in this Directory

| File | Type | Size | Description |
|---|---|---|---|
| [`atp-entw-Win10-MacOS.zip`](atp-entw-Win10-MacOS.zip) | Portable App Suite | ~1.4 MB | Standalone native C binaries (No Python or runtime required). Includes `atp_dashboard.exe`, `dat_to_lst.exe`, macOS Universal `ATP-ENTW.app`, and embedded Siemens EDDI manual |
| [`atp-entw-analysis.apk`](atp-entw-analysis.apk) | Android App | ~734 KB | Native Android APK with responsive layout, SAF file picker integration, and offline diagnostics |
| [`0401-1.zip`](0401-1.zip) | Sample Dataset | ~56 KB | Verification dataset containing raw Siemens ATP diagnostic dump files (`0401-01.DAT`, `0401-02.DAT`) |
| [`emu-atp.zip`](emu-atp.zip) | EMU Data Archives | ~1.1 MB | Historical multi-train ATP archives (`emu019_1151011.zip`, `emu028_1151015.zip`, `emu031_1150910.zip`, `emu042_1151006.zip`) |

---

## ⚡ Pure C Native Standalone Engine (Zero External Dependencies)

To achieve maximum portability and instant startup without requiring Python, JRE, or third-party runtime environments, the system features pure C native implementations:

### 1. Standalone HTTP Server & Launcher (`atp_dashboard`)
- **Embedded Web Assets**: The full 900+ KB Web Dashboard is compressed with Gzip down to ~106 KB and embedded directly into the executable as a static byte array.
- **On-the-Fly Decompression**: The embedded micro-server responds with HTTP header `Content-Encoding: gzip`, allowing web browsers (Edge, Chrome, Safari, Firefox) to natively decompress and render the dashboard with zero CPU overhead.
- **Auto-Port & Browser Launch**: Automatically discovers open local ports (`8088`, `8000`, `8888`, `9000`) and launches the default web browser asynchronously.
- **Silent Background Execution on Windows**: Invokes Win32 API `ShowWindow(GetConsoleWindow(), SW_HIDE)` to suppress intrusive console windows on launch.

### 2. Native DAT to LST Converter & Track Circuit Analyzer (`dat_to_lst`)
- **Pure C Siemens Protocol Decoder**: High-speed batch processing of `.DAT` diagnostic dumps into standardized Siemens `.LST` formats.
- **Internal Time Auto-Renaming**: Extracts protocol timestamps (`DD.MM.YY HH:MM:SS`) from hex stream headers and renames output files systematically (e.g., `20080401_014145_0401-01.LST`).
- **B-Telegram Parameter Profiler**: Classifies unique track circuit carrier frequencies, block codes (`GKK / GKKv`), speed limits (`VE / VG`), block lengths (`GKL`), and compiles structured reports into `B-tel-list.txt`.
- **Built-in ZIP Archiver**: Creates standard ZIP archives directly in pure C (Method 0 STORED) with no zlib or external library dependencies.

### 3. Cross-Platform Standalone Binaries
- **Windows 10 / 11 (x86-64)**:
  - `windows/atp_dashboard.exe` (~310 KB)
  - `windows/dat_to_lst.exe` (~217 KB)
- **macOS Apple Silicon (M1/M2/M3/M4)**:
  - `macos/atp_dashboard_arm64` (~151 KB)
  - `macos/dat_to_lst_arm64` (~69 KB)
- **macOS Intel (x86_64)**:
  - `macos/atp_dashboard_x86_64` (~126 KB)
  - `macos/dat_to_lst_x86_64` (~36 KB)
- **macOS Application Bundle**:
  - `ATP-ENTW.app` with universal architecture switching and retina app icons.

---

## 🚀 Key Diagnostic Features

### 1. DAT to LST Batch Converter & B-Telegram Track Circuit Parameter Analyzer (`B-tel-list.txt`)
- **Protocol Timestamp Auto-Renaming**:
  - Automatically parses internal header timestamps inside `.DAT` files (e.g., `2008.04.01 01:41:45`) to name generated `.LST` files systematically (e.g., `20080401_014145_0401-01.LST`).
- **DAISI B-Telegram (Block Telegram) Decoding**:
  - Extracts carrier frequency (`f`: `10.5 kHz`, `12.5 kHz`, `13.5 kHz`, `16.5 kHz`, etc.).
  - Resolves current block section identifier (`GKK`), approach block identifier (`GKKv`), and physical block lengths (`GKL` 1x / 2x scales).
  - Decodes target frequency code (`TFn`), approach frequency code (`TFa`), offset parameters (`TFO`, `FOa`, `FOn`), and track gradient (`Neig` in ‰).
  - Retrieves exit speed limit (`VE`), target speed limit (`VG`), temporary speed restriction zones (`La / VLa`), deceleration target distances (`Z / VZ`), signal aspect conditions (`BHP`), and platform screen door status (`PSD`).
- **Structured Report Generation (`B-tel-list.txt`)**:
  - **Summary Table**: Tabulates every unique track circuit profile along with its frequency, block codes, speed limits, occurrence counts, and percentages.
  - **Individual Profiles**: Detailed physical breakdown of each parameter set, initial file origin, record index, millisecond timestamp, and raw telegram string samples.
  - **Chronological Transition Log**: Sequentially tracks block section transitions (`GKK -> GKKv`) and carrier frequency switches as the train traverses the line.

### 2. Native Binary Reverse-Engineering (No Legacy Tool Dependencies)
- **ZDF Event History Decoding**:
  - Deep parsing of proprietary 614-byte event records.
  - Accurate conversion of 32-bit timestamps with epoch base `1990-01-01 00:00:00 UTC`.
  - Full extraction of operational modes (`SM`, `RM`, `AR`), active master (`ATO`, `Driver`), and trigger-time 10-telegram circular buffer.
- **ERR Hardware Channel Fault Memory Decoding**:
  - Cross-referenced against Siemens EDDI / TB700M technical manuals to decode channel fault codes (Crosstalk, Joint synchronization failure, Fan failure, Static output channel discrepancy, etc.).
  - Complete 22-byte hardware diagnostic registers (`Info Hex`) formatted into structured layouts.
- **Intelligent Co-Occurring Event Merging**:
  - Automatically groups events occurring on the same train within the same second (e.g., Reason 1 `Telegram transmission error` co-triggered with Reason 140 `Emergency brake of train`) to prevent statistical distortion while highlighting safety-critical interventions.

### 3. Interactive Web Dashboard & Siemens Diagnostic Manual
- **Self-Contained Historical Database**:
  - Pre-loaded with operational telegram records and emergency brake intervention events.
- **Official Diagnostic Manual Reference**:
  - Embedded Siemens EDDI diagnostic manual PDF (`DiagnosisManual.pdf`) accessible directly from the app.
- **Mobile & Desktop Responsive Layout**:
  - Optimized for portrait smartphone screens with compact wrapping, adaptive typography, and high-contrast color themes.
  - Instant toggle between Traditional Chinese and English with persistent user preference storage.

---

## 💻 Dual-Platform Portable Package Guide (Windows 10 / macOS)

Extract [`atp-entw-Win10-MacOS.zip`](atp-entw-Win10-MacOS.zip) to any folder. No administrative privileges or setup steps are required.

### 🪟 Windows 10 / 11 Instructions
1. **Launch Dashboard**:
   - Double-click `啟動-ATP分析儀表板(Windows).bat` (or run `windows\atp_dashboard.exe`).
   - The embedded server launches in the background and automatically opens the dashboard in your default browser (Edge / Chrome).
2. **Batch DAT Conversion**:
   - Simply **drag and drop** any `.DAT` file or `.ZIP` archive onto `轉換-DAT轉LST(Windows).bat`.
   - The script automatically converts files, names `.LST` by internal time, and produces `B-tel-list.txt` bundled in a ZIP archive.

### 🍎 macOS Instructions
1. **Launch Dashboard**:
   - Double-click `ATP-ENTW.app` (native macOS application bundle) or execute `啟動-ATP分析儀表板(MacOS).command`.
   - If Gatekeeper prompts on first run, right-click (or Control-click) `ATP-ENTW.app` and choose **Open**.
2. **Batch DAT Conversion**:
   - Double-click `轉換-DAT轉LST(MacOS).command` or execute `./macos/dat_to_lst <file.dat>` in Terminal.

### 📱 Android Mobile / Tablet Instructions
1. Transfer [`atp-entw-analysis.apk`](atp-entw-analysis.apk) to your Android device (Android 6.0+).
2. Install the APK and launch **ATP-ENTW Analysis**.
3. Use the integrated file picker to select and analyze diagnostic archives directly on mobile devices offline.

---

## 🔍 Binary Specifications (Data Layout)

### 1. ZDF File Format (Event History)
- **Header**: 5 bytes (ASCII `ZDF` + 2-byte version number).
- **Record Size**: 614 bytes fixed length.
- **Key Fields**:
  | Offset | Type | Description |
  |---|---|---|
  | `0` | `uint8` | Record status flag |
  | `1..4` | `uint32` LE | Record index |
  | `5..6` | `uint16` LE | Trigger Reason ID (`1` = Telegram Error, `140` = Emergency Brake) |
  | `7..10` | `uint32` LE | Timestamp (seconds since 1990-01-01 00:00:00 UTC) |
  | `14..17` | `uint32` LE | Speed (unit: cm/s, converted to km/h) |
  | `18..21` | `uint32` LE | Maximum travel distance Xmax (cm) |
  | `22..25` | `uint32` LE | Minimum travel distance Xmin (cm) |
  | `29` | `uint8` | Operational mode index (`SM`, `RM`, `AR`) |
  | `31` | `uint8` | Active driver index (`ATO`, `Driver`) |
  | `64..613` | Array | **10-Telegram History Buffer** (55 bytes per telegram entry) |

### 2. ERR File Format (Hardware Channel Fault Memory)
- **Header**: 5 bytes (ASCII `ERR` + version header).
- **Record Size**: 33 bytes fixed length.
- **Key Fields**:
  | Offset | Type | Description |
  |---|---|---|
  | `0` | `uint8` | Validity flag |
  | `1..2` | `uint16` LE | Error Class (`0x0001` = Fatal cut-off, `0x0002` = Warning) |
  | `3..4` | `uint16` LE | Error Number (e.g., 22, 34, 40, 42, 82, 91) |
  | `5..8` | `uint32` LE | Timestamp (seconds since 1990-01-01; 0 indicates unsynchronized boot) |
  | `9..10` | `uint16` LE | Instance identifier |
  | `11..32` | 22 bytes | Hardware diagnostic register (`Info Hex`) |

---

## 📄 License
This project is licensed under the **MIT License**.
