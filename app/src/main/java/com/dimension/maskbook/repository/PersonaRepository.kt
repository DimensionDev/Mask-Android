package com.dimension.maskbook.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.MutableLiveData
import com.dimension.maskbook.wallet.platform.IPlatformSwitcher
import com.dimension.maskbook.wallet.repository.ContactData
import com.dimension.maskbook.wallet.repository.IContactsRepository
import com.dimension.maskbook.wallet.repository.IPersonaRepository
import com.dimension.maskbook.wallet.repository.Network
import com.dimension.maskbook.wallet.repository.Persona
import com.dimension.maskbook.wallet.repository.PersonaData
import com.dimension.maskbook.wallet.repository.PlatformType
import com.dimension.maskbook.wallet.repository.Profile
import com.dimension.maskbook.wallet.repository.RedirectTarget
import com.dimension.maskbook.wallet.repository.SocialData
import com.dimension.maskwalletcore.WalletKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

private val CurrentPersonaKey = stringPreferencesKey("current_persona")
val Context.personaDataStore: DataStore<Preferences> by preferencesDataStore(name = "persona")
private const val TAG = "PersonaRepository"

class PersonaRepository(
    private val dataStore: DataStore<Preferences>,
    private val context: Context,
    private val platformSwitcher: IPlatformSwitcher,
) : IPersonaRepository,
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
                    name = profile.nickname ?: "",
                    avatar = "",
                    personaId = b.firstOrNull { it.linkedProfiles.containsKey(profile.identifier) }?.identifier,
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
        get() = _currentPersona.combine(persona) { a: String, b: List<PersonaData> ->
            b.firstOrNull { it.id == a }
        }
    private val _redirect = MutableLiveData<RedirectTarget?>(null)
    override val redirect: MutableLiveData<RedirectTarget?>
        get() = _redirect

    private val _connectingChannel = Channel<String>()

    @OptIn(DelicateCoroutinesApi::class, ExperimentalTime::class)
    override fun beginConnectingProcess(
        personaId: String,
        platformType: PlatformType
    ) {
        connectingJob?.cancel()
        platformSwitcher.switchTo(platformType)
        platformSwitcher.showTooltips(true)
        connectingJob = GlobalScope.launch {
            val name = _connectingChannel.receive()
            if (name.isNotEmpty()) {
                platformSwitcher.showTooltips(false)
                when (platformType) {
                    PlatformType.Twitter -> JSMethod.Persona.connectProfile(Network.Twitter, personaId, name)
                    PlatformType.Facebook -> JSMethod.Persona.connectProfile(Network.Facebook, personaId, name)
                }
                refreshSocial()
                refreshPersona()
                platformSwitcher.launchDeeplink("maskwallet://Home/Personas")
//            platformSwitcher.launchDeeplink("maskwallet://ConnectSocial/${personaId.encodeUrl()}/${platformType}")
//            while (true) {
//                delay(Duration.Companion.seconds(5))
//                val profile = JSMethod.Persona.getCurrentDetectedProfile()
//                if (profile != null) {
//                    platformSwitcher.showTooltips(false)
//                    refreshPersona()
//                    setCurrentPersona(profile)
//                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("maskwallet://ConnectSocial/${personaId.encodeUrl()}/${platformType}")))
//                    break
//                }
//            }
            }
        }
    }

    override fun finishConnectingProcess(userName: String, platformType: PlatformType) {
        scope.launch {
            _connectingChannel.send(userName)
        }
    }

    override fun cancelConnectingProcess() {
        scope.launch {
            _connectingChannel.send("")
        }
    }

    override val contacts =
        combine(_twitter, _facebook, _currentPersona) { twitter, facebook, persona ->
            (twitter + facebook).filter { !it.linkedPersona }.map {
                ContactData(
                    id = it.identifier,
                    name = it.nickname ?: "",
                    personaId = persona
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
            currentPersona.firstOrNull()?.let {
                val emailKey = stringPreferencesKey("${it.id}_email")
                dataStore.edit {
                    it[emailKey] = value
                }
            }
        }
    }

    override fun savePhoneForCurrentPersona(value: String) {
        scope.launch {
            currentPersona.firstOrNull()?.let {
                val phoneKey = stringPreferencesKey("${it.id}_phone")
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

    private suspend fun refreshPersona() {
        _persona.value = JSMethod.Persona.queryMyPersonas(null)
    }

    override fun setCurrentPersona(id: String) {
        scope.launch {
            dataStore.edit {
                it[CurrentPersonaKey] = id
            }
        }
    }

    override fun generateNewMnemonic(): List<String> {
        return createNewMnemonic().split(" ")
    }

    private fun createNewMnemonic(password: String = ""): String {
        return WalletKey.create(password).mnemonic
    }

    override fun logout() {
        scope.launch {
            val deletePersona = currentPersona.firstOrNull() ?: return@launch
            val newCurrentPersona = persona.firstOrNull()?.first { it.id != deletePersona.id }

            removePersona(deletePersona.id)

            if (newCurrentPersona != null) {
                setCurrentPersona(newCurrentPersona.id)
            }
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

    override fun createPersonaFromMnemonic(value: List<String>, name: String) {
        scope.launch {
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
