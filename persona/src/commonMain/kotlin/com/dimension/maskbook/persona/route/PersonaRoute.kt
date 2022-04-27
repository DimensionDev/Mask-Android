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
package com.dimension.maskbook.persona.route

import com.dimension.maskbook.common.routeProcessor.annotations.Route

@Suppress("CONST_VAL_WITHOUT_INITIALIZER")
@Route
expect object PersonaRoute {
    const val Logout: String
    const val LogoutBeforeCheck: String
    const val PersonaMenu: String
    const val SwitchPersona: String
    const val SetAvatar: String
    const val PersonaAvatarModal: String
    object RenamePersona {
        operator fun invoke(personaId: String): String
    }
    const val ExportPrivateKey: String
    object DownloadQrCode {
        operator fun invoke(idType: String, idBase64: String): String
    }
    object SelectPlatform {
        operator fun invoke(personaId: String): String
    }
    object ConnectAccount {
        operator fun invoke(personaId: String, profileId: String, avatar: String?): String
    }
    object DisconnectSocial {
        operator fun invoke(
            personaId: String,
            socialId: String,
            personaName: String?,
            socialName: String?
        ): String
    }

    object BackUpPassword {
        operator fun invoke(target: String): String
    }

    object Synchronization {
        object Scan {
            operator fun invoke(failedRoute: String?): String
        }
        const val Success: String
        const val Failed: String
        object Persona {
            const val AlreadyExists: String
            object PrivateKey {
                operator fun invoke(privateKey: String, nickname: String?): String
            }
            object Identity {
                operator fun invoke(identity: String, nickname: String?): String
            }
        }
    }

    object Register {
        const val Init: String

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
                    operator fun invoke(uri: String, uploaded_at: Long?, abstract: String?, account: String?): String
                }
                object Password {
                    operator fun invoke(uri: String, uploaded_at: Long?, abstract: String?, account: String?): String
                }
                const val Failed: String
                object Success {
                    operator fun invoke(uri: String, account: String?): String
                }
                object Wallet {
                    operator fun invoke(uri: String, account: String?): String
                }
                object SetupPassword {
                    operator fun invoke(uri: String, account: String?): String
                }
                object ConfirmPassword {
                    operator fun invoke(uri: String, account: String?): String
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
