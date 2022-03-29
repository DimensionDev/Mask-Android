wget -O ./Maskbook.zip https://dl.circleci.com/private/output/job/7d79bb64-a663-49f1-acd5-670d5e0f0461/artifacts/0/MaskNetwork.gecko.zip --header "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3829.0 Safari/537.36 Edg/77.0.197.1"
mkdir -p ./extension/src/androidMain/assets/web_extensions/Maskbook
unzip -o ./Maskbook.zip -d ./extension/src/androidMain/assets/web_extensions/Maskbook
rm ./Maskbook.zip