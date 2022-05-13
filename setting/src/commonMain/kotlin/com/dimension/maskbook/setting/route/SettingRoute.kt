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

import com.dimension.maskbook.common.routeProcessor.annotations.Route

@Suppress("CONST_VAL_WITHOUT_INITIALIZER")
@Route
expect object SettingRoute {
    object SetupPasswordDialog {
        operator fun invoke(action: String): String
    }
    const val LanguageSettings: String
    const val AppearanceSettings: String
    const val DataSourceSettings: String
    const val PaymentPasswordSettings: String
    const val PaymentPasswordSettingsSuccess: String
    const val ChangeBackUpPassword: String
    const val ChangeBackUpPasswordSuccess: String

    object Settings_ChangeEmail {
        const val Settings_ChangeEmail_Setup: String
        object Settings_ChangeEmail_Setup_Code {
            operator fun invoke(email: String): String
        }
        const val Settings_ChangeEmail_Setup_Success: String
        object Settings_ChangeEmail_Change_Code {
            operator fun invoke(email: String): String
        }
        const val Settings_ChangeEmail_Change_New: String
        object Settings_ChangeEmail_Change_New_Code {
            operator fun invoke(email: String): String
        }
        const val Settings_ChangeEmail_Change_Success: String
    }

    object Settings_ChangePhone {
        const val Settings_ChangePhone_Setup: String
        object Settings_ChangePhone_Setup_Code {
            operator fun invoke(phone: String): String
        }
        const val Settings_ChangePhone_Setup_Success: String
        object Settings_ChangePhone_Change_Code {
            operator fun invoke(phone: String): String
        }
        const val Settings_ChangePhone_Change_New: String
        object Settings_ChangePhone_Change_New_Code {
            operator fun invoke(phone: String): String
        }
        const val Settings_ChangePhone_Change_Success: String
    }

    object BackupData {
        const val BackupData_Cloud_Success: String
        const val BackupData_Cloud_Failed: String
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

        const val BackupSelection_NoEmailAndPhone: String
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
                url: String? = null,
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
                url: String? = null,
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
        const val BackupSelection: String
        const val Password: String
        object BackupLocal {
            const val Route: String
            const val Backup: String
            const val Saving: String
            const val Success: String
            const val Failed: String
        }
    }
}
