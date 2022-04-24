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
package com.dimension.maskbook.persona.repository

import android.net.Uri
import android.util.Base64
import com.dimension.maskbook.common.ext.decodeBase64Bytes
import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.decodeMsgPack
import com.dimension.maskbook.common.ext.encodeBase64String
import com.dimension.maskbook.common.ext.encodeJson
import com.dimension.maskbook.common.ext.encodeJsonElement
import com.dimension.maskbook.common.ext.encodeMsgPack
import com.dimension.maskbook.common.ext.toSite
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.persona.datasource.DbPersonaDataSource
import com.dimension.maskbook.persona.datasource.DbPostDataSource
import com.dimension.maskbook.persona.datasource.DbProfileDataSource
import com.dimension.maskbook.persona.datasource.DbRelationDataSource
import com.dimension.maskbook.persona.datasource.JsProfileDataSource
import com.dimension.maskbook.persona.export.error.PersonaAlreadyExitsError
import com.dimension.maskbook.persona.export.model.ConnectAccountData
import com.dimension.maskbook.persona.export.model.IndexedDBPersona
import com.dimension.maskbook.persona.export.model.IndexedDBPost
import com.dimension.maskbook.persona.export.model.IndexedDBProfile
import com.dimension.maskbook.persona.export.model.IndexedDBRelation
import com.dimension.maskbook.persona.export.model.LinkedProfileDetailsState
import com.dimension.maskbook.persona.export.model.PersonaData
import com.dimension.maskbook.persona.export.model.PlatformType
import com.dimension.maskbook.persona.export.model.SocialData
import com.dimension.maskbook.persona.ext.toJWK
import com.dimension.maskbook.persona.model.ContactData
import com.dimension.maskbook.persona.model.SocialProfile
import com.dimension.maskbook.persona.model.options.AttachProfileOptions
import com.dimension.maskbook.persona.model.options.DetachProfileOptions
import com.dimension.maskbook.setting.export.model.JsonWebKey
import com.dimension.maskwalletcore.CurveType
import com.dimension.maskwalletcore.EncryptionOption
import com.dimension.maskwalletcore.PersonaKey
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
import org.bouncycastle.jce.provider.BouncyCastleProvider

