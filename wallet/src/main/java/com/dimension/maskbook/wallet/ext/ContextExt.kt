package com.dimension.maskbook.wallet.ext

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.dimension.maskbook.wallet.R

fun Context.shareText(text: String) {
    startActivity(Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }.let {
        Intent.createChooser(it, text)
    })
}

fun Context.copyText(text: String, label: String = "", hint: Boolean = true) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    clipboard.setPrimaryClip(
        ClipData.newPlainText(
            label,
            text
        )
    )
    if (hint)
        Toast.makeText(this, getString(R.string.common_alert_copied_to_clipboard_title), Toast.LENGTH_SHORT).show()
}