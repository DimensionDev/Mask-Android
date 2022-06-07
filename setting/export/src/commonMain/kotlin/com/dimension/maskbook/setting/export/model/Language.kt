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
package com.dimension.maskbook.setting.export.model

enum class Language(val value: String) {
    auto("auto"),
    enUS("en-US"),
    zhCN("zh-CN"),
    zhTW("zh-TW"),
    koKR("ko-KR"),
    jaJP("ja-JP"),
    esES("es-ES"),
    faIR("fa-IR"),
    itIT("it-IT"),
    ruRU("ru-RU"),
    frFR("fr-FR");

    companion object {
        fun parse(value: String) = when (value) {
            auto.value -> auto
            enUS.value -> enUS
            zhCN.value -> zhCN
            zhTW.value -> zhTW
            koKR.value -> koKR
            jaJP.value -> jaJP
            esES.value -> esES
            faIR.value -> faIR
            itIT.value -> itIT
            ruRU.value -> ruRU
            frFR.value -> frFR
            else -> auto
        }
    }
}
