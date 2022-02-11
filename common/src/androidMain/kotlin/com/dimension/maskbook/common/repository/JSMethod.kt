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
package com.dimension.maskbook.common.repository

import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.util.MessageChannel
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.export.model.Profile
import com.dimension.maskbook.setting.export.model.Appearance
import com.dimension.maskbook.setting.export.model.BackupPreview
import com.dimension.maskbook.setting.export.model.DataProvider
import com.dimension.maskbook.setting.export.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import com.dimension.maskbook.persona.export.model.Persona as ExportPersona

@Serializable
data class SwitchBlockChainData(
    val coinId: Int? = null,
    val networkId: Long? = null,
)

@Serializable
data class Web3Request(
    val id: String,
    val payload: JsonRpcPayload?,
)

@Serializable
data class JsonRpcPayload(
    val jsonrpc: String,
    val method: String,
    val params: JsonArray,
    @SerialName("id")
    val _id: JsonPrimitive,
) {
    @kotlinx.serialization.Transient
    val id: Any = when {
//        _id == null -> null
        _id.isString -> _id.toString()
        else -> _id.content.toInt()
    }
}

object JSMethod {
    object Misc {
        fun openCreateWalletView(): Flow<String> {
            return MessageChannel.subscribeMessage("misc_openCreateWalletView").map {
                if (!it?.params.isNullOrEmpty()) Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona) else ""
            }
        }

        fun openDashboardView(): Flow<String> {
            return MessageChannel.subscribeMessage("misc_openDashboardView").map {
                if (!it?.params.isNullOrEmpty()) Deeplinks.Main.Home(CommonRoute.Main.Tabs.Wallet) else ""
            }
        }

        fun openAppsView(): Flow<String> {
            return MessageChannel.subscribeMessage("misc_openAppsView").map {
                if (!it?.params.isNullOrEmpty()) Deeplinks.Main.Home(CommonRoute.Main.Tabs.Labs) else ""
            }
        }

