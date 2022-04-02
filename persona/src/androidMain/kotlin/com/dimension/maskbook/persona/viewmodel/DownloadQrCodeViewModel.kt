/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.persona.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.common.ext.decodeBase64
import com.dimension.maskbook.common.ext.encodeBase64
import com.dimension.maskbook.common.ui.barcode.barcodeBitmap
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.datasource.DbPersonaDataSource
import com.dimension.maskbook.persona.export.model.PersonaQrCode
import com.dimension.maskbook.persona.repository.IPersonaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class DownloadQrCodeViewModel(
    private val idType: IdType,
    private val idBase64: String,
    private val personaRepository: IPersonaRepository,
    private val dataSource: DbPersonaDataSource,
) : ViewModel() {
    enum class IdType {
        ID,
        Mnemonic
    }

    private val _filePickerLaunched = MutableStateFlow(false)
    val filePickerLaunched = _filePickerLaunched.asStateIn(viewModelScope)

    val personaQrCode = personaRepository.personaList.mapNotNull {
        it.mapNotNull {
            dataSource.getPersonaQrCode(it.identifier)
        }.firstOrNull {
            when (idType) {
                IdType.ID -> it.identifier == idBase64.decodeBase64()
                IdType.Mnemonic -> it.identityWords == idBase64.decodeBase64()
            }
        }
    }.asStateIn(viewModelScope, null)

    fun launchFilePicker() {
        _filePickerLaunched.value = true
    }

    suspend fun save(
        context: Context,
        uri: Uri,
    ) = withContext(Dispatchers.IO) {
        runCatching {
            personaQrCode.value?.let {
                val pdfDocument = PdfDocument()
                // A4 paper size is 792*1123
                val pageInfo = PageInfo.Builder(792, 1123, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                drawPdf(info = it, context = context, canvas = page.canvas)
                pdfDocument.finishPage(page)
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    pdfDocument.writeTo(os)
                }
                pdfDocument.close()
            } ?: throw Error("PersonaQrCode info is null")
        }
    }

    private fun PersonaQrCode.qrCodeStr(): String {
        var str = if (identityWords.isNotEmpty()) {
            "mask://persona/identity/${identityWords.encodeBase64(Base64.NO_WRAP)}"
        } else {
            "mask://persona/privatekey/$privateKeyBase64"
        }
        if (nickName.isNotEmpty()) {
            str += "?nickname=$nickName"
        }
        return str
    }

    // to many bugs when i use compose to generate pdf so I use canvas instead
    // ugly but works fine
    private fun drawPdf(info: PersonaQrCode, context: Context, canvas: Canvas) {
        // draw bg
        val primaryColor = Color.parseColor("#1C68F3")
        val bitmapPaint = Paint()
        val bgBitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.bg_pdf),
            740,
            1040,
            false
        )
        canvas.drawBitmap(bgBitmap, 21f, 28f, bitmapPaint)
        // draw solid bg
        bitmapPaint.color = Color.WHITE
        canvas.drawRect(172f, 122f, 172 + 450f, 122 + 480f, bitmapPaint)

        // draw Logo
        bitmapPaint.reset()
        val logoBitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.pdf_logo),
            64,
            64,
            false
        )
        canvas.drawBitmap(logoBitmap, 192f, 142f, bitmapPaint)
        //draw Text
        val textPaint = Paint()
        textPaint.color = Color.BLACK
        textPaint.textSize = 20f
        textPaint.typeface = Typeface.DEFAULT_BOLD
        //nick name y is text baseline position
        canvas.drawText(info.nickName, 264f, 142f + textPaint.textBaseLine(), textPaint)
        //identity code
        textPaint.textSize = 18f
        canvas.drawText(
            context.getString(R.string.scene_persona_download_qr_code_identity_code),
            336f,
            651f + textPaint.textBaseLine(),
            textPaint
        )
        //id
        textPaint.reset()
        textPaint.color = Color.BLACK
        textPaint.textSize = 12f
        canvas.drawText(info.identifier, 264f, 180f + textPaint.textBaseLine(), textPaint)
        // tips
        bitmapPaint.reset()
        val tipsBitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(context.resources, R.drawable.pdf_tips),
            24,
            24,
            false
        )
        canvas.drawBitmap(tipsBitmap, 196f, 959f, bitmapPaint)
        textPaint.reset()
        textPaint.color = primaryColor
        textPaint.textSize = 13f
        val tips = context.getString(R.string.scene_persona_download_qr_code_tips)
        val offsetY = textPaint.fontMetrics.bottom - textPaint.fontMetrics.top
        tips.split("\n").forEachIndexed { index, s ->
            canvas.drawText(s, 228f, 941f + textPaint.textBaseLine() + index * offsetY, textPaint)
        }

        // draw barcode
        bitmapPaint.reset()
        canvas.drawBitmap(
            barcodeBitmap(info.qrCodeStr(), 320, 320) ?: throw Error("Can't generate qr code!"),
            236f,
            256f,
            bitmapPaint
        )

        bitmapPaint.reset()
        bitmapPaint.color = Color.GRAY
        canvas.drawLine(196f, 626f, 196 + 402f, 626.5f, bitmapPaint)
        val wordsStartLeft = 196f
        val wordsStartTop = 693f
        val wordsWidth = 126f
        val wordsHeight = 48f
        val spacing = 12f
        bitmapPaint.reset()
        bitmapPaint.color = primaryColor
        bitmapPaint.alpha = 10
        textPaint.reset()
        textPaint.color = primaryColor
        textPaint.textSize = 16f
        info.identityWords.takeIf { it.isNotEmpty() }?.split(" ")?.withIndex()
            ?.groupBy { it.index / 3 }
            ?.map { it.value.map { it.value } }
            ?.forEachIndexed { row, words ->
                words.forEachIndexed { column, word ->
                    val left = wordsStartLeft + column * wordsWidth + column * spacing
                    val top = wordsStartTop + row * wordsHeight + row * spacing
                    canvas.drawRoundRect(
                        left,
                        top,
                        left + wordsWidth,
                        top + wordsHeight,
                        8f,
                        8f,
                        bitmapPaint
                    )
                    canvas.drawText(
                        "${(column + row * 3) + 1} $word",
                        left + 28f,
                        top + 12f + textPaint.textBaseLine(),
                        textPaint
                    )
                }
            }
    }

    private fun Paint.textBaseLine() = fontMetrics.descent - fontMetrics.top
}
