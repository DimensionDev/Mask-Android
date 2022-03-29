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
package com.dimension.maskbook.persona.datasource

import com.dimension.maskbook.persona.db.PersonaDatabase
import com.dimension.maskbook.persona.db.migrator.mapper.toDbPostRecord
import com.dimension.maskbook.persona.db.migrator.mapper.toIndexedDBPost
import com.dimension.maskbook.persona.db.sql.buildQueryPostSql
import com.dimension.maskbook.persona.model.indexed.IndexedDBPost
import com.dimension.maskbook.persona.model.options.CreatePostOptions
import com.dimension.maskbook.persona.model.options.QueryPostOptions
import com.dimension.maskbook.persona.model.options.QueryPostsOptions
import com.dimension.maskbook.persona.model.options.UpdatePostOptions

class JsPostDataSource(database: PersonaDatabase) {

    private val postDao = database.postDao()

    suspend fun createPost(options: CreatePostOptions): IndexedDBPost {
        val newPost = options.post.toDbPostRecord()
        postDao.insert(newPost)
        return options.post
    }

    suspend fun queryPost(options: QueryPostOptions): IndexedDBPost? {
        return postDao.find(options.identifier)?.toIndexedDBPost()
    }

    suspend fun queryPosts(options: QueryPostsOptions): List<IndexedDBPost> {
        val query = buildQueryPostSql(
            encryptBy = options.encryptBy,
            userIds = options.userIds,
            network = options.network,
            pageOptions = options.pageOptions,
        )
        return postDao.findListRaw(query).map {
            it.toIndexedDBPost()
        }
    }

    suspend fun updatePost(options: UpdatePostOptions): List<IndexedDBPost> {
        val oldPost = postDao.find(options.post.identifier)
        val newPost = options.post.toDbPostRecord()

        if (oldPost == null) {
            postDao.insert(newPost)
            return listOf(options.post)
        }

        // 0: append, 1:override
        when (options.options.mode) {
            0 -> {
                if (oldPost.recipientsRaw != null) {
                    oldPost.recipientsRaw!!.putAll(newPost.recipientsRaw ?: emptyMap())
                } else {
                    oldPost.recipientsRaw = newPost.recipientsRaw
                }
            }
            1 -> { oldPost.recipientsRaw = newPost.recipientsRaw }
        }

        postDao.insert(oldPost)
        return listOf(oldPost.toIndexedDBPost())
    }
}
