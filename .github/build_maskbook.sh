mkdir build
cd build
git clone https://github.com/DimensionDev/Maskbook.git
cd Maskbook
git reset --hard 0f44eb2702b5a4adbba8d00c68b3bde03f246e73
pnpm install
npx gulp build-ci
mkdir -p ../../app/src/main/assets/web_extensions/Maskbook
unzip -o ./MaskNetwork.gecko.zip -d ../../app/src/main/assets/web_extensions/Maskbook