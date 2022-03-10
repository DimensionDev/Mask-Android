mkdir build
cd build
git clone https://github.com/DimensionDev/Maskbook.git
cd Maskbook
git reset --hard 26da062c6b29d853000a589612ca4032e9451c41
pnpm install
npx gulp build-ci
mkdir -p ../../extension/src/androidMain/assets/web_extensions/Maskbook
unzip -o ./MaskNetwork.gecko.zip -d ../../extension/src/androidMain/assets/web_extensions/Maskbook