        fun openSettingsView(): Flow<String> {
            return MessageChannel.subscribeMessage("misc_openSettingsView").map {
                if (!it?.params.isNullOrEmpty()) Deeplinks.Main.Home(CommonRoute.Main.Tabs.Setting) else ""
            }
        }
    }

    object Wallet {
        suspend fun updateEthereumChainId(chainId: Long) {
            MessageChannel.executeMessage(
                "wallet_updateEthereumChainId",
                mapOf("chainId" to chainId)
            )
        }

        suspend fun updateEthereumAccount(address: String) {
            MessageChannel.executeMessage(
                "wallet_updateEthereumAccount",
                mapOf("account" to address)
            )
        }

        fun web3Event(): Flow<Web3Request?> {
            return MessageChannel.subscribeMessage("sendJsonString").map {
                it?.id?.toString()
                    ?.let { it1 -> Web3Request(it1, it.params?.decodeJson<List<String>>()?.firstOrNull()?.decodeJson<JsonRpcPayload>()) }
            }
        }

        fun switchBlockChain(): Flow<SwitchBlockChainData?> {
            return MessageChannel.subscribeMessage("wallet_switchBlockChain")
                .map { it?.params?.decodeJson<SwitchBlockChainData>() }
        }
    }

    object App {
        suspend fun isPluginEnabled(pluginID: String): Boolean {
            return MessageChannel.execute<Boolean>(
                "app_isPluginEnabled",
                mapOf("pluginID" to pluginID)
            ) ?: false
        }

        suspend fun setPluginStatus(
            pluginID: String,
            enabled: Boolean,
        ) {
            MessageChannel.executeMessage(
                "app_setPluginStatus",
                mapOf(
                    "pluginID" to pluginID,
                    "enabled" to enabled,
                )
            )
        }
    }

    object Persona {
        suspend fun getCurrentDetectedProfile(): String? {
            return MessageChannel.executeMessage("SNSAdaptor_getCurrentDetectedProfile")
        }

        suspend fun createPersonaByMnemonic(
            mnemonic: String,
            nickname: String,
            password: String,
        ): ExportPersona? {
            return MessageChannel.executeMessage(
                "persona_createPersonaByMnemonic",
                mapOf(
                    "mnemonic" to mnemonic,
                    "nickname" to nickname,
                    "password" to password,
                )
            )?.decodeJson<ExportPersona>()
        }

        suspend fun queryPersonas(
            identifier: String?,
            hasPrivateKey: Boolean
        ): List<ExportPersona> {
            return MessageChannel.executeMessage(
                "persona_queryPersonas",
                listOfNotNull(
                    identifier?.let {
                        "identifier" to identifier
                    },
                    "hasPrivateKey" to hasPrivateKey,
                ).toMap()
            )?.decodeJson<List<ExportPersona>>() ?: emptyList()
        }

        suspend fun queryMyPersonas(
            network: Network?,
        ): List<ExportPersona> {
            return MessageChannel.executeMessage(
                "persona_queryMyPersonas",
                listOfNotNull(
                    network?.let {
                        "network" to network.value
                    },
                ).toMap()
            )?.decodeJson<List<ExportPersona>>() ?: emptyList()
        }

        suspend fun updatePersonaInfo(
            identifier: String,
            nickname: String
        ) {
            MessageChannel.executeMessage(
                "persona_updatePersonaInfo",
                mapOf(
                    "identifier" to identifier,
                    "data" to mapOf(
                        "nickname" to nickname
                    )
                )
            )
        }

        suspend fun removePersona(
            identifier: String
        ) {
            MessageChannel.executeMessage(
                "persona_removePersona",
                mapOf(
                    "identifier" to identifier,
                )
            )
        }

        suspend fun restoreFromJson(
            json: String
        ) {
            MessageChannel.executeMessage(
                "persona_restoreFromJson",
                mapOf("backup" to json)
            )
        }

        suspend fun restoreFromBase64(
            backup: String
        ) {
            MessageChannel.executeMessage(
                "persona_restoreFromBase64",
                mapOf("backup" to backup)
            )
        }

        suspend fun connectProfile(
            network: Network,
            personaIdentifier: String,
            userName: String,
        ) {
            MessageChannel.executeMessage(
                "persona_connectProfile",
                mapOf(
                    "profileIdentifier" to "person:${network.value}/$userName",
                    "personaIdentifier" to personaIdentifier,
                )
            )
        }

        suspend fun disconnectProfile(
            identifier: String
        ) {
            MessageChannel.executeMessage(
                "persona_disconnectProfile",
                mapOf(
                    "identifier" to identifier
                )
            )
        }

        suspend fun backupMnemonic(
            identifier: String
        ): String? {
            return MessageChannel.executeMessage(
                "persona_backupMnemonic",
                mapOf(
                    "identifier" to identifier
                )
            )
        }

        suspend fun backupBase64(
            identifier: String
        ): String? {
            return MessageChannel.executeMessage(
                "persona_backupBase64",
                mapOf(
                    "identifier" to identifier
                )
            )
        }

        suspend fun backupJson(
            identifier: String
        ): String? {
            return MessageChannel.executeMessage(
                "persona_backupJson",
                mapOf(
                    "identifier" to identifier,
                )
            )
        }

        suspend fun backupPrivateKey(
            identifier: String
        ): String? {
            return MessageChannel.executeMessage(
                "persona_backupPrivateKey",
                mapOf(
                    "identifier" to identifier,
                )
            )
        }

        suspend fun queryProfiles(
            network: Network
        ): List<Profile> {
            return MessageChannel.executeMessage(
                "profile_queryProfiles",
                mapOf(
                    "network" to network.value
                )
            )?.decodeJson<List<Profile>>() ?: emptyList()
        }

        suspend fun queryMyProfile(
            network: Network
        ): List<Profile> {
            return MessageChannel.executeMessage(
                "profile_queryMyProfiles",
                mapOf(
                    "network" to network.value
                )
            )?.decodeJson<List<Profile>>() ?: emptyList()
        }

        suspend fun updateProfileInfo(
            identifier: String,
            nickname: String?,
            avatarURL: String?
        ) {
            MessageChannel.executeMessage(
                "profile_updateProfileInfo",
                mapOf(
                    "identifier" to identifier,
                    "data" to listOfNotNull(
                        nickname?.let {
                            "nickname" to nickname
                        },
                        avatarURL?.let {
                            "avatarURL" to avatarURL
                        },
                    ).toMap()
                )
            )
        }

        suspend fun removeProfile(
            identifier: String
        ) {
            MessageChannel.executeMessage(
                "profile_removeProfile",
                mapOf(
                    "identifier" to identifier,
                )
            )
        }
    }

    object Setting {
        suspend fun getTrendingDataSource(): DataProvider {
            return MessageChannel.execute<Int>("settings_getTrendingDataSource").let { result ->
                DataProvider.values().first { it.value == result }
            }
        }

        suspend fun setTrendingDataSource(provider: DataProvider) {
            MessageChannel.executeMessage(
                "settings_setTrendingDataSource",
                mapOf("provider" to provider.ordinal)
            )
        }

        suspend fun getTheme(): Appearance {
            return MessageChannel.executeMessage("settings_getTheme")?.let {
                Appearance.valueOf(it)
            } ?: Appearance.default
        }

        suspend fun setTheme(appearance: Appearance) {
            MessageChannel.executeMessage("settings_setTheme", mapOf("theme" to appearance.name))
        }

        suspend fun getLanguage(): Language {
            return MessageChannel.executeMessage("settings_getLanguage")?.let {
                Language.valueOf(it)
            } ?: Language.auto
        }

        suspend fun setLanguage(language: Language) {
            MessageChannel.executeMessage(
                "settings_setLanguage",
                mapOf("language" to language.name)
            )
        }

        suspend fun createBackupJson(
            noPosts: Boolean = false,
            noWallets: Boolean = false,
            noPersonas: Boolean = false,
            noProfiles: Boolean = false,
            hasPrivateKeyOnly: Boolean = false,
        ): String {
            return MessageChannel.executeMessage(
                "settings_createBackupJsonString",
                mapOf(
                    "noPosts" to noPosts,
                    "noWallets" to noWallets,
                    "noPersonas" to noPersonas,
                    "noProfiles" to noProfiles,
                    "hasPrivateKeyOnly" to hasPrivateKeyOnly,
                )
            ) ?: ""
        }

        suspend fun getBackupPreviewInfo(json: String): BackupPreview? {
            return MessageChannel.executeMessage(
                "settings_getBackupPreviewInfo",
                mapOf("backupInfo" to json)
            )?.decodeJson<BackupPreview>()
        }

        suspend fun restoreBackup(json: String) {
            MessageChannel.executeMessage("settings_restoreBackup", mapOf("backupInfo" to json))
        }
    }
}
