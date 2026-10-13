# OffScroll — NativeStarter edition

Offline, Reels-style vertical video player for Android (Kotlin · Jetpack Compose · Media3 · Room).
This is the AI Studio **OffScroll** project merged into the **NativeStarter** layout, with a GitHub Actions
pipeline that builds installable APKs on every push.

## Get an APK from GitHub (no Android Studio needed)

1. Create a new GitHub repository and push this folder to it (the folder contents must be at the repo root).
2. Open the **Actions** tab → **Android CI** runs automatically on every push / pull request.
3. When it finishes, open the run and download **OffScroll-debug-apk** (or **OffScroll-release-apk**) from *Artifacts*.
4. Install the `.apk` on your phone (allow "Install unknown apps").

To publish a downloadable Release page, push a tag: `git tag v1.0.0 && git push origin v1.0.0`.

## Signed releases (optional)

Without secrets, the release APK is signed with the debug key (fine for sideloading, not for Play Store).
To sign with your own key:

```bash
keytool -genkeypair -v -keystore my-upload-key.jks -alias upload \
        -keyalg RSA -keysize 2048 -validity 10000
base64 -w0 my-upload-key.jks   # copy the output
```

Add these in **Settings → Secrets and variables → Actions**:

| Secret | Value |
|---|---|
| `KEYSTORE_BASE64` | base64 output from above |
| `STORE_PASSWORD` | keystore password |
| `KEY_ALIAS` | `upload` (or your alias) |
| `KEY_PASSWORD` | key password (defaults to the store password) |

Never commit the `.jks` file (it is git-ignored).

## Build locally

Open the folder in Android Studio (latest stable) and press Run, or with Gradle 9.3.1 installed:

```bash
gradle assembleDebug     # app/build/outputs/apk/debug/
```

## Project notes

- `applicationId`: `com.aistudio.offscroll.vfeed` (change in `app/build.gradle.kts` to make it your own).
- Min SDK 24 · Target/Compile SDK 36.
- Gradle wrapper jar is not included; CI installs Gradle 9.3.1 directly. Run `gradle wrapper` once locally if you want `./gradlew`.
- Firebase / google-services were removed (unused), so no `google-services.json` is required.
