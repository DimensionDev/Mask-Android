mkdir build
cd build
git clone https://github.com/DimensionDev/Maskbook.git
cd Maskbook
git reset --hard c6f9b512d29fcb1d61d8122aaff544bbd8d0d3ef
pnpm install
npx gulp build-ci
mkdir -p ../../app/src/main/assets/web_extensions/Maskbook
unzip -o ./MaskNetwork.gecko.zip -d ../../app/src/main/assets/web_extensions/Maskbook