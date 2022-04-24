solana_web3="$1/src/androidMain/assets/web_extensions/Maskbook/js/npm-ns.solana.web3.js.js"
cross_fetch="$1/src/androidMain/assets/web_extensions/Maskbook/js/npm.cross-fetch.js"

fix_fetch="s/this\.fetch = false;//"

if [[ "$OSTYPE" == "darwin"* ]];then
  sed -i '' "$fix_fetch" "$solana_web3"
  sed -i '' "$fix_fetch" "$cross_fetch"
else
  sed -i'' "$fix_fetch" "$solana_web3"
  sed -i'' "$fix_fetch" "$cross_fetch"
fi
