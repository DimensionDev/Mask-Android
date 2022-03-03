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

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.MutableLiveData
import com.dimension.maskbook.common.ext.ifNullOrEmpty
import com.dimension.maskbook.common.platform.IPlatformSwitcher
import com.dimension.maskbook.common.repository.JSMethod
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.persona.export.error.PersonaAlreadyExitsError
import com.dimension.maskbook.persona.export.model.ConnectAccountData
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.export.model.Persona
import com.dimension.maskbook.persona.export.model.PersonaData
import com.dimension.maskbook.persona.export.model.PlatformType
import com.dimension.maskbook.persona.export.model.Profile
import com.dimension.maskbook.persona.export.model.SocialData
import com.dimension.maskbook.persona.export.model.SocialProfile
import com.dimension.maskbook.persona.model.ContactData
import com.dimension.maskbook.persona.model.RedirectTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

private val CurrentPersonaKey = stringPreferencesKey("current_persona")
val Context.personaDataStore: DataStore<Preferences> by preferencesDataStore(name = "persona")
private const val TAG = "PersonaRepository"

class PersonaRepository(
    private val dataStore: DataStore<Preferences>,
    private val platformSwitcher: IPlatformSwitcher,
) : IPersonaRepository,
    ISocialsRepository,
    IContactsRepository {

    private var connectingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _currentPersona = dataStore.data.map {
        it[CurrentPersonaKey] ?: ""
    }

    private val _twitter = MutableStateFlow(emptyList<Profile>())
    override val twitter: Flow<List<SocialData>>
        get() = _twitter.combine(_persona) { a: List<Profile>, b: List<Persona> ->
            a.map { profile ->
                SocialData(
                    id = profile.identifier,
                    name = profile.nickname.ifNullOrEmpty {
                        profile.identifier.substringAfter('/')
                    },
                    avatar = "",
                    personaId = b.firstOrNull { it.linkedProfiles.containsKey(profile.identifier) }?.identifier,
                    linkedPersona = profile.linkedPersona,
                    network = Network.Twitter,
                )
            }
        }

    private val _facebook = MutableStateFlow(emptyList<Profile>())
    override val facebook: Flow<List<SocialData>>
        get() = _facebook.combine(_persona) { a: List<Profile>, b: List<Persona> ->
            a.map { profile ->
                SocialData(
                    id = profile.identifier,
                    name = profile.nickname ?: "",
                    avatar = "",
                    personaId = b.firstOrNull { it.linkedProfiles.containsKey(profile.identifier) }?.identifier,
                    linkedPersona = profile.linkedPersona,
                    network = Network.Facebook,
                )
            }
        }

    private val _persona = MutableStateFlow(emptyList<Persona>())
    override val persona: Flow<List<PersonaData>>
        get() = _persona.combine(dataStore.data) { items, data ->
            items.map {
                val emailKey = stringPreferencesKey("${it.identifier}_email")
                val phoneKey = stringPreferencesKey("${it.identifier}_phone")
                PersonaData(
                    id = it.identifier,
                    name = it.nickname ?: "",
                    email = data[emailKey],
                    phone = data[phoneKey]
                )
            }
        }

    override val currentPersona: Flow<PersonaData?>
        get() = _currentPersona.combine(persona) { current: String, list: List<PersonaData> ->
            list.firstOrNull { it.id == current }
        }

    private val _redirect = MutableLiveData<RedirectTarget?>(null)
    override val redirect: MutableLiveData<RedirectTarget?>
        get() = _redirect

    @OptIn(DelicateCoroutinesApi::class, ExperimentalTime::class)
    override fun beginConnectingProcess(
        personaId: String,
        platformType: PlatformType
    ) {
        connectingJob?.cancel()
        platformSwitcher.switchTo(platformType)
        platformSwitcher.showTooltips(true)
        connectingJob = GlobalScope.launch {
            while (true) {
                delay(5.seconds)
                // TODO: getCurrentDetectedProfileDelegateToSNSAdaptor will always return person:localhost/$unknown when first login
                val profile = JSMethod.Persona.getCurrentDetectedProfileDelegateToSNSAdaptor()?.takeIf {
                    it.isNotEmpty()
                }?.let {
                    SocialProfile.parse(it)
                }
                if (profile != null) {
                    platformSwitcher.showTooltips(false)
                    platformSwitcher.showModal("ConnectAccount", ConnectAccountData(personaId, profile))
                    break
                }
            }
        }
    }

    override fun finishConnectingProcess(profile: SocialProfile, personaId: String) {
        scope.launch {
            JSMethod.Persona.connectProfile(profile.network, personaId, profile.userId)
            refreshSocial()
            refreshPersona()
            platformSwitcher.launchDeeplink(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona))
        }
    }

    override fun cancelConnectingProcess() {
        connectingJob?.cancel()
        platformSwitcher.launchDeeplink(Deeplinks.Main.Home(CommonRoute.Main.Tabs.Persona))
    }

    override val socials: Flow<List<SocialData>>
        get() = combine(
            twitter, facebook, _currentPersona
        ) { twitter, facebook, persona ->
            (twitter + facebook)
                .filter { it.personaId == persona }
        }

    override val contacts: Flow<List<ContactData>>
        get() = combine(
            twitter, facebook, _currentPersona
        ) { twitter, facebook, persona ->
            (twitter + facebook)
                .filter { it.personaId != persona }
                .map {
                    ContactData(
                        id = it.id,
                        name = it.name,
                        personaId = persona,
                        linkedPersona = it.linkedPersona,
                        network = it.network,
                    )
                }
        }

    override fun init() {
        scope.launch {
            awaitAll(
                async { refreshSocial() },
                async {
                    refreshPersona()
                    _currentPersona.firstOrNull()?.let { current ->
                        if (!_persona.value.any { it.identifier == current }) {
                            setCurrentPersona("")
                        }
                    }
                    if (_currentPersona.firstOrNull().isNullOrEmpty()) {
                        _redirect.postValue(RedirectTarget.Setup)
                    } else {
                        _redirect.postValue(RedirectTarget.Gecko)
                    }
                }
            )
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

    private suspend fun refreshSocial() {
        _twitter.value = JSMethod.Persona.queryProfiles(Network.Twitter)
        _facebook.value = JSMethod.Persona.queryProfiles(Network.Facebook)
    }

    override fun refreshPersona() {
        scope.launch {
            _persona.value = JSMethod.Persona.queryMyPersonas(null)
            if (_currentPersona.firstOrNull().isNullOrEmpty()) {
                _persona.value.firstOrNull()?.identifier?.let { setCurrentPersona(it) }
            }
        }
    }

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
            refreshPersona()
        }
    }

    private suspend fun removePersona(id: String) {
        JSMethod.Persona.removePersona(id)
        val emailKey = stringPreferencesKey("${id}_email")
        val phoneKey = stringPreferencesKey("${id}_phone")
        dataStore.edit {
            it[emailKey] = ""
            it[phoneKey] = ""
        }
    }

    override fun updatePersona(id: String, value: String) {
        scope.launch {
            JSMethod.Persona.updatePersonaInfo(id, value)
            refreshPersona()
        }
    }

    override fun connectTwitter(personaId: String, userName: String) {
        scope.launch {
            JSMethod.Persona.connectProfile(Network.Twitter, personaId, userName)
            refreshPersona()
        }
    }

    override fun connectFacebook(personaId: String, userName: String) {
        scope.launch {
            JSMethod.Persona.connectProfile(Network.Facebook, personaId, userName)
            refreshPersona()
        }
    }

    override fun disconnectTwitter(personaId: String, socialId: String) {
        scope.launch {
            JSMethod.Persona.disconnectProfile(socialId)
            refreshSocial()
            refreshPersona()
        }
    }

    override fun disconnectFacebook(personaId: String, socialId: String) {
        scope.launch {
            JSMethod.Persona.disconnectProfile(socialId)
            refreshSocial()
            refreshPersona()
        }
    }

    override suspend fun createPersonaFromMnemonic(value: List<String>, name: String) {
        withContext(scope.coroutineContext) {
            val personas = _persona.value
            val mnemonic = value.joinToString(" ")
            personas.forEach {
                if (mnemonic == JSMethod.Persona.backupMnemonic(it.identifier)) throw PersonaAlreadyExitsError()
            }
            val persona = JSMethod.Persona.createPersonaByMnemonic(value.joinToString(" "), name, "")
            refreshPersona()
            persona?.identifier?.let { setCurrentPersona(it) }
        }
    }

    override fun createPersonaFromPrivateKey(value: String) {
        scope.launch {
        }
    }

    override fun updateCurrentPersona(value: String) {
        scope.launch {
            _currentPersona.firstOrNull()?.let {
                JSMethod.Persona.updatePersonaInfo(it, value)
                refreshPersona()
            }
        }
    }

    override suspend fun backupPrivateKey(id: String): String {
        return JSMethod.Persona.backupPrivateKey(id) ?: ""
    }
}
