mkdir build
cd build
git clone https://github.com/DimensionDev/Maskbook.git
cd Maskbook
git reset --hard ece338555828d3b55d6fe08609e9173146dd7c48
pnpm install
npx gulp build-ci
mkdir -p ../../extension/src/androidMain/assets/web_extensions/Maskbook
unzip -o ./MaskNetwork.gecko.zip -d ../../extension/src/androidMain/assets/web_extensions/Maskbook