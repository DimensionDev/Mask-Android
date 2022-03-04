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
package com.dimension.maskbook.wallet.export.model

data class WalletCollectibleCollectionData(
    val slug: String,
    val name: String,
    val imageUrl: String,
    val walletId: String,
    val chainType: ChainType,
) {
    companion object
}

data class WalletCollectibleData(
    val id: String,
    val chainType: ChainType,
    val icon: String,
    val name: String,
    val tokenId: String,
    val link: String,
    val previewUrl: String?,
    val imageUrl: String?,
    val videoUrl: String?,
    val contract: WalletCollectibleContract,
    val collection: WalletCollectibleCollectionData
) : TradableData {
    override fun tradableId() = id

    companion object
}

data class WalletCollectibleContract(
    val address: String,
    val imageUrl: String,
    val name: String,
    val symbol: String
)