internal class PersonaRepository(
    private val scope: CoroutineScope,
    private val extensionServices: ExtensionServices,
    private val preferenceRepository: IPreferenceRepository,
    private val personaDataSource: DbPersonaDataSource,
    private val profileDataSource: DbProfileDataSource,
    private val relationDataSource: DbRelationDataSource,
    private val postDataSource: DbPostDataSource,
    private val jsProfileDataSource: JsProfileDataSource,
) : IPersonaRepository,
    ISocialsRepository,
    IContactsRepository {

    private var connectingJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentPersona: Flow<PersonaData?>
        get() = preferenceRepository.currentPersonaIdentifier.flatMapLatest {
            personaDataSource.getPersonaFlow(it)
        }

    override val personaList: Flow<List<PersonaData>>
        get() = personaDataSource.getPersonaListFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val socials: Flow<List<SocialData>>
        get() = preferenceRepository.currentPersonaIdentifier.flatMapLatest {
            profileDataSource.getSocialListFlow(it)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val contacts: Flow<List<ContactData>>
        get() = preferenceRepository.currentPersonaIdentifier.flatMapLatest {
            relationDataSource.getContactListFlow(it)
        }

    override suspend fun hasPersona(): Boolean {
        return !personaDataSource.isEmpty()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun beginConnectingProcess(
        personaId: String,
        platformType: PlatformType,
        onDone: (ConnectAccountData) -> Unit,
    ) {
        connectingJob?.cancel()
        extensionServices.setSite(platformType.toSite())

        connectingJob = preferenceRepository.lastDetectProfileIdentifier
            .filterNot { it.isEmpty() }
            .filterNot { personaDataSource.hasConnected(it) }
            .flatMapLatest { profileDataSource.getSocialFlow(it) }
            .filterNotNull()
            .flowOn(Dispatchers.IO)
            .onEach {
                onDone.invoke(ConnectAccountData(personaId, it))
                connectingJob?.cancel()
            }
            .flowOn(Dispatchers.Main)
            .launchIn(scope)
    }

    override fun init() {
        scope.launch {
            if (personaDataSource.isEmpty()) {
                return@launch
            }

            val identifier = preferenceRepository.currentPersonaIdentifier.firstOrNull()
            if (!identifier.isNullOrEmpty() && personaDataSource.contains(identifier)) {
                return@launch
            }

            val newCurrentPersona = personaDataSource.getPersonaFirst()
            setCurrentPersona(newCurrentPersona?.identifier.orEmpty())
        }
    }

    override suspend fun setCurrentPersona(id: String) {
        withContext(scope.coroutineContext) {
            if (id.isEmpty() || personaDataSource.getPersona(id) != null) {
                preferenceRepository.setCurrentPersonaIdentifier(id)
            }
        }
    }

    override suspend fun logout() {
        withContext(scope.coroutineContext) {
            val deletePersona = currentPersona.firstOrNull() ?: return@withContext
            // set current persona first ,avoid currentPersona emmit null if there has other personas
            val newCurrentPersona = personaDataSource.getPersonaList().firstOrNull {
                it != deletePersona && it.owned
            }
            setCurrentPersona(newCurrentPersona?.identifier.orEmpty())
            personaDataSource.deletePersona(deletePersona.identifier)
        }
    }

    override fun updatePersona(id: String, nickname: String) {
        scope.launch {
            personaDataSource.updateNickName(id, nickname)
        }
    }

    override fun updateCurrentPersona(nickname: String) {
        scope.launch {
            val id = currentPersona.firstOrNull()?.identifier ?: return@launch
            personaDataSource.updateNickName(id, nickname)
        }
    }

    override fun connectProfile(personaId: String, socialProfile: SocialProfile) {
        scope.launch {
            jsProfileDataSource.attachProfile(
                AttachProfileOptions(
                    personaId,
                    profileIdentifier = socialProfile.toString(),
                    state = AttachProfileOptions.State(LinkedProfileDetailsState.Confirmed)
                )
            )
        }
    }

    override fun disconnectProfile(personaId: String, socialProfile: SocialProfile) {
        scope.launch {
            jsProfileDataSource.detachProfile(
                DetachProfileOptions(
                    profileIdentifier = socialProfile.toString(),
                )
            )
        }
    }

    override suspend fun createPersonaFromMnemonic(value: List<String>, name: String) {
        withContext(scope.coroutineContext) {
            try {
                val mnemonic = value.joinToString(" ")
                if (personaDataSource.containsMnemonic(mnemonic)) {
                    throw PersonaAlreadyExitsError()
                }
                val path = "m/44'/60'/0'/0/0"
                val persona = PersonaKey.create(
                    mnemonic,
                    "",
                    path,
                    // TODO: Support other curve
                    CurveType.SECP256K1,
                    EncryptionOption(EncryptionOption.Version.V38)
                )
                val data = IndexedDBPersona(
                    identifier = persona.identifier,
                    linkedProfiles = emptyMap(),
                    nickname = name,
                    privateKey = persona.privateKey.toJWK().encodeJsonElement(),
                    publicKey = persona.publicKey.toJWK().encodeJsonElement(),
                    localKey = persona.localKey?.toJWK()?.encodeJsonElement(),
                    mnemonic = IndexedDBPersona.Mnemonic(
                        words = mnemonic,
                        parameter = IndexedDBPersona.Mnemonic.Parameter(
                            path = path,
                            withPassword = false,
                        )
                    ),
                    hasLogout = false,
                    uninitialized = false,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                )

                personaDataSource.addAll(listOf(data))
                // setCurrentPersona not work cause it's not in the database yet
                preferenceRepository.setCurrentPersonaIdentifier(data.identifier)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun createPersonaFromPrivateKey(value: String, name: String) {
        withContext(scope.coroutineContext) {
            if (personaDataSource.containsPrivateKey(value)) throw PersonaAlreadyExitsError()
            value.decodeBase64Bytes(Base64.NO_PADDING or Base64.NO_WRAP)
                .decodeMsgPack<JsonWebKey>()
                .let {
                    val data = IndexedDBPersona(
                        identifier = getIdentifierFromPrivateKey(it),
                        linkedProfiles = emptyMap(),
                        nickname = name,
                        privateKey = it.encodeJsonElement(),
                        publicKey = it.copy(d = null).encodeJsonElement(),
                        localKey = null,
                        mnemonic = null,
                        hasLogout = false,
                        uninitialized = false,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis(),
                    )
                    personaDataSource.addAll(listOf(data))
                    setCurrentPersona(data.identifier)
                }
        }
    }

    private fun getIdentifierFromPrivateKey(value: JsonWebKey): String {
        // TODO: Support other curve
        return (
            ECKey.parse(value.copy(crv = Curve.SECP256K1.name, d = null).encodeJson()).toECPublicKey(
                BouncyCastleProvider()
            ) as BCECPublicKey
            )
            .q
            .getEncoded(true)
            .encodeBase64String(Base64.URL_SAFE)
            .replace("_", "|")
            .let {
                "ec_key:secp256k1/$it".trim()
            }
    }

    override suspend fun backupPrivateKey(id: String): String {
        return personaDataSource.getPersonaPrivateKey(id)
            ?.decodeJson<JsonWebKey>()
            .encodeMsgPack()
            .encodeBase64String(Base64.NO_WRAP or Base64.NO_PADDING)
    }

    override fun setPlatform(platformType: PlatformType) {
        extensionServices.setSite(platformType.toSite())
    }

    override fun setAvatarForCurrentPersona(avatar: Uri?) {
        scope.launch {
            currentPersona.firstOrNull()?.let { personaData ->
                personaDataSource.updateAvatar(personaData.identifier, avatar?.toString())
            }
        }
    }

    override suspend fun createPersonaBackup(hasPrivateKeyOnly: Boolean): List<IndexedDBPersona> {
        return personaDataSource.getIndexDbPersonaRecord().filter {
            if (hasPrivateKeyOnly) {
                it.privateKey != null
            } else {
                true
            }
        }
    }

    override suspend fun restorePersonaBackup(list: List<IndexedDBPersona>) {
        personaDataSource.addAll(list)
        if (currentPersona.firstOrNull() == null) {
            setCurrentPersona(list.firstOrNull()?.identifier.orEmpty())
        }
    }

    override suspend fun createProfileBackup(): List<IndexedDBProfile> {
        return profileDataSource.getProfileList()
    }

    override suspend fun restoreProfileBackup(profile: List<IndexedDBProfile>) {
        profileDataSource.addAll(profile)
    }

    override suspend fun createRelationsBackup(): List<IndexedDBRelation> {
        return relationDataSource.getAll()
    }

    override suspend fun restoreRelationBackup(relation: List<IndexedDBRelation>) {
        relationDataSource.addAll(relation)
    }

    override suspend fun createPostsBackup(): List<IndexedDBPost> {
        return postDataSource.getAll()
    }

    override suspend fun restorePostBackup(post: List<IndexedDBPost>) {
        postDataSource.addAll(post)
    }
}
