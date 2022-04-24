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
package com.dimension.maskbook.labs.mapper

import com.dimension.maskbook.common.bigDecimal.BigDecimal
import com.dimension.maskbook.common.ext.humanizeToken
import com.dimension.maskbook.common.ext.onDrawableRes
import com.dimension.maskbook.labs.R
import com.dimension.maskbook.labs.model.RedPacketAvailabilityState
import com.dimension.maskbook.labs.model.RedPacketState
import com.dimension.maskbook.labs.model.options.RedPacketOptions
import com.dimension.maskbook.labs.model.ui.UiLuckyDropData
import com.dimension.maskbook.wallet.export.model.ChainData
import com.dimension.maskbook.wallet.export.model.WalletData
import org.joda.time.DateTime
import java.util.Locale
import java.util.TimeZone
import kotlin.math.pow

private const val passwordInvalid = "PASSWORD INVALID"

fun RedPacketOptions.RedPacketAvailability.toRedPacketState(): RedPacketAvailabilityState {
    val isExpired = expired
    val isEmpty = balance.compareTo(BigDecimal.ZERO) == 0
    val isClaimed = claimedAmount > BigDecimal.ZERO
    val isRefunded = isEmpty && claimed < total
    return RedPacketAvailabilityState(
        isExpired = isExpired,
        isEmpty = isEmpty,
        isClaimed = isClaimed,
        isRefunded = isRefunded,
        claimedAmount = claimedAmount,
        claimed = claimed,
    )
}

fun RedPacketOptions.toRedPacketState(
    currentWallet: WalletData,
    currentChain: ChainData,
): RedPacketState {
    val availabilityState = availability.toRedPacketState()
    val isExpired = availabilityState.isExpired
    val isEmpty = availabilityState.isEmpty
    val isClaimed = availabilityState.isClaimed
    val isRefunded = availabilityState.isRefunded

    val isCreator = payload.sender.address == currentWallet.address
    val isPasswordValid = payload.password.isNotEmpty() && payload.password != passwordInvalid
    val isOnSameChain = payload.token.chainId == currentChain.chainId

    // same as ios
    val canClaim = !isExpired && !isEmpty && !isClaimed && isPasswordValid && isOnSameChain
    val canRefund = isExpired && !isEmpty && isCreator && isOnSameChain
    val canSend = !isEmpty && !isExpired && !isRefunded && isCreator && isOnSameChain

    return RedPacketState(
        isExpired = isExpired,
        isEmpty = isEmpty,
        isClaimed = isClaimed,
        isRefunded = isRefunded,
        isCreator = isCreator,
        isPasswordValid = isPasswordValid,
        isOnSameChain = isOnSameChain,
        canClaim = canClaim,
        canRefund = canRefund,
        canSend = canSend,
    )
}

fun RedPacketOptions.toUiLuckyDropData(
    currentWallet: WalletData,
    currentChain: ChainData,
): UiLuckyDropData {
    val decimals = payload.token.decimals
    val symbol = payload.token.symbol

    val amount = payload.total.divide(BigDecimal(10.0.pow(decimals)))
    val endTime = DateTime(payload.creationTime + payload.duration)
    val timeZone = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
    val state = toRedPacketState(currentWallet, currentChain)
    return UiLuckyDropData(
        wallet = UiLuckyDropData.Wallet(
            name = currentWallet.name,
            address = currentWallet.address,
            chainId = currentChain.chainId,
            chainType = currentChain.chainType,
            chainTypeIcon = currentChain.chainType.onDrawableRes,
            chainBalance = currentWallet.tokens
                .find { it.tokenAddress == currentChain.nativeTokenID }
                ?.let { "${it.count.humanizeToken()} ${it.tokenData.symbol}" }
                ?: "",
            decimals = decimals,
            symbol = symbol,
        ),
        redPacket = UiLuckyDropData.RedPacket(
            contractAddress = payload.contractAddress,
            message = payload.sender.message,
            senderName = payload.sender.name,
            shares = payload.shares,
            amount = amount,
            amountString = "$amount $symbol",
            endTime = endTime.toString("yyyy/MM/dd hh a", Locale.US) + " " + timeZone,
            stateStringRes = when {
                state.isClaimed -> R.string.scene_open_red_package_claimed
                state.isRefunded -> R.string.scene_open_red_package_refunded
                state.isExpired -> R.string.scene_open_red_package_expired
                state.isEmpty -> R.string.scene_open_red_package_empty
                else -> R.string.scene_open_red_package_on_going
            },
            rpId = payload.rpId,
            password = payload.password,
            canClaim = state.canClaim,
            canSend = state.canSend,
            canRefund = state.canRefund,
            postLink = postLink,
            tokenName = payload.token.name,
        ),
        buttonEnabled = state.canClaim || state.canRefund,
        buttonStringRes = when {
            state.canClaim -> R.string.scene_open_red_package_claim
            state.canRefund -> R.string.scene_open_red_package_refund
            state.isExpired -> R.string.scene_open_red_package_expired
            !state.isOnSameChain -> R.string.scene_open_red_package_wrong_network
            else -> 0
        }
    )
}
