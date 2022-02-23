mkdir build
cd build
git clone https://github.com/DimensionDev/Maskbook.git
cd Maskbook
git reset --hard fee68cb4d58ab3b6417a17a73dc4d666463e1841
pnpm install
npx gulp build-ci
mkdir -p ../../app/src/main/assets/web_extensions/Maskbook
unzip -o ./MaskNetwork.gecko.zip -d ../../app/src/main/assets/web_extensions/Maskbook