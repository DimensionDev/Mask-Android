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

import com.dimension.maskbook.common.ext.JSON
import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.execute
import com.dimension.maskbook.common.ext.normalized
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.extension.export.model.ExtensionMessage
import com.dimension.maskbook.extension.export.model.buildExtensionResponse
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
import kotlinx.serialization.json.encodeToJsonElement

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

        services.subscribeJSEvent(*methods)
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
            createPersona -> {
                val options = message.decodeOptions<CreatePersonaOptions>() ?: return true

                // set current persona when create
                appPersonaRepository.setCurrentPersona(options.persona.identifier)

                return message.responseSuccess(personaRepository.createPersona(options))
            }
            queryPersona -> {
                val options = message.decodeOptions<QueryPersonaOptions>() ?: return true
                return message.responseSuccess(personaRepository.queryPersona(options))
            }
            queryPersonaByProfile -> {
                val options = message.decodeOptions<ParamOptions<QueryPersonaByProfileOptions>>()?.options ?: return true
                return message.responseSuccess(personaRepository.queryPersonaByProfile(options))
            }
            queryPersonas -> {
                val options = message.decodeOptions<QueryPersonasOptions>() ?: return true
                return message.responseSuccess(personaRepository.queryPersonas(options))
            }
            updatePersona -> {
                val options = message.decodeOptions<UpdatePersonaOptions>() ?: return true
                return message.responseSuccess(personaRepository.updatePersona(options))
            }
            deletePersona -> {
                val options = message.decodeOptions<DeletePersonaOptions>() ?: return true
                return message.responseSuccess(personaRepository.deletePersona(options))
            }
        }
        return false
    }

    // Profile

    private suspend fun subscribeWithProfile(message: ExtensionMessage): Boolean {
        when (message.method) {
            createProfile -> {
                val options = message.decodeOptions<CreateProfileOptions>() ?: return true
                return message.responseSuccess(profileRepository.createProfile(options))
            }
            queryProfile -> {
                val options = message.decodeOptions<ParamOptions<QueryProfileOptions>>()?.options
                    ?: return true
                return message.responseSuccess(profileRepository.queryProfile(options))
            }
            queryProfiles -> {
                val options = message.decodeOptions<QueryProfilesOptions>() ?: return true
                return message.responseSuccess(profileRepository.queryProfiles(options))
            }
            updateProfile -> {
                val options = message.decodeOptions<UpdateProfileOptions>() ?: return true
                return message.responseSuccess(profileRepository.updateProfile(options))
            }
            deleteProfile -> {
                val options = message.decodeOptions<DeleteProfileOptions>() ?: return true
                return message.responseSuccess(profileRepository.deleteProfile(options))
            }
            attachProfile -> {
                val options = message.decodeOptions<AttachProfileOptions>() ?: return true
                return message.responseSuccess(profileRepository.attachProfile(options))
            }
            detachProfile -> {
                val options = message.decodeOptions<DetachProfileOptions>() ?: return true
                return message.responseSuccess(profileRepository.detachProfile(options))
            }
        }
        return false
    }

    // Relation

    private suspend fun subscribeWithRelation(message: ExtensionMessage): Boolean {
        when (message.method) {
            createRelation -> {
                val options = message.decodeOptions<CreateRelationOptions>() ?: return true
                return message.responseSuccess(relationRepository.createRelation(options))
            }
            queryRelations -> {
                val options = message.decodeOptions<QueryRelationsOptions>() ?: return true
                return message.responseSuccess(relationRepository.queryRelations(options))
            }
            updateRelation -> {
                val options = message.decodeOptions<UpdateRelationOptions>() ?: return true
                return message.responseSuccess(relationRepository.updateRelation(options))
            }
            deleteRelation -> {
                val options = message.decodeOptions<DeleteRelationOptions>() ?: return true
                return message.responseSuccess(relationRepository.deleteRelation(options))
            }
        }
        return false
    }

    // Avatar

    private suspend fun subscribeWithAvatar(message: ExtensionMessage): Boolean {
        when (message.method) {
            queryAvatar -> {
                val options = message.decodeOptions<QueryAvatarOptions>() ?: return true
                return message.responseSuccess(profileRepository.queryAvatar(options))
            }
            storeAvatar -> {
                val options = message.decodeOptions<StoreAvatarOptions>() ?: return true
                return message.responseSuccess(profileRepository.storeAvatar(options))
            }
        }
        return false
    }

    // Post

    private suspend fun subscribeWithPost(message: ExtensionMessage): Boolean {
        when (message.method) {
            createPost -> {
                val options = message.decodeOptions<CreatePostOptions>() ?: return true
                return message.responseSuccess(postRepository.createPost(options))
            }
            queryPost -> {
                val options = message.decodeOptions<QueryPostOptions>() ?: return true
                return message.responseSuccess(postRepository.queryPost(options))
            }
            queryPosts -> {
                val options = message.decodeOptions<QueryPostsOptions>() ?: return true
                return message.responseSuccess(postRepository.queryPosts(options))
            }
            updatePost -> {
                val options = message.decodeOptions<UpdatePostOptions>() ?: return true
                return message.responseSuccess(postRepository.updatePost(options))
            }
        }
        return false
    }

    // Helper

    private fun subscribeWithHelper(message: ExtensionMessage): Boolean {
        when (message.method) {
            notifyVisibleDetectedProfileChanged -> {
                val detectedProfileIdentifiers = message.decodeOptions<List<String>>()
                if (detectedProfileIdentifiers.isNullOrEmpty()) return true
                preferenceRepository.setLastDetectProfileIdentifier(detectedProfileIdentifiers[0])
                return true
            }
        }
        return false
    }

    companion object {
        private const val createPersona = "create_persona"
        private const val queryPersona = "query_persona"
        private const val queryPersonaByProfile = "query_persona_by_profile"
        private const val queryPersonas = "query_personas"
        private const val updatePersona = "update_persona"
        private const val deletePersona = "delete_persona"

        private const val createProfile = "create_profile"
        private const val queryProfile = "query_profile"
        private const val queryProfiles = "query_profiles"
        private const val updateProfile = "update_profile"
        private const val deleteProfile = "delete_profile"
        private const val attachProfile = "attach_profile"
        private const val detachProfile = "detach_profile"

        private const val createRelation = "create_relation"
        private const val queryRelations = "query_relations"
        private const val updateRelation = "update_relation"
        private const val deleteRelation = "delete_relation"

        private const val queryAvatar = "query_avatar"
        private const val storeAvatar = "store_avatar"

        private const val createPost = "create_post"
        private const val queryPost = "query_post"
        private const val queryPosts = "query_posts"
        private const val updatePost = "update_post"

        private const val notifyVisibleDetectedProfileChanged = "notify_visible_detected_profile_changed"

        private val methods = arrayOf(
            createPersona, queryPersona, queryPersonaByProfile, queryPersonas, updatePersona, deletePersona,
            createProfile, queryProfile, queryProfiles, updateProfile, deleteProfile, attachProfile, detachProfile,
            createRelation, queryRelations, updateRelation, deleteRelation,
            queryAvatar, storeAvatar,
            createPost, queryPost, queryPosts, updatePost,
            notifyVisibleDetectedProfileChanged,
        )
    }
}

private inline fun <reified T> ExtensionMessage.decodeOptions(): T? {
    return params?.decodeJson<T>()
}

private inline fun <reified T : Any> ExtensionMessage.responseSuccess(result: T?): Boolean {
    response(
        buildExtensionResponse(
            id = id,
            jsonrpc = jsonrpc,
            result = wrapResult(result),
        )
    )
    return true
}

private inline fun <reified T : Any> wrapResult(result: T?): Any? {
    return JSON.encodeToJsonElement(result).normalized
}
