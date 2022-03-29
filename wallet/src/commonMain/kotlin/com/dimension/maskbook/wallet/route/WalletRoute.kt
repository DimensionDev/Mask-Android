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
        const val Route: String
        object Pharse {
            operator fun invoke(wallet: String): String
        }
        object Verify {
            operator fun invoke(wallet: String): String
        }
        const val Confirm: String
    }

    object ImportWallet {
        object Import {
            operator fun invoke(wallet: String): String
        }
        object Mnemonic {
            operator fun invoke(wallet: String): String
        }
        object PrivateKey {
            operator fun invoke(wallet: String): String
        }
        object Keystore {
            operator fun invoke(wallet: String): String
        }
        object DerivationPath {
            operator fun invoke(wallet: String, mnemonicCode: String): String
        }
    }

    object Transfer {
        const val Route: String
        object SearchAddress {
            operator fun invoke(tradableId: String?): String
        }
        const val ScanQrCode: String
        object Send {
            operator fun invoke(address: String, tradableId: String?): String
        }
        object SearchToken {
            operator fun invoke(tradableId: String?): String
        }
        object SearchCollectibles {
            operator fun invoke(tradableId: String?): String
        }
        const val EditGasFee: String
        object AddContactSheet {
            operator fun invoke(address: String): String
        }
        object SendConfirm {
            operator fun invoke(address: String, amount: String, tradableId: String?): String
        }
    }

    object UnlockWalletDialog {
        operator fun invoke(target: String): String
    }

    object Register {
        const val Init: String

        const val Synchronization: String

        object CreateIdentity {
            const val Route: String
            object Backup {
                operator fun invoke(personaName: String, isWelcome: Boolean): String
            }
            object Verify {
                operator fun invoke(personaName: String, isWelcome: Boolean): String
            }
            object Confirm {
                operator fun invoke(personaName: String, isWelcome: Boolean): String
            }
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
                const val Route: String
                object Loading {
                    operator fun invoke(uri: String): String
                }
                object Password {
                    operator fun invoke(uri: String): String
                }
                const val Failed: String
                object Success {
                    operator fun invoke(uri: String): String
                }
                const val Notification: String
            }

            const val IdentityPersona: String
            object Identity {
                operator fun invoke(name: String): String
            }
            const val PrivateKey: String
            const val Complected: String
            const val Failed: String
            object AlreadyExists {
                operator fun invoke(restoreFrom: String): String
            }
        }
    }
}
