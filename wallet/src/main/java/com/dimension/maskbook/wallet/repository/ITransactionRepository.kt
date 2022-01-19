package com.dimension.maskbook.wallet.repository

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
        return getTransactionByWallet(walletData).filter {
            it.tokenData == tokenData
        }
    }

    override suspend fun getTransactionByWallet(walletData: WalletData): List<TransactionData> {
        val current =
            walletRepository.dWebData.firstOrNull()?.chainType?.dbank ?: return emptyList()
        val result =
            walletServices.debankServices.history(current, walletData.address.lowercase())
        return result.data?.historyList?.mapNotNull {
            val tokenId = it.tokenApprove?.tokenID
                ?: it.receives?.firstOrNull()?.tokenID
                ?: it.sends?.firstOrNull()?.tokenID
            val tokenData = result.data?.tokenDict?.get(tokenId).let { token ->
                TokenData(
                    address = token?.id ?: "",
                    chainId = token?.chain?.name ?: "eth",
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
                    else -> TransactionType.Approve
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