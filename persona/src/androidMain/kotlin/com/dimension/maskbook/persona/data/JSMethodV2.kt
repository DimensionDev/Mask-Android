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
import com.dimension.maskbook.persona.migrator.model.IndexedDBAllRecord
import com.dimension.maskbook.persona.model.options.CreatePersonaOptions
import com.dimension.maskbook.persona.model.options.DeletePersonaOptions
import com.dimension.maskbook.persona.model.options.QueryPersonaByProfileOptions
import com.dimension.maskbook.persona.model.options.QueryPersonaOptions
import com.dimension.maskbook.persona.model.options.QueryPersonasOptions
import com.dimension.maskbook.persona.model.options.UpdatePersonaOptions
import com.dimension.maskbook.persona.repository.JsPersonaRepository
import io.github.aakira.napier.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class JSMethodV2(
    private val scope: CoroutineScope,
    private val services: ExtensionServices,
    private val personaRepository: JsPersonaRepository,
) {
    fun startSubscribe() {
        scope.launch {
            val dbRecords: IndexedDBAllRecord? = services.execute("get_all_indexedDB_records")
            // TODO migrate db
        }

        services.extensionMessage
            .onEach {
                subscribeWithPersona(it) ||
                    subscribeWithProfile(it) ||
                    subscribeWithRelation(it) ||
                    subscribeWithAvatar(it) ||
                    subscribeWithPost(it)
            }
            .catch { log(throwable = it) { "subscribe error:" } }
            .launchIn(scope)
    }

    // Persona

    private suspend fun subscribeWithPersona(message: ExtensionMessage): Boolean {
        when (message.method) {
            "create_persona" -> {
                val options: CreatePersonaOptions = message.params?.decodeJson() ?: return true
                message.responseSuccess(personaRepository.createPersona(options))
                return true
            }
            "query_persona" -> {
                val options: QueryPersonaOptions = message.params?.decodeJson() ?: return true
                message.responseSuccess(personaRepository.queryPersona(options))
                return true
            }
            "query_persona_by_profile" -> {
                val options: QueryPersonaByProfileOptions = message.params?.decodeJson() ?: return true
                message.responseSuccess(personaRepository.queryPersonaByProfile(options))
                return true
            }
            "query_personas" -> {
                val options: QueryPersonasOptions = message.params?.decodeJson() ?: return true
                message.responseSuccess(personaRepository.queryPersonas(options))
                return true
            }
            "update_persona" -> {
                val options: UpdatePersonaOptions = message.params?.decodeJson() ?: return true
                message.responseSuccess(personaRepository.updatePersona(options))
                return true
            }
            "delete_persona" -> {
                val options: DeletePersonaOptions = message.params?.decodeJson() ?: return true
                message.responseSuccess(personaRepository.deletePersona(options))
                return true
            }
        }
        return false
    }

    // Profile

    private suspend fun subscribeWithProfile(message: ExtensionMessage): Boolean {
        when (message.method) {
            "create_profile" -> {
                // message.params -> CreateProfileOptions
                // return ProfileRecord?
            }
            "query_profile" -> {
                // message.params -> QueryProfileOptions
                // return ProfileRecord?
            }
            "query_profiles" -> {
                // message.params -> QueryProfilesOptions
                // return ProfileRecord[]
            }
            "update_profile" -> {
                // message.params -> UpdateProfileOptions
                // return ProfileRecord?
            }
            "delete_profile" -> {
                // message.params -> DeleteProfileOptions
                // return Unit
            }
            "attachProfile" -> {
                // message.params -> AttachProfileOptions
                // return Unit
            }
            "detachProfile" -> {
                // message.params -> DetachProfileOptions
                // return Unit
            }
        }
        return false
    }

    // Relation

    private suspend fun subscribeWithRelation(message: ExtensionMessage): Boolean {
        when (message.method) {
            "create_relation" -> {
                // message.params -> CreateRelationOptions
                // return RelationRecord?
            }
            "query_relations" -> {
                // message.params -> QueryRelationsOptions
                // return RelationRecord[]
            }
            "update_relation" -> {
                // message.params -> UpdateRelationOptions
                // return RecordRelation?
            }
            "delete_relation" -> {
                // message.params -> DeleteRelationOptions
                // return Unit
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

private fun <T> ExtensionMessage.responseSuccess(result: T) {
    response(
        ExtensionResponseMessage.success(
            messageId = id,
            jsonrpc = "2.0",
            payloadId = "",
            result = result,
        )
    )
}
