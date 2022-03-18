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

import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.execute
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.extension.export.model.ExtensionMessage
import com.dimension.maskbook.extension.export.model.ExtensionResponseMessage
import com.dimension.maskbook.persona.db.PersonaDatabase
import com.dimension.maskbook.persona.db.migrator.IndexedDBDataMigrator
import com.dimension.maskbook.persona.model.indexed.IndexedDBAllRecord
import com.dimension.maskbook.persona.model.options.AttachProfileOptions
import com.dimension.maskbook.persona.model.options.CreatePersonaOptions
import com.dimension.maskbook.persona.model.options.CreatePostOptions
import com.dimension.maskbook.persona.model.options.CreateProfileOptions
import com.dimension.maskbook.persona.model.options.CreateRelationOptions
import com.dimension.maskbook.persona.model.options.DeletePersonaOptions
import com.dimension.maskbook.persona.model.options.DeleteProfileOptions
import com.dimension.maskbook.persona.model.options.DeleteRelationOptions
import com.dimension.maskbook.persona.model.options.DetachProfileOptions
import com.dimension.maskbook.persona.model.options.ParamOptions
import com.dimension.maskbook.persona.model.options.QueryAvatarOptions
import com.dimension.maskbook.persona.model.options.QueryPersonaByProfileOptions
import com.dimension.maskbook.persona.model.options.QueryPersonaOptions
import com.dimension.maskbook.persona.model.options.QueryPersonasOptions
import com.dimension.maskbook.persona.model.options.QueryPostOptions
import com.dimension.maskbook.persona.model.options.QueryPostsOptions
import com.dimension.maskbook.persona.model.options.QueryProfileOptions
import com.dimension.maskbook.persona.model.options.QueryProfilesOptions
import com.dimension.maskbook.persona.model.options.QueryRelationsOptions
import com.dimension.maskbook.persona.model.options.StoreAvatarOptions
import com.dimension.maskbook.persona.model.options.UpdatePersonaOptions
import com.dimension.maskbook.persona.model.options.UpdatePostOptions
import com.dimension.maskbook.persona.model.options.UpdateProfileOptions
import com.dimension.maskbook.persona.model.options.UpdateRelationOptions
import com.dimension.maskbook.persona.repository.IPersonaRepository
import com.dimension.maskbook.persona.repository.IPreferenceRepository
import com.dimension.maskbook.persona.repository.JsPersonaRepository
import com.dimension.maskbook.persona.repository.JsPostRepository
import com.dimension.maskbook.persona.repository.JsProfileRepository
import com.dimension.maskbook.persona.repository.JsRelationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class JSMethodV2(
    private val scope: CoroutineScope,
    private val services: ExtensionServices,
    private val appPersonaRepository: IPersonaRepository,
    private val database: PersonaDatabase,
    private val preferenceRepository: IPreferenceRepository,
    private val personaRepository: JsPersonaRepository,
    private val profileRepository: JsProfileRepository,
    private val relationRepository: JsRelationRepository,
    private val postRepository: JsPostRepository,
) {
    fun startSubscribe() {
        scope.launch {
            if (preferenceRepository.isMigratorIndexedDb.first()) {
                return@launch
            }

            val records: IndexedDBAllRecord? = services.execute("get_all_indexedDB_records")
            if (records != null) {
                IndexedDBDataMigrator.migrate(database, records)
                preferenceRepository.setIsMigratorIndexedDb(true)
            }
        }

        services.extensionMessage
            .onEach {
                subscribeWithPersona(it) ||
                    subscribeWithProfile(it) ||
                    subscribeWithRelation(it) ||
                    subscribeWithAvatar(it) ||
                    subscribeWithPost(it) ||
                    subscribeWithHelper(it)
            }
            .launchIn(scope)
    }

    // Persona

    private suspend fun subscribeWithPersona(message: ExtensionMessage): Boolean {
        when (message.method) {
            "create_persona" -> {
                val options = message.decodeOptions<CreatePersonaOptions>() ?: return true

                // set current persona when create
                appPersonaRepository.setCurrentPersona(options.persona.identifier)

                return message.responseSuccess(personaRepository.createPersona(options))
            }
            "query_persona" -> {
                val options = message.decodeOptions<QueryPersonaOptions>() ?: return true
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
                val options = message.decodeOptions<QueryAvatarOptions>() ?: return true
                return message.responseSuccess(profileRepository.queryAvatar(options))
            }
            "store_avatar" -> {
                val options = message.decodeOptions<StoreAvatarOptions>() ?: return true
                return message.responseSuccess(profileRepository.storeAvatar(options))
            }
        }
        return false
    }

    // Post

    private suspend fun subscribeWithPost(message: ExtensionMessage): Boolean {
        when (message.method) {
            "create_post" -> {
                val options = message.decodeOptions<CreatePostOptions>() ?: return true
                return message.responseSuccess(postRepository.createPost(options))
            }
            "query_post" -> {
                val options = message.decodeOptions<QueryPostOptions>() ?: return true
                return message.responseSuccess(postRepository.queryPost(options))
            }
            "query_posts" -> {
                val options = message.decodeOptions<QueryPostsOptions>() ?: return true
                return message.responseSuccess(postRepository.queryPosts(options))
            }
            "update_post" -> {
                val options = message.decodeOptions<UpdatePostOptions>() ?: return true
                return message.responseSuccess(postRepository.updatePost(options))
            }
        }
        return false
    }

    // Helper

    private fun subscribeWithHelper(message: ExtensionMessage): Boolean {
        when (message.method) {
            "notify_visible_detected_profile_changed" -> {
                val detectedProfileIdentifiers = message.decodeOptions<List<String>>()
                if (detectedProfileIdentifiers.isNullOrEmpty()) return true
                preferenceRepository.setLastDetectProfileIdentifier(detectedProfileIdentifiers[0])
                return true
            }
        }
        return false
    }
}

private inline fun <reified T> ExtensionMessage.decodeOptions(): T? {
    return params?.decodeJson<T>()
}

private inline fun <reified T> ExtensionMessage.responseSuccess(result: T?): Boolean {
    response(
        ExtensionResponseMessage(
            id = id,
            jsonrpc = jsonrpc,
            result = result,
        )
    )
    return true
}
