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

import com.dimension.maskbook.common.ext.decodeOptions
import com.dimension.maskbook.common.ext.execute
import com.dimension.maskbook.common.ext.responseSuccess
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.extension.export.model.ExtensionMessage
import com.dimension.maskbook.persona.datasource.JsPersonaDataSource
import com.dimension.maskbook.persona.datasource.JsPostDataSource
import com.dimension.maskbook.persona.datasource.JsProfileDataSource
import com.dimension.maskbook.persona.datasource.JsRelationDataSource
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
import com.dimension.maskbook.persona.model.options.QueryRelationOptions
import com.dimension.maskbook.persona.model.options.QueryRelationsOptions
import com.dimension.maskbook.persona.model.options.StoreAvatarOptions
import com.dimension.maskbook.persona.model.options.UpdatePersonaOptions
import com.dimension.maskbook.persona.model.options.UpdatePostOptions
import com.dimension.maskbook.persona.model.options.UpdateProfileOptions
import com.dimension.maskbook.persona.model.options.UpdateRelationOptions
import com.dimension.maskbook.persona.repository.IPersonaRepository
import com.dimension.maskbook.persona.repository.IPreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class JSMethodV2(
    private val scope: CoroutineScope,
    private val services: ExtensionServices,
    private val database: PersonaDatabase,
    private val personaRepository: IPersonaRepository,
    private val preferenceRepository: IPreferenceRepository,
    private val personaDataSource: JsPersonaDataSource,
    private val profileDataSource: JsProfileDataSource,
    private val relationDataSource: JsRelationDataSource,
    private val postDataSource: JsPostDataSource,
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

        services.subscribeBackgroundJSEvent(*methods)
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

                // set current persona when usr create
                // ps: persona created by user will have privateKey
                if (options.persona.privateKey != null) {
                    personaRepository.setCurrentPersona(options.persona.identifier)
                }

                return message.responseSuccess(personaDataSource.createPersona(options))
            }
            queryPersona -> {
                val options = message.decodeOptions<QueryPersonaOptions>() ?: return true
                return message.responseSuccess(personaDataSource.queryPersona(options))
            }
            queryPersonaByProfile -> {
                val options = message.decodeOptions<ParamOptions<QueryPersonaByProfileOptions>>()?.options ?: return true
                return message.responseSuccess(personaDataSource.queryPersonaByProfile(options))
            }
            queryPersonas -> {
                val options = message.decodeOptions<QueryPersonasOptions>() ?: return true
                return message.responseSuccess(personaDataSource.queryPersonas(options))
            }
            updatePersona -> {
                val options = message.decodeOptions<UpdatePersonaOptions>() ?: return true
                return message.responseSuccess(personaDataSource.updatePersona(options))
            }
            deletePersona -> {
                val options = message.decodeOptions<DeletePersonaOptions>() ?: return true
                return message.responseSuccess(personaDataSource.deletePersona(options))
            }
        }
        return false
    }

    // Profile

    private suspend fun subscribeWithProfile(message: ExtensionMessage): Boolean {
        when (message.method) {
            createProfile -> {
                val options = message.decodeOptions<CreateProfileOptions>() ?: return true
                return message.responseSuccess(profileDataSource.createProfile(options))
            }
            queryProfile -> {
                val options = message.decodeOptions<ParamOptions<QueryProfileOptions>>()?.options
                    ?: return true
                return message.responseSuccess(profileDataSource.queryProfile(options))
            }
            queryProfiles -> {
                val options = message.decodeOptions<QueryProfilesOptions>() ?: return true
                return message.responseSuccess(profileDataSource.queryProfiles(options))
            }
            updateProfile -> {
                val options = message.decodeOptions<UpdateProfileOptions>() ?: return true
                return message.responseSuccess(profileDataSource.updateProfile(options))
            }
            deleteProfile -> {
                val options = message.decodeOptions<DeleteProfileOptions>() ?: return true
                return message.responseSuccess(profileDataSource.deleteProfile(options))
            }
            attachProfile -> {
                val options = message.decodeOptions<AttachProfileOptions>() ?: return true
                return message.responseSuccess(profileDataSource.attachProfile(options))
            }
            detachProfile -> {
                val options = message.decodeOptions<DetachProfileOptions>() ?: return true
                return message.responseSuccess(profileDataSource.detachProfile(options))
            }
        }
        return false
    }

    // Relation

    private suspend fun subscribeWithRelation(message: ExtensionMessage): Boolean {
        when (message.method) {
            createRelation -> {
                val options = message.decodeOptions<CreateRelationOptions>() ?: return true
                return message.responseSuccess(relationDataSource.createRelation(options))
            }
            queryRelation -> {
                val options = message.decodeOptions<QueryRelationOptions>() ?: return true
                return message.responseSuccess(relationDataSource.queryRelation(options))
            }
            queryRelations -> {
                val options = message.decodeOptions<QueryRelationsOptions>() ?: return true
                return message.responseSuccess(relationDataSource.queryRelations(options))
            }
            updateRelation -> {
                val options = message.decodeOptions<UpdateRelationOptions>() ?: return true
                return message.responseSuccess(relationDataSource.updateRelation(options))
            }
            deleteRelation -> {
                val options = message.decodeOptions<DeleteRelationOptions>() ?: return true
                return message.responseSuccess(relationDataSource.deleteRelation(options))
            }
        }
        return false
    }

    // Avatar

    private suspend fun subscribeWithAvatar(message: ExtensionMessage): Boolean {
        when (message.method) {
            queryAvatar -> {
                val options = message.decodeOptions<QueryAvatarOptions>() ?: return true
                return message.responseSuccess(profileDataSource.queryAvatar(options))
            }
            storeAvatar -> {
                val options = message.decodeOptions<StoreAvatarOptions>() ?: return true
                return message.responseSuccess(profileDataSource.storeAvatar(options))
            }
        }
        return false
    }

    // Post

    private suspend fun subscribeWithPost(message: ExtensionMessage): Boolean {
        when (message.method) {
            createPost -> {
                val options = message.decodeOptions<CreatePostOptions>() ?: return true
                return message.responseSuccess(postDataSource.createPost(options))
            }
            queryPost -> {
                val options = message.decodeOptions<QueryPostOptions>() ?: return true
                return message.responseSuccess(postDataSource.queryPost(options))
            }
            queryPosts -> {
                val options = message.decodeOptions<QueryPostsOptions>() ?: return true
                return message.responseSuccess(postDataSource.queryPosts(options))
            }
            updatePost -> {
                val options = message.decodeOptions<UpdatePostOptions>() ?: return true
                return message.responseSuccess(postDataSource.updatePost(options))
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
        private const val queryRelation = "query_relation"
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
            createRelation, queryRelation, queryRelations, updateRelation, deleteRelation,
            queryAvatar, storeAvatar,
            createPost, queryPost, queryPosts, updatePost,
            notifyVisibleDetectedProfileChanged,
        )
    }
}
