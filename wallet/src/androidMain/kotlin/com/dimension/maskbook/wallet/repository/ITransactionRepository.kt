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

import com.dimension.maskbook.wallet.export.model.ChainType
import com.dimension.maskbook.wallet.export.model.TokenData
import com.dimension.maskbook.wallet.export.model.WalletData
import com.dimension.maskbook.wallet.services.WalletServices
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.roundToLong

interface ITransactionRepository {
    suspend fun getTransactionByToken(
        walletData: WalletData,
        tokenData: TokenData
    ): List<TransactionData>

    suspend fun getTransactionByWallet(walletData: WalletData): List<TransactionData>
}

class TransactionRepository(
    private val walletRepository: IWalletRepository,
    private val walletServices: WalletServices,
) : ITransactionRepository {

    override suspend fun getTransactionByToken(
        walletData: WalletData,
        tokenData: TokenData
    ): List<TransactionData> {
        return getTransactionByWalletAndChainType(
            walletData = walletData,
            chainType = tokenData.chainType
        ).filter {
            it.tokenData == tokenData
        }
    }

    override suspend fun getTransactionByWallet(walletData: WalletData): List<TransactionData> {
        val current =
            walletRepository.dWebData.firstOrNull()?.chainType ?: return emptyList()
        return getTransactionByWalletAndChainType(walletData = walletData, chainType = current)
    }

    private suspend fun getTransactionByWalletAndChainType(walletData: WalletData, chainType: ChainType): List<TransactionData> {
        val chainId = chainType.dbank
        val result =
            walletServices.debankServices.history(chainId, walletData.address.lowercase())
        return result.data?.historyList?.mapNotNull {
            val tokenId = it.tokenApprove?.tokenID
                ?: it.receives?.firstOrNull()?.tokenID
                ?: it.sends?.firstOrNull()?.tokenID
            val tokenData = result.data?.tokenDict?.get(tokenId).let { token ->
                TokenData(
                    address = token?.id ?: "",
                    chainType = token?.chain?.name?.toChainType() ?: ChainType.unknown,
                    name = token?.name ?: "",
                    symbol = token?.symbol ?: "",
                    decimals = token?.decimals ?: 0,
                    logoURI = token?.logoURL,
                    price = java.math.BigDecimal(token?.price ?: 0.0)
                )
            }
            TransactionData(
                id = it.id ?: "",
                createdAt = (it.timeAt?.roundToLong() ?: 0L) * 1000,
                updatedAt = (it.timeAt?.roundToLong() ?: 0L) * 1000,
                type = when {
                    it.cateID == "approve" -> TransactionType.Approve
                    it.cateID == "cancel" -> TransactionType.Cancel
                    it.cateID == "receive" -> TransactionType.Receive
                    it.cateID == "send" -> TransactionType.Send
                    it.receives?.any { it.fromAddr == "0x0000000000000000000000000000000000000000" } == true -> TransactionType.Receive
                    it.receives?.any { it.fromAddr == walletData.address } == true -> TransactionType.Send
                    it.sends?.any { it.toAddr == walletData.address } == true -> TransactionType.Receive
                    else -> TransactionType.Unknown
                },
                count = java.math.BigDecimal(
                    it.tx?.value ?: it.sends?.firstOrNull()?.amount
                        ?: it.receives?.firstOrNull()?.amount ?: 0.0
                ),
                status = TransactionStatus.Success,
                message = it.tx?.name ?: "",
                tokenData = tokenData,
            )
        } ?: emptyList()
    }
}
