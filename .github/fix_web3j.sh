sed_option=
if [[ "$OSTYPE" == "darwin"* ]]; then
  sed_option="-i ''"
fi

sed "$sed_option" "s/this\.fetch = false;//" "$1/src/androidMain/assets/web_extensions/Maskbook/js/npm-ns.solana.web3.js.js"