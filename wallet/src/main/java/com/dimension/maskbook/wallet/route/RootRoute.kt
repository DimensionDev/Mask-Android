package com.dimension.maskbook.wallet.route

import moe.tlaster.kroute.processor.Route

@Route(
    className = "Root"
)
interface RootRoute {
    interface Register {
        val Init: String
        fun CreateIdentity(personaName: String): String
        val WelcomeCreatePersona: String
        val CreatePersona: String

        interface Recovery {
            val Home: String

            interface RemoteBackupRecovery {
                val RemoteBackupRecovery_NoBackup: String
                fun RemoteBackupRecovery_Email_Code(email: String): String
                val RemoteBackupRecovery_Email: String
                fun RemoteBackupRecovery_Phone_Code(phone: String): String
                val RemoteBackupRecovery_Phone: String
            }

            interface LocalBackup {
                fun RemoteBackupRecovery_RecoveryLocal(uri: String): String
                val LocalBackup_PickFile: String
            }

            val Identity: String
            val PrivateKey: String
            val Complected: String
        }
    }

    interface Main {
        val Home: String
        val PluginSettings: String
        val Logout: String
        fun BackUpPassword(target: String): String
        val MarketTrendSettings: String
        val PersonaMenu: String
        val SwitchPersona: String
        fun RenamePersona(personaId: String): String
        val ExportPrivateKey: String
        fun SelectPlatform(personaId: String): String
        fun ConnectSocial(personaId: String, platform: String): String
        fun DisconnectSocial(
            personaId: String,
            platform: String,
            socialId: String,
            personaName: String? = null,
            socialName: String? = null,
        )

        fun CollectibleDetail(id: String): String
        fun WalletQrcode(name: String): String
        fun TokenDetail(id: String): String
        val SwitchWalletAdd: String
        val SwitchWalletAddWalletConnect: String
        fun WalletNetworkSwitch(target: String): String
        val WalletNetworkSwitchWarningDialog: String
        val SwitchWallet: String
        fun WalletSwitchModal(id: String): String
        val WalletBalancesMenu: String
        fun WalletManagementDeleteDialog(id: String): String
        val WalletManagementBackup: String
        val WalletManagementTransactionHistory: String
        fun WalletManagementRename(id: String): String
        fun WalletIntroHostLegal(type: String): String
        fun WalletIntroHostPassword(type: String): String
        fun WalletIntroHostFaceId(type: String): String
        fun WalletIntroHostFaceIdEnableSuccess(type: String): String
        fun WalletIntroHostTouchId(type: String): String
        fun WalletIntroHostTouchIdEnableSuccess(type: String): String
        fun CreateOrImportWallet(type: String): String
        val MultiChainWalletDialog: String
        fun CreateWallet(wallet: String): String
        fun ImportWallet(wallet: String): String
        fun SendTokenScene(token: String): String
        fun UnlockWalletDialog(target: String): String

        val SetupPasswordDialog: String
        val LanguageSettings: String
        val AppearanceSettings: String
        val DataSourceSettings: String
        val PaymentPasswordSettings: String
        val PaymentPasswordSettingsSuccess: String
        val ChangeBackUpPassword: String
        val ChangeBackUpPasswordSuccess: String

        interface Settings_ChangeEmail {
            val Settings_ChangeEmail_Setup: String
            fun Settings_ChangeEmail_Setup_Code(email: String): String
            val Settings_ChangeEmail_Setup_Success: String
            fun Settings_ChangeEmail_Change_Code(email: String): String
            val Settings_ChangeEmail_Change_New: String
            fun Settings_ChangeEmail_Change_New_Code(email: String): String
            val Settings_ChangeEmail_Change_Success: String
        }

        interface Settings_ChangePhone {
            val Settings_ChangePhone_Setup: String
            fun Settings_ChangePhone_Setup_Code(phone: String): String
            val Settings_ChangePhone_Setup_Success: String
            fun Settings_ChangePhone_Change_Code(phone: String): String
            val Settings_ChangePhone_Change_New: String
            fun Settings_ChangePhone_Change_New_Code(phone: String): String
            val Settings_ChangePhone_Change_Success: String
        }

        interface BackupData {
            val BackupData_Cloud_Success: String
            val BackupData_Cloud_Failed: String
            fun BackupData_BackupCloud(type: String, value: String, code: String)
            fun BackupData_BackupCloud_Execute(
                withWallet: Boolean,
                type: String,
                value: String,
                code: String
            ): String

            val BackupSelection_NoEmailAndPhone: String
            fun BackupData_BackupMerge_Confirm_Success(
                type: String,
                value: String,
                code: String
            ): String

            fun BackupData_BackupMerge_Confirm(
                type: String,
                value: String,
                code: String,
                download_url: String? = null,
                size: Long? = null,
                uploaded_at: Long? = null,
                abstract: String? = null,
            ): String
            fun BackupData_BackupMerge(
                type: String,
                value: String,
                code: String,
                download_url: String? = null,
                size: Long? = null,
                uploaded_at: Long? = null,
                abstract: String? = null,
            ): String
            fun BackupSelection_Email(email: String): String
            fun BackupSelection_Phone(phone: String): String
            val BackupSelection: String
            val Password: String
            val BackupLocalHost: String
            val BackupLocalFailure: String
            val BackupLocalSuccess: String
        }
    }
}