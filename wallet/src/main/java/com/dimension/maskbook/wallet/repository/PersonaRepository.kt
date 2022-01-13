package com.dimension.maskbook.wallet.repository

import androidx.lifecycle.LiveData
import com.dimension.maskbook.wallet.R
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class SocialData(
    val id: String,
    val name: String,
    val personaId: String? = null,
)

data class PersonaData(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
)

@Serializable
data class Persona(
    val identifier: String,
    val nickname: String?,
    val linkedProfiles: Map<String, ProfileState>,
    val hasPrivateKey: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

@Serializable
enum class ProfileState {
    @SerialName("pending")
    pending,
    @SerialName("confirmed")
    confirmed,
}

@Serializable
data class Profile(
    val identifier: String,
    val nickname: String?,
    val linkedPersona : Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

enum class Network(val value: String) {
    Twitter("twitter.com"),
    Facebook("facebook.com"),
    Instagram("instagram.com"),
    Minds("minds.com"),
}

enum class RedirectTarget {
    Gecko,
    Setup,
}

interface IPersonaRepository {
    val twitter: Flow<List<SocialData>>
    val facebook: Flow<List<SocialData>>
    val persona: Flow<List<PersonaData>>
    val currentPersona: Flow<PersonaData?>
    val redirect: LiveData<RedirectTarget?>
    fun beginConnectingProcess(
        personaId: String,
        platformType: PlatformType,
    )
    fun finishConnectingProcess(
        userName: String,
        platformType: PlatformType,
    )
    fun cancelConnectingProcess()
    fun setCurrentPersona(id: String)
    fun generateNewMnemonic(): List<String>
    fun addPersona(value: String)
    fun logout()
    fun removePersona(id: String)
    fun updatePersona(id: String, value: String)
    fun connectTwitter(personaId: String, userName: String)
    fun connectFacebook(personaId: String, userName: String)
    fun disconnectTwitter(personaId: String, socialId: String)
    fun disconnectFacebook(personaId: String, socialId: String)
    fun createPersonaFromMnemonic(value: List<String>, name: String)
    fun createPersonaFromPrivateKey(value: String)
    fun updateCurrentPersona(value: String)
    suspend fun backupPrivateKey(id: String): String
    fun init()
    fun saveEmailForCurrentPersona(value: String)
    fun savePhoneForCurrentPersona(value: String)
}

val Network.icon: Int
    get() = when (this) {
        Network.Twitter -> R.drawable.twitter
        Network.Facebook -> R.drawable.facebook
        Network.Instagram -> R.drawable.instagram
        Network.Minds -> R.drawable.ic_persona_empty_mind
    }

//
//class FakePersonaRepository : IPersonaRepository {
//    private val _persona = MutableStateFlow(emptyList<PersonaData>())
//    private val _twitter = MutableStateFlow((0..6).map {
//        SocialData(
//            UUID.randomUUID().toString().replace("-", ""),
//            it.toString()
//        )
//    })
//    private val _facebook = MutableStateFlow((0..6).map {
//        SocialData(
//            UUID.randomUUID().toString().replace("-", ""),
//            it.toString()
//        )
//    })
//    override val twitter: Flow<List<SocialData>> = _twitter.asSharedFlow()
//    override val facebook: Flow<List<SocialData>> = _facebook.asSharedFlow()
//    override val persona = _persona.asSharedFlow()
//    override val currentPersona: Flow<PersonaData?> =
//        _persona.map { it.firstOrNull() }
//    override val redirect: LiveData<RedirectTarget?>
//        get() = MutableLiveData(null)
//
//    override fun beginConnectingProcess(personaId: String, platformType: PlatformType) {
//
//    }
//
//    override fun finishConnectingProcess(userName: String, platformType: PlatformType) {
//        TODO("Not yet implemented")
//    }
//
//    override fun setCurrentPersona(id: String) {
//        _persona.value = _persona.value.toMutableList().let {
//            val index = it.indexOfFirst { it.id == id }
//            if (index != -1) {
//                it[index] = it[index]
//            }
//            it
//        }
//    }
//
//    override fun generateNewMnemonic(): List<String> {
//        return (0..11).map {
//            it.toString()
//        }
//    }
//
//    override fun addPersona(value: String) {
//        _persona.value += PersonaData(
//            name = value,
//            id = UUID.randomUUID().toString().replace("-", "")
//        )
//    }
//
//    override fun removePersona(id: String) {
//        _persona.value = _persona.value.filter { it.id != id }
//    }
//
//    override fun updatePersona(id: String, value: String) {
//        _persona.value = _persona.value.toMutableList().let {
//            val index = it.indexOfFirst { it.id == id }
//            if (index != -1) {
//                it[index] = it[index].copy(name = value)
//            }
//            it
//        }
//    }
//
//    override fun connectTwitter(personaId: String, userName: String) {
//        _twitter.value = _twitter.value.toMutableList().let {
//            val index = it.indexOfFirst { it.id == data.id }
//            if (index != -1) {
//                it[index] = it[index].copy(personaId = personaId)
//            }
//            it
//        }
//    }
//
//    override fun connectFacebook(personaId: String, userName: String) {
//        _facebook.value = _facebook.value.toMutableList().let {
//            val index = it.indexOfFirst { it.id == data.id }
//            if (index != -1) {
//                it[index] = it[index].copy(personaId = personaId)
//            }
//            it
//        }
//    }
//
//    override fun disconnectTwitter(personaId: String, socialId: String) {
//        _twitter.value = _twitter.value.toMutableList().let {
//            val index = it.indexOfFirst { it.id == socialId }
//            if (index != -1) {
//                it[index] = it[index].copy(personaId = null)
//            }
//            it
//        }
//    }
//
//    override fun disconnectFacebook(personaId: String, socialId: String) {
//        _facebook.value = _facebook.value.toMutableList().let {
//            val index = it.indexOfFirst { it.id == socialId }
//            if (index != -1) {
//                it[index] = it[index].copy(personaId = null)
//            }
//            it
//        }
//    }
//
//    override fun createPersonaFromMnemonic(value: List<String>, name: String) {
//        addPersona("")
//    }
//
//    override fun createPersonaFromPrivateKey(value: String) {
//        addPersona("")
//    }
//
//    override fun updateCurrentPersona(value: String) {
//
//    }
//
//    override suspend fun backupPrivateKey(id: String): String {
//        TODO("Not yet implemented")
//    }
//
//    override fun init() {
//
//    }
//
//    override fun saveEmailForCurrentPersona(value: String) {
//        TODO("Not yet implemented")
//    }
//
//    override fun savePhoneForCurrentPersona(value: String) {
//        TODO("Not yet implemented")
//    }
//}
