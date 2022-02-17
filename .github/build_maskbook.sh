mkdir build
cd build
git clone https://github.com/DimensionDev/Maskbook.git
cd Maskbook
git reset --hard 215b391508549d24f97913fcac30c99f0da6f97e
pnpm install
npx gulp build-ci
mkdir -p ../../app/src/main/assets/web_extensions/Maskbook
unzip -o ./MaskNetwork.gecko.zip -d ../../app/src/main/assets/web_extensions/Maskbook