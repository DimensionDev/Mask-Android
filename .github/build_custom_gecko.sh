hg clone https://hg.mozilla.org/mozilla-central
hg up -C FIREFOX_BETA_87_BASE
hg import ./customGecko.patch --no-commit
cd mozilla-central
echo 'ac_add_options --enable-application=mobile/android
ac_add_options --enable-artifact-builds
ac_add_options --enable-update-channel=beta
ac_add_options --target=aarch64-linux-android
mk_add_options MOZ_OBJDIR=./objdir-frontend' >mozconfig
./mach bootstrap --application-choice mobile_android_artifact_mode --no-interactive
./mach build
# maven output under ./objdir-frontend/gradle/build/mobile/android/geckoview/maven/