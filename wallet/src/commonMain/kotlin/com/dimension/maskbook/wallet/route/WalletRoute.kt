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
package com.dimension.maskbook.wallet.route

import com.dimension.maskbook.common.routeProcessor.annotations.Route

@Route
expect object WalletRoute {
    object BackUpPassword {
        operator fun invoke(target: String): String
    }
    object CollectibleDetail {
        operator fun invoke(id: String): String
    }

    object WalletQrcode {
        operator fun invoke(name: String): String
    }

    object TokenDetail {
        operator fun invoke(id: String): String
    }

    val SwitchWalletAdd: String
    val SwitchWalletAddWalletConnect: String

    object WalletNetworkSwitch {
        operator fun invoke(target: String): String
    }

    object WalletSwitchEditModal {
        operator fun invoke(id: String): String
    }

    val WalletNetworkSwitchWarningDialog: String
    val SwitchWallet: String

    val WalletBalancesMenu: String

    object WalletManagementDeleteDialog {
        operator fun invoke(id: String): String
    }

    val WalletManagementBackup: String
    val WalletManagementTransactionHistory: String

    object WalletManagementRename {
        operator fun invoke(id: String, name: String): String
    }

    object WalletIntroHostLegal {
        operator fun invoke(type: String): String
    }

    object WalletIntroHostPassword {
        operator fun invoke(type: String): String
    }

    object WalletIntroHostFaceId {
        operator fun invoke(type: String): String
    }

    object WalletIntroHostFaceIdEnableSuccess {
        operator fun invoke(type: String): String
    }

    object WalletIntroHostTouchId {
        operator fun invoke(type: String): String
    }

    object WalletIntroHostTouchIdEnableSuccess {
        operator fun invoke(type: String): String
    }

    object CreateOrImportWallet {
        operator fun invoke(type: String): String
    }

    val MultiChainWalletDialog: String

    object CreateWallet {
        operator fun invoke(wallet: String): String
    }

    object ImportWallet {
        operator fun invoke(wallet: String): String
    }

    object SendTokenScene {
        operator fun invoke(tradableId: String?): String
    }

    object UnlockWalletDialog {
        operator fun invoke(target: String): String
    }

    object Register {
        val Init: String

        object CreateIdentity {
            operator fun invoke(personaName: String): String
        }

        val WelcomeCreatePersona: String
        val CreatePersona: String

        object Recovery {
            val Home: String

            object RemoteBackupRecovery {
                val RemoteBackupRecovery_NoBackup: String

                object RemoteBackupRecovery_Email_Code {
                    operator fun invoke(email: String): String
                }

                val RemoteBackupRecovery_Email: String

                object RemoteBackupRecovery_Phone_Code {
                    operator fun invoke(phone: String): String
                }

                val RemoteBackupRecovery_Phone: String
            }

            object LocalBackup {
                object RemoteBackupRecovery_RecoveryLocal {
                    operator fun invoke(uri: String): String
                }

                val LocalBackup_PickFile: String
            }

            val IdentityPersona: String
            object Identity {
                operator fun invoke(name: String): String
            }
            val PrivateKey: String
            val Complected: String
        }
    }
}
