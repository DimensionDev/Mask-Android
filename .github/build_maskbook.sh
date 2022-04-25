mkdir build
cd build
git clone https://github.com/DimensionDev/Maskbook.git
cd Maskbook
git reset --hard 361cd64d69e6bea94b1f7a8be2f5937a1fa2d278
pnpm install
npx gulp build-ci
mkdir -p ../../extension/src/androidMain/assets/web_extensions/Maskbook
unzip -o ./MaskNetwork.gecko.zip -d ../../extension/src/androidMain/assets/web_extensions/Maskbook