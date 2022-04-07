mkdir build
cd build
git clone https://github.com/DimensionDev/Maskbook.git
cd Maskbook
git reset --hard 64e16929fd01fa331bec529f6b4628e5232cf228
pnpm install
npx gulp build-ci
mkdir -p ../../extension/src/androidMain/assets/web_extensions/Maskbook
unzip -o ./MaskNetwork.gecko.zip -d ../../extension/src/androidMain/assets/web_extensions/Maskbook