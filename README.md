# MouseHome v3.8

MouseHome is a small Android TV utility that adds a **HOME action to a wireless mouse**.

It was created for a **TranSpeed 8K618-T** Android TV box after the original remote control was no longer available. The application allows the middle mouse button to keep its normal short-click behavior while adding a HOME command when the button is held for about one second.

## Tested Device

MouseHome was developed and tested on:

- **Device:** TranSpeed 8K618-T
- **Memory / Storage:** 4 GB / 32 GB
- **Operating system:** Android TV OS 12
- **Firmware / UI:** BigdroidOS 2.0.1
- **Android security patch:** February 5, 2022
- **Mouse input device:** `SIGMACHIP Usb Mouse`
- **Mouse input path on the tested device:** `/dev/input/event2`

> **Note:** MouseHome was developed and tested on my personal TranSpeed 8K618-T. Other Android TV devices, firmware versions, or mouse devices may require adjustments.

## What MouseHome Does

MouseHome watches the middle mouse button directly from the Linux input device.

Behavior:

- **Short middle click** — remains unchanged and can continue to act as OK / Select.
- **Long middle press (about 1 second)** — sends Android `KEYCODE_HOME`.

The application uses root access to read the mouse input device and execute the Android HOME command.

## Requirements

The tested configuration requires:

- Android TV box with root access
- Working `su` command
- ADB access for installation and testing
- Wireless or USB mouse with a middle button / clickable wheel

Root access can be checked with:

```bash
adb shell su -c id
```

A successful result should contain something similar to:

```text
uid=0(root)
```

## How It Works

On the tested TranSpeed box, the mouse is available as:

```text
/dev/input/event2
```

The middle mouse button generates `BTN_MIDDLE` events.

MouseHome runs a small root shell process that watches those events:

1. `BTN_MIDDLE DOWN` starts a timer.
2. If the button is still held after approximately one second, MouseHome sends:
   ```text
   KEYCODE_HOME
   ```
3. If the button is released before the timer expires, MouseHome does nothing and the normal middle-click behavior is preserved.

Because `/dev/input/eventX` assignments can differ between devices, other Android TV boxes may require changes to the input-device path.

## Install the APK

Enable Developer Options and USB debugging on the Android TV box.

Connect with ADB:

```bash
adb connect <TV-BOX-IP>:5555
adb devices
```

Install MouseHome:

```bash
adb install MouseHome-v3.8.apk
```

To upgrade an existing installation:

```bash
adb install -r MouseHome-v3.8.apk
```

After installation, open **MouseHome** once.

## START and STOP

MouseHome has two operating states:

- **START / ENABLED** — long middle-button press activates HOME.
- **STOP / DISABLED** — MouseHome does not monitor the mouse.

The selected setting is saved and survives a reboot.

When the application is opened manually after startup, its screen remains visible so the setting can be changed.

## Automatic Startup on BigdroidOS

Normal Android boot receivers were not reliable on the tested TranSpeed firmware. BigdroidOS includes its own **Autostart app** feature, which provides a reliable startup method.

On the TranSpeed 8K618-T, configure BigdroidOS approximately as follows:

1. Open the BigdroidOS **Autostart app** settings.
2. Turn **Autostart app** ON.
3. Set **Sleep on** to OFF.
4. Set **Only if Internet connected** to OFF.
5. Choose **MouseHome** as the application to start.

At the first automatic MouseHome launch after a reboot:

- If the saved state is **START**, MouseHome starts the mouse-monitoring process.
- If the saved state is **STOP**, MouseHome remains disabled.
- MouseHome then returns automatically to the Android HOME screen.

Later manual launches during the same boot remain on the MouseHome screen so START / STOP can be changed.

## Test After Reboot

Reboot the Android TV box:

```bash
adb reboot
```

After Android finishes starting:

1. Wait for BigdroidOS to launch MouseHome automatically.
2. The system should return to the HOME screen.
3. Short-click the mouse wheel and verify its normal behavior.
4. Hold the mouse wheel for about one second.
5. Android should return to HOME.

## Build From Source

### Requirements

The tested Windows build environment used:

- **JDK:** Eclipse Temurin JDK 17
- **Gradle:** 8.7
- **Android Gradle Plugin:** 8.5.2
- **compileSdk:** 35
- **Android SDK:** installed under `C:\Android`

The repository intentionally does **not** include downloaded Gradle binaries, Android SDK files, build output, or `local.properties`.

The included `build.ps1` script can download Gradle 8.7 into the local `.build-tools` directory when needed. That directory is ignored by Git.

### Android SDK

Create a local `local.properties` file if required by your environment.

Example:

```properties
sdk.dir=C\:\\Android
```

Adjust the path for your own Android SDK installation.

### Build

From PowerShell in the project directory:

```powershell
powershell -ExecutionPolicy Bypass -File .\build.ps1
```

The debug APK is normally created at:

```text
app\build\outputs\apk\debug\app-debug.apk
```

## Source Repository vs APK Releases

The GitHub repository contains the source code and build scripts.

Generated APK files are intentionally excluded from the normal Git repository with `.gitignore`.

For users who only want to install MouseHome, the APK should be downloaded from the project's **GitHub Releases** section.

### Download APK

For version 3.8, open the GitHub **Releases** page and select the `v3.8` release.

Download:

```text
MouseHome-v3.8.apk
```

Then install it with ADB as described above.

> The APK may be debug-signed during development. For long-term public distribution and reliable future upgrades, using a consistently signed release APK is recommended.

## Compatibility Notes

MouseHome v3.8 is specifically tested with the TranSpeed 8K618-T configuration described above.

Compatibility with another device is not guaranteed because:

- The device may not have root access.
- `su` behavior may differ.
- The mouse may use a different `/dev/input/eventX` path.
- The middle button may report different Linux input events.
- Android firmware may restrict background or root processes.
- Other firmware may use a different autostart mechanism.

The current implementation is therefore best considered a working solution for the tested TranSpeed / BigdroidOS configuration and a useful starting point for similar rooted Android TV devices.

## Disclaimer

MouseHome was created for personal use and has been tested successfully on my own TranSpeed 8K618-T TV box running Android TV 12 / BigdroidOS 2.0.1.

The software is provided **as-is**. While it works well on my device, different Android TV boxes, firmware versions, mouse devices, or system configurations may behave differently.

Please use MouseHome at your own discretion. I cannot guarantee compatibility with every device or be responsible for issues that may result from its use.

If you try MouseHome on another compatible device, feedback about your experience is welcome.

## Version

Current tested version:

**MouseHome v3.8**
