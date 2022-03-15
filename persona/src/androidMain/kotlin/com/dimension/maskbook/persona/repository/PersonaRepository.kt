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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dimension.maskbook.common.ext.asStateIn
import com.dimension.maskbook.common.ext.toSite
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.persona.data.JSMethod
import com.dimension.maskbook.persona.export.error.PersonaAlreadyExitsError
import com.dimension.maskbook.persona.export.model.ConnectAccountData
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.export.model.PersonaData
import com.dimension.maskbook.persona.export.model.PlatformType
import com.dimension.maskbook.persona.export.model.SocialData
import com.dimension.maskbook.persona.export.model.SocialProfile
import com.dimension.maskbook.persona.model.ContactData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime

private val CurrentPersonaKey = stringPreferencesKey("current_persona")

internal class PersonaRepository(
    private val scope: CoroutineScope,
    private val dataStore: DataStore<Preferences>,
    private val jsMethod: JSMethod,
    private val extensionServices: ExtensionServices,
    private val personaRepository: DbPersonaRepository,
    private val profileRepository: DbProfileRepository,
    private val relationRepository: DbRelationRepository,
) : IPersonaRepository,
    ISocialsRepository,
    IContactsRepository {

    private var connectingJob: Job? = null

    private val currentPersonaIdentifier = dataStore.data.map {
        it[CurrentPersonaKey] ?: ""
    }

    override val persona: Flow<List<PersonaData>> =
        personaRepository.getListFlow().zip(dataStore.data) { list, data ->
            list.map { persona ->
                val id = persona.identifier
                val emailKey = stringPreferencesKey("${id}_email")
                val phoneKey = stringPreferencesKey("${id}_phone")
                PersonaData(
                    id = id,
                    name = persona.nickname.orEmpty(),
                    email = data[emailKey],
                    phone = data[phoneKey]
                )
            }
        }.asStateIn(scope, emptyList())

    override val currentPersona: Flow<PersonaData?> =
        combine(currentPersonaIdentifier, persona) { personaIdentifier, persona ->
            persona.find { it.id == personaIdentifier }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val socials: Flow<List<SocialData>>
        get() = currentPersonaIdentifier.flatMapLatest { personaIdentifier ->
            profileRepository.getSocialListFlow(
                personaIdentifier = personaIdentifier,
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val contacts: Flow<List<ContactData>>
        get() = currentPersonaIdentifier.flatMapLatest { personaIdentifier ->
            relationRepository.getContactListFlow(
                personaIdentifier = personaIdentifier,
            )
        }

    private val lastDetectProfile: Flow<SocialProfile?> =
        extensionServices.subscribeJSEvent("notify_visible_detected_profile_changed")
            .mapNotNull { it.params }
            .map { SocialProfile.parse(it) }
            .asStateIn(scope, null)

    override suspend fun hasPersona(): Boolean {
        return !personaRepository.isEmpty()
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalTime::class)
    override fun beginConnectingProcess(
        personaId: String,
        platformType: PlatformType,
        onDone: (ConnectAccountData) -> Unit,
    ) {
        connectingJob?.cancel()
        extensionServices.setSite(platformType.toSite())
        connectingJob = scope.launch {
            val profile = lastDetectProfile.filterNotNull().first()
            onDone.invoke(ConnectAccountData(personaId, profile))
        }
    }

    override fun finishConnectingProcess(profile: SocialProfile, personaId: String) {
        scope.launch {
            jsMethod.connectProfile(profile.network, personaId, profile.userId)
            // refreshSocial()
            // refreshPersona()

            // platformSwitcher.launchDeeplink(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona))
        }
    }

    override fun cancelConnectingProcess() {
        connectingJob?.cancel()
        // platformSwitcher.launchDeeplink(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona))
    }

    override fun init() {
        scope.launch {
            if (personaRepository.isEmpty()) {
                return@launch
            }

            val identifier = currentPersonaIdentifier.firstOrNull()
            if (!identifier.isNullOrEmpty() && personaRepository.contains(identifier)) {
                return@launch
            }

            val persona = personaRepository.getList().first()
            setCurrentPersona(persona.identifier)
        }
    }

    override fun saveEmailForCurrentPersona(value: String) {
        scope.launch {
            currentPersona.firstOrNull()?.let { personaData ->
                val emailKey = stringPreferencesKey("${personaData.id}_email")
                dataStore.edit {
                    it[emailKey] = value
                }
            }
        }
    }

    override fun savePhoneForCurrentPersona(value: String) {
        scope.launch {
            currentPersona.firstOrNull()?.let { personaData ->
                val phoneKey = stringPreferencesKey("${personaData.id}_phone")
                dataStore.edit {
                    it[phoneKey] = value
                }
            }
        }
    }

    // private suspend fun refreshSocial() {
    //     _twitter.value = jsMethod.queryProfiles(Network.Twitter)
    //     _facebook.value = jsMethod.queryProfiles(Network.Facebook)
    // }
    //
    // override suspend fun refreshPersona() {
    //     _persona.value = jsMethod.queryMyPersonas(null)
    //     if (_currentPersona.firstOrNull().isNullOrEmpty()) {
    //         _persona.value.firstOrNull()?.identifier?.let { setCurrentPersona(it) }
    //     }
    // }

    override fun setCurrentPersona(id: String) {
        scope.launch {
            dataStore.edit {
                it[CurrentPersonaKey] = id
            }
        }
    }

    override fun logout() {
        scope.launch {
            val deletePersona = currentPersona.firstOrNull() ?: return@launch
            val newCurrentPersona = persona.firstOrNull()?.firstOrNull { it.id != deletePersona.id }

            removePersona(deletePersona.id)

            setCurrentPersona(newCurrentPersona?.id ?: "")
            // refreshPersona()
        }
    }

    private suspend fun removePersona(id: String) {
        // jsMethod.removePersona(id)
        val emailKey = stringPreferencesKey("${id}_email")
        val phoneKey = stringPreferencesKey("${id}_phone")
        dataStore.edit {
            it[emailKey] = ""
            it[phoneKey] = ""
        }
    }

    override fun updatePersona(id: String, value: String) {
        scope.launch {
            jsMethod.updatePersonaInfo(id, value)
            // refreshPersona()
        }
    }

    override fun connectProfile(personaId: String, userName: String) {
        scope.launch {
            jsMethod.connectProfile(Network.Twitter, personaId, userName)
            // refreshPersona()
        }
    }

    override fun disconnectProfile(personaId: String, socialId: String) {
        scope.launch {
            jsMethod.disconnectProfile(socialId)
            // refreshSocial()
            // refreshPersona()
        }
    }

    override suspend fun createPersonaFromMnemonic(value: List<String>, name: String) {
        withContext(scope.coroutineContext) {
            val mnemonic = value.joinToString(" ")
            personaRepository.getList().forEach {
                if (mnemonic == jsMethod.backupMnemonic(it.identifier)) throw PersonaAlreadyExitsError()
            }
            val persona = jsMethod.createPersonaByMnemonic(value.joinToString(" "), name, "")
            // refreshPersona()
            persona?.identifier?.let { setCurrentPersona(it) }
        }
    }

    override fun createPersonaFromPrivateKey(value: String) {
    }

    override fun updateCurrentPersona(value: String) {
        // scope.launch {
        //     _currentPersona.firstOrNull()?.let {
        //         jsMethod.updatePersonaInfo(it, value)
        //         refreshPersona()
        //     }
        // }
    }

    override suspend fun backupPrivateKey(id: String): String {
        return jsMethod.backupPrivateKey(id) ?: ""
    }

    // override suspend fun ensurePersonaDataLoaded() {
    //     _loaded.first { it }
    // }

    override fun setPlatform(platformType: PlatformType) {
        extensionServices.setSite(platformType.toSite())
    }
}
