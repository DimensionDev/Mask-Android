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
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.util.Base64
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.common.ext.encodeBase64
import com.dimension.maskbook.persona.datasource.DbPersonaDataSource
import com.dimension.maskbook.persona.export.model.PersonaQrCode
import com.dimension.maskbook.persona.repository.IPersonaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class DownloadQrCodeViewModel(
    private val personaRepository: IPersonaRepository,
    private val dataSource: DbPersonaDataSource,
) : ViewModel() {
    private val _filePickerLaunched = MutableStateFlow(false)
    val filePickerLaunched = _filePickerLaunched.asStateIn(viewModelScope)

    val personaQrCode = personaRepository.currentPersona.mapNotNull {
        it?.let { persona ->
            dataSource.getPersonaQrCode(persona.identifier)
        }
    }.asStateIn(viewModelScope, null)

    fun pickFile() {
        _filePickerLaunched.value = true
    }

    suspend fun save(
        uri: Uri,
        context: Context,
        pdfContent: View,
        height: Int,
        width: Int
    ) = withContext(Dispatchers.IO) {
        runCatching {
            personaQrCode.value?.let {
                // generate PDF
                val pdfDocument = PdfDocument()
                val myPageInfo = PageInfo.Builder(width, height, 1).create()
                val myPage = pdfDocument.startPage(myPageInfo)
                pdfContent.draw(myPage.canvas)
                pdfDocument.finishPage(myPage)
                context.contentResolver.openOutputStream(uri)?.use {
                    pdfDocument.writeTo(it)
                }
                pdfDocument.close()
            } ?: throw Error("PersonaQrCode info is null")
        }
    }

    fun generateQrCodeStr(qrCode: PersonaQrCode): String {
        var str = if (qrCode.identityWords.isNotEmpty()) {
            "mask://persona/identity/${qrCode.identityWords.encodeBase64(Base64.NO_WRAP)}"
        } else {
            "mask://persona/privatekey/${qrCode.privateKeyBase64}"
        }
        if (qrCode.nickName.isNotEmpty()) {
            str += "?nickname=${qrCode.nickName}"
        }
        return str
    }
}
