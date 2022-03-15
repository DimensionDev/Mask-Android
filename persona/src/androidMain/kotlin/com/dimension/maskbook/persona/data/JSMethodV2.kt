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
package com.dimension.maskbook.persona.data

import android.util.Log
import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.encodeJson
import com.dimension.maskbook.common.ext.execute
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.extension.export.model.ExtensionMessage
import com.dimension.maskbook.persona.db.migrator.IndexedDBDataMigrator
import com.dimension.maskbook.persona.db.migrator.model.IndexedDBAllRecord
import com.dimension.maskbook.persona.model.options.AttachProfileOptions
import com.dimension.maskbook.persona.model.options.CreatePersonaOptions
import com.dimension.maskbook.persona.model.options.CreateProfileOptions
import com.dimension.maskbook.persona.model.options.CreateRelationOptions
import com.dimension.maskbook.persona.model.options.DeletePersonaOptions
import com.dimension.maskbook.persona.model.options.DeleteProfileOptions
import com.dimension.maskbook.persona.model.options.DeleteRelationOptions
import com.dimension.maskbook.persona.model.options.DetachProfileOptions
import com.dimension.maskbook.persona.model.options.ParamOptions
import com.dimension.maskbook.persona.model.options.QueryPersonaByProfileOptions
import com.dimension.maskbook.persona.model.options.QueryPersonaOptions
import com.dimension.maskbook.persona.model.options.QueryPersonasOptions
import com.dimension.maskbook.persona.model.options.QueryProfileOptions
import com.dimension.maskbook.persona.model.options.QueryProfilesOptions
import com.dimension.maskbook.persona.model.options.QueryRelationsOptions
import com.dimension.maskbook.persona.model.options.UpdatePersonaOptions
import com.dimension.maskbook.persona.model.options.UpdateProfileOptions
import com.dimension.maskbook.persona.model.options.UpdateRelationOptions
import com.dimension.maskbook.persona.repository.IPreferenceRepository
import com.dimension.maskbook.persona.repository.JsPersonaRepository
import com.dimension.maskbook.persona.repository.JsProfileRepository
import com.dimension.maskbook.persona.repository.JsRelationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class JSMethodV2(
    private val scope: CoroutineScope,
    private val services: ExtensionServices,
    private val indexedDBDataMigrator: IndexedDBDataMigrator,
    private val preferenceRepository: IPreferenceRepository,
    private val personaRepository: JsPersonaRepository,
    private val profileRepository: JsProfileRepository,
    private val relationRepository: JsRelationRepository,
) {
    fun startSubscribe() {
        scope.launch {
            if (preferenceRepository.isMigratorIndexedDb.first()) {
                return@launch
            }

            val records: IndexedDBAllRecord? = services.execute("get_all_indexedDB_records")
            if (records != null) {
                indexedDBDataMigrator.migrate(records)
                preferenceRepository.setIsMigratorIndexedDb(true)
            }
        }

        services.extensionMessage
            .onEach {
                subscribeWithPersona(it) ||
                    subscribeWithProfile(it) ||
                    subscribeWithRelation(it) ||
                    subscribeWithAvatar(it) ||
                    subscribeWithPost(it)
            }
            .catch { Log.w("JSMethodV2", it) }
            .launchIn(scope)
    }

    // Persona

    private suspend fun subscribeWithPersona(message: ExtensionMessage): Boolean {
        when (message.method) {
            "create_persona" -> {
                val options = message.decodeOptions<CreatePersonaOptions>() ?: return true
                return message.responseSuccess(personaRepository.createPersona(options))
            }
            "query_persona" -> {
                val options = message.decodeOptions<ParamOptions<QueryPersonaOptions>>()?.options
                    ?: return true
                return message.responseSuccess(personaRepository.queryPersona(options))
            }
            "query_persona_by_profile" -> {
                val options = message.decodeOptions<QueryPersonaByProfileOptions>() ?: return true
                return message.responseSuccess(personaRepository.queryPersonaByProfile(options))
            }
            "query_personas" -> {
                val options = message.decodeOptions<QueryPersonasOptions>() ?: return true
                return message.responseSuccess(personaRepository.queryPersonas(options))
            }
            "update_persona" -> {
                val options = message.decodeOptions<UpdatePersonaOptions>() ?: return true
                return message.responseSuccess(personaRepository.updatePersona(options))
            }
            "delete_persona" -> {
                val options = message.decodeOptions<DeletePersonaOptions>() ?: return true
                return message.responseSuccess(personaRepository.deletePersona(options))
            }
        }
        return false
    }

    // Profile

    private suspend fun subscribeWithProfile(message: ExtensionMessage): Boolean {
        when (message.method) {
            "create_profile" -> {
                val options = message.decodeOptions<CreateProfileOptions>() ?: return true
                return message.responseSuccess(profileRepository.createProfile(options))
            }
            "query_profile" -> {
                val options = message.decodeOptions<ParamOptions<QueryProfileOptions>>()?.options
                    ?: return true
                return message.responseSuccess(profileRepository.queryProfile(options))
            }
            "query_profiles" -> {
                val options = message.decodeOptions<QueryProfilesOptions>() ?: return true
                return message.responseSuccess(profileRepository.queryProfiles(options))
            }
            "update_profile" -> {
                val options = message.decodeOptions<UpdateProfileOptions>() ?: return true
                return message.responseSuccess(profileRepository.updateProfile(options))
            }
            "delete_profile" -> {
                val options = message.decodeOptions<DeleteProfileOptions>() ?: return true
                return message.responseSuccess(profileRepository.deleteProfile(options))
            }
            "attach_profile" -> {
                val options = message.decodeOptions<AttachProfileOptions>() ?: return true
                return message.responseSuccess(profileRepository.attachProfile(options))
            }
            "detach_profile" -> {
                val options = message.decodeOptions<DetachProfileOptions>() ?: return true
                return message.responseSuccess(profileRepository.detachProfile(options))
            }
        }
        return false
    }

    // Relation

    private suspend fun subscribeWithRelation(message: ExtensionMessage): Boolean {
        when (message.method) {
            "create_relation" -> {
                val options = message.decodeOptions<CreateRelationOptions>() ?: return true
                return message.responseSuccess(relationRepository.createRelation(options))
            }
            "query_relations" -> {
                val options = message.decodeOptions<QueryRelationsOptions>() ?: return true
                return message.responseSuccess(relationRepository.queryRelations(options))
            }
            "update_relation" -> {
                val options = message.decodeOptions<UpdateRelationOptions>() ?: return true
                return message.responseSuccess(relationRepository.updateRelation(options))
            }
            "delete_relation" -> {
                val options = message.decodeOptions<DeleteRelationOptions>() ?: return true
                return message.responseSuccess(relationRepository.deleteRelation(options))
            }
        }
        return false
    }

    // Avatar

    private suspend fun subscribeWithAvatar(message: ExtensionMessage): Boolean {
        when (message.method) {
            "query_avatar" -> {
                // message.params -> QueryAvatarOptions
                // return String?
            }
            "storeAvatar" -> {
                // message.params -> StoreAvatarOptions
                // return Unit
            }
        }
        return false
    }

    // Post

    private suspend fun subscribeWithPost(message: ExtensionMessage): Boolean {
        when (message.method) {
            "create_post" -> {
                // message.params -> CreatePostOptions
                // return PostRecord?
            }
            "query_post" -> {
                // message.params -> QueryPostOptions
                // return PostRecord?
            }
            "query_posts" -> {
                // message.params -> QueryPostsOptions
                // return PostRecord[]
            }
            "updatePost" -> {
                // message.params -> UpdatePostOptions
                // return PostRecord[] ???
            }
        }
        return false
    }
}

private inline fun <reified T> ExtensionMessage.decodeOptions(): T? {
    return params?.decodeJson<T>()
}

private inline fun <reified T> ExtensionMessage.responseSuccess(result: T): Boolean {
    responseRaw(
        SerializableExtensionResponseMessage(
            messageId = id.toString(),
            jsonrpc = "2.0",
            result = result,
        ).encodeJson()
    )
    return true
}

@Serializable
data class SerializableExtensionResponseMessage<T>(
    val messageId: String,
    val jsonrpc: String,
    val result: T
)
