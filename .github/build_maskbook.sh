mkdir build
cd build
git clone https://github.com/DimensionDev/Maskbook.git
cd Maskbook
git reset --hard 5de8fb1b27ef3f2cc988ca735a03461a153904e1
pnpm install
npx gulp build-ci
mkdir -p ../../extension/src/androidMain/assets/web_extensions/Maskbook
unzip -o ./MaskNetwork.gecko.zip -d ../../extension/src/androidMain/assets/web_extensions/Maskbook
sh ../../.github/fix_web3j.sh ../../extension