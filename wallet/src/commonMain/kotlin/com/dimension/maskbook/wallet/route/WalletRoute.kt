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

@Suppress("CONST_VAL_WITHOUT_INITIALIZER")
@Route
expect object WalletRoute {
    object SendTokenConfirm {
        operator fun invoke(data: String): String
    }

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

    const val SwitchWalletAdd: String
    const val SwitchWalletAddWalletConnect: String

    object WalletNetworkSwitch {
        operator fun invoke(target: String): String
    }

    object WalletSwitchEditModal {
        operator fun invoke(id: String): String
    }

    const val WalletNetworkSwitchWarningDialog: String
    const val SwitchWallet: String

    const val WalletBalancesMenu: String

    object WalletManagementDeleteDialog {
        operator fun invoke(id: String): String
    }

    const val WalletManagementBackup: String
    const val WalletManagementTransactionHistory: String

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

    const val MultiChainWalletDialog: String

    object CreateWallet {
        operator fun invoke(wallet: String): String
    }

    object ImportWallet {
        operator fun invoke(wallet: String): String
    }

    object SendTokenScene {
        operator fun invoke(tokenAddress: String?): String
    }

    object UnlockWalletDialog {
        operator fun invoke(target: String): String
    }

    object Register {
        const val Init: String

        object CreateIdentity {
            operator fun invoke(personaName: String): String
        }

        const val WelcomeCreatePersona: String
        const val CreatePersona: String

        object Recovery {
            const val Home: String

            object RemoteBackupRecovery {
                const val RemoteBackupRecovery_NoBackup: String

                object RemoteBackupRecovery_Email_Code {
                    operator fun invoke(email: String): String
                }

                const val RemoteBackupRecovery_Email: String

                object RemoteBackupRecovery_Phone_Code {
                    operator fun invoke(phone: String): String
                }

                const val RemoteBackupRecovery_Phone: String
            }

            object LocalBackup {
                object RemoteBackupRecovery_RecoveryLocal {
                    operator fun invoke(uri: String): String
                }
            }

            const val IdentityPersona: String
            object Identity {
                operator fun invoke(name: String): String
            }
            const val PrivateKey: String
            const val Complected: String
            object AlreadyExists {
                operator fun invoke(restoreFrom: String): String
            }
        }
    }
}
