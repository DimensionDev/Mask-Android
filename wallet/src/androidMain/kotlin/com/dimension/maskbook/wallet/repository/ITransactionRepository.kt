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
package com.dimension.maskbook.wallet.repository

import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.ifNullOrEmpty
import com.dimension.maskbook.debankapi.model.ChainID
import com.dimension.maskbook.debankapi.model.TokenDict
import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.TokenData
import com.dimension.maskbook.wallet.export.model.WalletCollectibleData
import com.dimension.maskbook.wallet.export.model.WalletData
import com.dimension.maskbook.wallet.ext.chainType
import com.dimension.maskbook.wallet.ext.dbank
import com.dimension.maskbook.wallet.services.WalletServices
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.roundToLong

interface ITransactionRepository {
    suspend fun getTransactionByToken(
        walletData: WalletData,
        tokenData: TokenData
    ): List<TransactionData>

    suspend fun getTransactionByWallet(walletData: WalletData): List<TransactionData>

    suspend fun getTransactionByCollectible(
        walletData: WalletData,
        collectible: WalletCollectibleData
    ): List<TransactionData>
}

class TransactionRepository(
    private val walletRepository: IWalletRepository,
    private val walletServices: WalletServices,
) : ITransactionRepository {

    override suspend fun getTransactionByToken(
        walletData: WalletData,
        tokenData: TokenData
    ): List<TransactionData> {
        val isNativeToken = walletRepository.getChainData(tokenData.chainType)
            .firstOrNull()?.nativeToken?.address == tokenData.address
        return getTransactionByWalletAndChainType(
            walletData = walletData,
            chainType = tokenData.chainType,
            getTokenId = {
                it?.id ?: if (isNativeToken) tokenData.address else ""
            }
        )
    }

    override suspend fun getTransactionByWallet(walletData: WalletData): List<TransactionData> {
        val current =
            walletRepository.dWebData.firstOrNull()?.chainType ?: return emptyList()
        return getTransactionByWalletAndChainType(
            walletData = walletData,
            chainType = current,
            getTokenId = { it?.innerId.ifNullOrEmpty { it?.id ?: "" } }
        )
    }

    override suspend fun getTransactionByCollectible(
        walletData: WalletData,
        collectible: WalletCollectibleData
    ): List<TransactionData> {
        return walletServices.etherscanServices.assetEvent(collectible.contract.address, walletData.address).result?.map {
            val type = if (it.from?.lowercase() == "0x0000000000000000000000000000000000000000") {
                TransactionType.Mint
            } else if (it.from?.lowercase() == walletData.address.lowercase()) {
                TransactionType.Send
            } else if (it.to?.lowercase() == walletData.address.lowercase()) {
                TransactionType.Receive
            } else {
                TransactionType.Approve
            }
            TransactionData(
                id = it.hash.orEmpty(),
                type = type,
                count = it.gas?.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                status = TransactionStatus.Success,
                message = type.name,
                createdAt = (it.timeStamp?.toLongOrNull() ?: 0L) * 1000,
                updatedAt = (it.timeStamp?.toLongOrNull() ?: 0L) * 1000,
                tokenData = TransactionTokenData(
                    chainType = ChainType.eth,
                    symbol = it.tokenSymbol.orEmpty(),
                    price = BigDecimal.ZERO,
                    id = it.tokenID.orEmpty(),
                    contractId = it.contractAddress.orEmpty(),
                ),
                price = it.gasPrice?.toBigDecimalOrNull() ?: BigDecimal.ZERO,
            )
        } ?: emptyList()
    }

    private suspend fun getTransactionByWalletAndChainType(
        walletData: WalletData,
        chainType: ChainType,
        getTokenId: (token: TokenDict?) -> String
    ): List<TransactionData> {
        val chainId = try {
            chainType.dbank
        } catch (ignored: Throwable) {
            return emptyList()
        }
        val result =
            walletServices.debankServices.history(chainId, walletData.address.lowercase())
        return result.data?.historyList?.map {
            val type = when {
                it.cateID == "approve" -> TransactionType.Approve
                it.cateID == "cancel" -> TransactionType.Cancel
                it.cateID == "receive" -> TransactionType.Receive
                it.cateID == "send" -> TransactionType.Send
                it.receives?.any { it.fromAddr == "0x0000000000000000000000000000000000000000" } == true -> TransactionType.Receive
                it.receives?.any { it.fromAddr == walletData.address } == true -> TransactionType.Send
                it.sends?.any { it.toAddr == walletData.address } == true -> TransactionType.Receive
                else -> TransactionType.Unknown
            }
            val tokenId = when (type) {
                TransactionType.Send -> {
                    it.sends?.firstOrNull()?.tokenID
                }
                TransactionType.Receive -> {
                    it.receives?.firstOrNull()?.tokenID
                }
                else ->
                    it.tokenApprove?.tokenID
                        ?: it.receives?.firstOrNull()?.tokenID
                        ?: it.sends?.firstOrNull()?.tokenID
            }
            val tokenData = result.data?.tokenDict?.get(tokenId).let { token ->
                TransactionTokenData(
                    id = getTokenId(token),
                    chainType = token?.chain?.name?.let { ChainID.valueOf(it).chainType } ?: chainType,
                    symbol = token?.symbol.ifNullOrEmpty { token?.name ?: "" },
                    price = java.math.BigDecimal(token?.price ?: 0.0),
                    contractId = token?.contractId ?: ""
                )
            }
            val count = java.math.BigDecimal(
                when (type) {
                    TransactionType.Send -> it.sends?.firstOrNull()?.amount ?: 0.0
                    TransactionType.Receive -> it.receives?.firstOrNull()?.amount ?: 0.0
                    TransactionType.Approve -> it.tx?.ethGasFee ?: 0.0
                    else -> it.tx?.value?.takeIf { it != 0.0 } ?: it.sends?.firstOrNull()?.amount?.takeIf { it != 0.0 }
                        ?: it.receives?.firstOrNull()?.amount?.takeIf { it != 0.0 } ?: 0.0
                }
            )
            TransactionData(
                id = it.id ?: "",
                createdAt = (it.timeAt?.roundToLong() ?: 0L) * 1000,
                updatedAt = (it.timeAt?.roundToLong() ?: 0L) * 1000,
                type = type,
                count = count,
                status = TransactionStatus.Success,
                message = it.tx?.name ?: "",
                tokenData = tokenData,
                price = when (type) {
                    TransactionType.Approve -> BigDecimal(it.tx?.usdGasFee ?: 0.0)
                    else -> count * tokenData.price
                }
            )
        } ?: emptyList()
    }
}
