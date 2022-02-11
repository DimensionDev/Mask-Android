mkdir build
cd build
git clone https://github.com/DimensionDev/Maskbook.git
cd Maskbook
git reset --hard 493f5455d8b5dee475ffd7510518a90a9f45aaba
pnpm install
npx gulp build-ci
mkdir -p ../../app/src/main/assets/web_extensions/Maskbook
unzip -o ./MaskNetwork.gecko.zip -d ../../app/src/main/assets/web_extensions/Maskbook