# Simple TV Launcher

A minimal Android TV home-screen launcher prototype. It lists apps and supports D-pad navigation and OK/select.

## Build
The GitHub Actions workflow builds a debug APK on every push to `main`, or when manually started from the Actions tab.

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Notes
- Keep the TV's original launcher enabled until this launcher has been tested.
- This is an early prototype. Test on the target TV before choosing it as the default Home app.
- The app list uses Android's Leanback and standard launcher categories.
