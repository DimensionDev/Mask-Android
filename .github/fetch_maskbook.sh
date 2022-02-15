wget -O ./Maskbook.zip https://49534-178806582-gh.circle-artifacts.com/0/MaskNetwork.gecko.zip --header "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3829.0 Safari/537.36 Edg/77.0.197.1"
mkdir -p ./app/src/main/assets/web_extensions/Maskbook
unzip -o ./Maskbook.zip -d ./app/src/main/assets/web_extensions/Maskbook
