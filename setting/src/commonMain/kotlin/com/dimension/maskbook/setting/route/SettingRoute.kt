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
package com.dimension.maskbook.setting.route

import com.dimension.maskbook.common.routeProcessor.Route

@Route
expect object SettingRoute {
    val SetupPasswordDialog: String
    val LanguageSettings: String
    val AppearanceSettings: String
    val DataSourceSettings: String
    val PaymentPasswordSettings: String
    val PaymentPasswordSettingsSuccess: String
    val ChangeBackUpPassword: String
    val ChangeBackUpPasswordSuccess: String

    object Settings_ChangeEmail {
        val Settings_ChangeEmail_Setup: String
        object Settings_ChangeEmail_Setup_Code {
            operator fun invoke(email: String): String
        }
        val Settings_ChangeEmail_Setup_Success: String
        object Settings_ChangeEmail_Change_Code {
            operator fun invoke(email: String): String
        }
        val Settings_ChangeEmail_Change_New: String
        object Settings_ChangeEmail_Change_New_Code {
            operator fun invoke(email: String): String
        }
        val Settings_ChangeEmail_Change_Success: String
    }

    object Settings_ChangePhone {
        val Settings_ChangePhone_Setup: String
        object Settings_ChangePhone_Setup_Code {
            operator fun invoke(phone: String): String
        }
        val Settings_ChangePhone_Setup_Success: String
        object Settings_ChangePhone_Change_Code {
            operator fun invoke(phone: String): String
        }
        val Settings_ChangePhone_Change_New: String
        object Settings_ChangePhone_Change_New_Code {
            operator fun invoke(phone: String): String
        }
        val Settings_ChangePhone_Change_Success: String
    }

    object BackupData {
        val BackupData_Cloud_Success: String
        val BackupData_Cloud_Failed: String
        object BackupData_BackupCloud {
            operator fun invoke(type: String, value: String, code: String): String
        }
        object BackupData_BackupCloud_Execute {
            operator fun invoke(
                withWallet: Boolean,
                type: String,
                value: String,
                code: String
            ): String
        }

        val BackupSelection_NoEmailAndPhone: String
        object BackupData_BackupMerge_Confirm_Success {
            operator fun invoke(
                type: String,
                value: String,
                code: String
            ): String
        }

        object BackupData_BackupMerge_Confirm {
            operator fun invoke(
                type: String,
                value: String,
                code: String,
                download_url: String? = null,
                size: Long? = null,
                uploaded_at: Long? = null,
                abstract: String? = null,
            ): String
        }
        object BackupData_BackupMerge {
            operator fun invoke(
                type: String,
                value: String,
                code: String,
                download_url: String? = null,
                size: Long? = null,
                uploaded_at: Long? = null,
                abstract: String? = null,
            ): String
        }
        object BackupSelection_Email {
            operator fun invoke(email: String): String
        }
        object BackupSelection_Phone {
            operator fun invoke(phone: String): String
        }
        val BackupSelection: String
        val Password: String
        val BackupLocalHost: String
        val BackupLocalFailure: String
        val BackupLocalSuccess: String
    }
}
