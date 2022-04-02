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
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.common.ext.decodeBase64
import com.dimension.maskbook.persona.datasource.DbPersonaDataSource
import com.dimension.maskbook.persona.repository.IPersonaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
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
        _uri.value = null
        _filePickerLaunched.value = true
    }

    private val _uri = MutableStateFlow<Uri?>(null)
    val uri = _uri.asStateIn(viewModelScope)
    val renderPdfView = combine(personaQrCode, uri) { code, uri ->
        code != null && uri != null
    }.asStateIn(viewModelScope, false)

    fun setUri(uri: Uri) {
        _uri.value = uri
    }

    suspend fun save(
        context: Context,
        pdfContent: View,
    ) = withContext(Dispatchers.IO) {
        runCatching {
            personaQrCode.value?.let {
                // generate PDF
                val pdfDocument = PdfDocument()
                val myPageInfo = PageInfo.Builder(pdfContent.width, pdfContent.height, 1).create()
                val myPage = pdfDocument.startPage(myPageInfo)
                pdfContent.draw(myPage.canvas)
                pdfDocument.finishPage(myPage)
                context.contentResolver.openOutputStream(uri.value ?: throw Error("uri is null"))?.use {
                    pdfDocument.writeTo(it)
                }
                pdfDocument.close()
            } ?: throw Error("PersonaQrCode info is null")
        }
    }
}
