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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime

internal class PersonaRepository(
    private val scope: CoroutineScope,
    private val jsMethod: JSMethod,
    private val extensionServices: ExtensionServices,
    private val preferenceRepository: IPreferenceRepository,
    private val personaRepository: DbPersonaRepository,
    private val profileRepository: DbProfileRepository,
    private val relationRepository: DbRelationRepository,
) : IPersonaRepository,
    ISocialsRepository,
    IContactsRepository {

    private var connectingJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentPersona: Flow<PersonaData?>
        get() = preferenceRepository.currentPersonaIdentifier.flatMapLatest {
            personaRepository.getPersonaFlow(it)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val socials: Flow<List<SocialData>>
        get() = preferenceRepository.currentPersonaIdentifier.flatMapLatest { personaIdentifier ->
            profileRepository.getSocialListFlow(
                personaIdentifier = personaIdentifier,
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val contacts: Flow<List<ContactData>>
        get() = preferenceRepository.currentPersonaIdentifier.flatMapLatest { personaIdentifier ->
            relationRepository.getContactListFlow(
                personaIdentifier = personaIdentifier,
            )
        }

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

        connectingJob = preferenceRepository.lastDetectProfileIdentifier
            .mapNotNull { SocialProfile.parse(it) }
            .flowOn(Dispatchers.IO)
            .onEach {
                onDone.invoke(ConnectAccountData(personaId, it))
                connectingJob?.cancel()
            }
            .flowOn(Dispatchers.Main)
            .launchIn(scope)
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

            val identifier = preferenceRepository.currentPersonaIdentifier.firstOrNull()
            if (!identifier.isNullOrEmpty() && personaRepository.contains(identifier)) {
                return@launch
            }

            val persona = personaRepository.getPersonaList().first()
            setCurrentPersona(persona.identifier)
        }
    }

    override fun saveEmailForCurrentPersona(value: String) {
        scope.launch {
            currentPersona.firstOrNull()?.let { personaData ->
                personaRepository.updateEmail(personaData.identifier, value)
            }
        }
    }

    override fun savePhoneForCurrentPersona(value: String) {
        scope.launch {
            currentPersona.firstOrNull()?.let { personaData ->
                personaRepository.updatePhone(personaData.identifier, value)
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
        preferenceRepository.setCurrentPersonaIdentifier(id)
    }

    override fun logout() {
        scope.launch {
            val deletePersona = currentPersona.firstOrNull() ?: return@launch
            val newCurrentPersona = personaRepository.getPersonaList()
                .firstOrNull { it.identifier != deletePersona.identifier }

            removePersona(deletePersona.identifier)
            setCurrentPersona(newCurrentPersona?.identifier ?: "")
        }
    }

    private suspend fun removePersona(id: String) {
        jsMethod.removePersona(id)
    }

    override fun updatePersona(id: String, nickname: String) {
        scope.launch {
            personaRepository.updateNickName(id, nickname)
            jsMethod.updatePersonaInfo(id, nickname)
        }
    }

    override fun updateCurrentPersona(nickname: String) {
        scope.launch {
            val id = currentPersona.firstOrNull()?.identifier ?: return@launch
            personaRepository.updateNickName(id, nickname)
            jsMethod.updatePersonaInfo(id, nickname)
        }
    }

    override fun connectProfile(personaId: String, userName: String) {
        scope.launch {
            jsMethod.connectProfile(Network.Twitter, personaId, userName)
        }
    }

    override fun disconnectProfile(personaId: String, socialId: String) {
        scope.launch {
            jsMethod.disconnectProfile(socialId)
        }
    }

    override suspend fun createPersonaFromMnemonic(value: List<String>, name: String) {
        withContext(scope.coroutineContext) {
            val mnemonic = value.joinToString(" ")
            personaRepository.getPersonaList().forEach {
                if (mnemonic == jsMethod.backupMnemonic(it.identifier)) throw PersonaAlreadyExitsError()
            }
            val persona = jsMethod.createPersonaByMnemonic(value.joinToString(" "), name, "")
            // refreshPersona()
            persona?.identifier?.let { setCurrentPersona(it) }
        }
    }

    override fun createPersonaFromPrivateKey(value: String) {
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
