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
package com.dimension.maskbook.wallet.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.dimension.maskbook.wallet.export.model.ChainType

@Entity(
    indices = [Index(value = ["walletId", "tokenId", "id"], unique = true)],
)
data class DbCollectible(
    @PrimaryKey val _id: String,
    val walletId: String,
    val chainType: ChainType,
    val tokenId: String,
    val externalLink: String? = null,
    val permalink: String? = null,
    val id: Long,
    val description: String? = null,
    val name: String,
    @Embedded(prefix = "creator_")
    val creator: DbCollectibleCreator,
    @Embedded(prefix = "collection_")
    val collection: DbCollection,
    @Embedded(prefix = "contract_")
    val contract: DbCollectibleContract,
    @Embedded(prefix = "url_")
    val url: DbCollectibleUrl,
)

data class DbCollection(
    val imageURL: String? = null,
    val name: String? = null,
    val slug: String,
)

data class DbCollectibleCreator(
    val userName: String? = null,
    val profileImgURL: String? = null,
    val address: String? = null,
    val config: String? = null
)

data class DbCollectibleUrl(
    val imageURL: String? = null,
    val imagePreviewURL: String? = null,
    val imageThumbnailURL: String? = null,
    val imageOriginalURL: String? = null,
    val animationURL: String? = null,
    val animationOriginalURL: String? = null,
)

data class DbCollectibleContract(
    val address: String,
    val imageUrl: String,
    val name: String,
    val symbol: String,
)
