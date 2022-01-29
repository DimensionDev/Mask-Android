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
package com.dimension.maskbook.debankapi.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Protocol(
    val id: String? = null,
    val chain: String? = null,
    val name: String? = null,

    @SerialName("site_url")
    val siteURL: String? = null,

    @SerialName("logo_url")
    val logoURL: String? = null,

    @SerialName("has_supported_portfolio")
    val hasSupportedPortfolio: Boolean? = null,

    val tvl: Double? = null,
    @SerialName("portfolio_item_list")
    val portfolioItemList: List<PortfolioItemList>? = null,

    @SerialName("net_usd_value")
    val netUsdValue: Double? = null,

    @SerialName("asset_usd_value")
    val assetUsdValue: Double? = null,

    @SerialName("debt_usd_value")
    val debtUsdValue: Long? = null
)

@Serializable
data class PortfolioItemList(
    val stats: Stats? = null,

    @SerialName("update_at")
    val updateAt: Double? = null,

    val name: String? = null,

    @SerialName("detail_types")
    val detailTypes: List<DetailType>? = null,

    val detail: Detail? = null,

    @SerialName("proxy_detail")
    val proxyDetail: ProxyDetail? = null
)

@Serializable
data class ProxyDetail(
    val project: Project? = null,

    @SerialName("proxy_contract_id")
    val proxyContractID: String? = null
)

@Serializable
data class Project(
    val id: String? = null,
    val name: String? = null,

    @SerialName("site_url")
    val siteURL: String? = null,

    @SerialName("logo_url")
    val logoURL: String? = null
)

@Serializable
data class Detail(
    @SerialName("supply_token_list")
    val supplyTokenList: List<Token>? = null,

    @SerialName("borrow_token_list")
    val borrowTokenList: List<Token>? = null,

    @SerialName("health_rate")
    val healthRate: Double? = null,

    @SerialName("reward_token_list")
    val rewardTokenList: List<Token>? = null,

    @SerialName("token_list")
    val tokenList: List<Token>? = null,

    @SerialName("debt_ratio")
    val debtRatio: Double? = null,

    val description: String? = null,

    @SerialName("unlock_at")
    val unlockAt: Long? = null,

    @SerialName("usd_value")
    val usdValue: Double? = null,

    @SerialName("expired_at")
    val expiredAt: Long? = null,

    @SerialName("collateral_token_list")
    val collateralTokenList: List<Token>? = null,

    @SerialName("collateral_rate")
    val collateralRate: Double? = null,

    val token: Token? = null,

    @SerialName("daily_unlock_amount")
    val dailyUnlockAmount: Double? = null,

    @SerialName("end_at")
    val endAt: Long? = null,

    val side: String? = null,

    @SerialName("base_token")
    val baseToken: Token? = null,

    @SerialName("quote_token")
    val quoteToken: Token? = null,

    @SerialName("position_token")
    val positionToken: Token? = null,

    @SerialName("margin_token")
    val marginToken: Token? = null,

    @SerialName("margin_rate")
    val marginRate: Double? = null,

    val leverage: Double? = null,

    @SerialName("daily_funding_rate")
    val dailyFundingRate: Double? = null,

    @SerialName("entry_price")
    val entryPrice: Double? = null,

    @SerialName("mark_price")
    val markPrice: Double? = null,

    @SerialName("liquidation_price")
    val liquidationPrice: String? = null,

    @SerialName("liquidate_price")
    val liquidatePrice: Double? = null,

    @SerialName("current_price")
    val currentPrice: Double? = null
)

@Serializable
enum class DetailType(val value: String) {
    Common("common"),
    InsuranceBuyer("insurance_buyer"),
    InsuranceSeller("insurance_seller"),
    Lending("lending"),
    LeveragedFarming("leveraged_farming"),
    Locked("locked"),
    Perpetuals("perpetuals"),
    Reward("reward"),
    Vesting("vesting");

    companion object : KSerializer<DetailType> {
        override val descriptor: SerialDescriptor
            get() {
                return PrimitiveSerialDescriptor("quicktype.DetailType", PrimitiveKind.STRING)
            }

        override fun deserialize(decoder: Decoder): DetailType =
            when (val value = decoder.decodeString()) {
                "common" -> Common
                "insurance_buyer" -> InsuranceBuyer
                "insurance_seller" -> InsuranceSeller
                "lending" -> Lending
                "leveraged_farming" -> LeveragedFarming
                "locked" -> Locked
                "perpetuals" -> Perpetuals
                "reward" -> Reward
                "vesting" -> Vesting
                else -> throw IllegalArgumentException("DetailType could not parse: $value")
            }

        override fun serialize(encoder: Encoder, value: DetailType) {
            return encoder.encodeString(value.value)
        }
    }
}

@Serializable
data class Stats(
    @SerialName("asset_usd_value")
    val assetUsdValue: Double? = null,

    @SerialName("debt_usd_value")
    val debtUsdValue: Double? = null,

    @SerialName("net_usd_value")
    val netUsdValue: Double? = null,

    @SerialName("daily_yield_usd_value")
    val dailyYieldUsdValue: Long? = null,

    @SerialName("daily_cost_usd_value")
    val dailyCostUsdValue: Long? = null,

    @SerialName("daily_net_yield_usd_value")
    val dailyNetYieldUsdValue: Long? = null
)
