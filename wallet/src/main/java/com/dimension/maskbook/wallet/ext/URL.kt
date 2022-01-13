package com.dimension.maskbook.wallet.ext

import java.net.URLDecoder
import java.net.URLEncoder

fun String.decodeUrl(): String = URLDecoder.decode(this, "UTF-8")
fun String.encodeUrl(): String = URLEncoder.encode(this, "UTF-8").replace("+", "%20")