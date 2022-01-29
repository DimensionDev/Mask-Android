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
package com.dimension.maskbook.wallet.services.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenSeaAssetModel(
    val assets: List<AssetElement>? = null
)

@Serializable
data class AssetElement(
    val id: Long? = null,

    @SerialName("token_id")
    val tokenID: String? = null,

    @SerialName("num_sales")
    val numSales: Long? = null,

    @SerialName("background_color")
    val backgroundColor: String? = null,

    @SerialName("image_url")
    val imageURL: String? = null,

    @SerialName("image_preview_url")
    val imagePreviewURL: String? = null,

    @SerialName("image_thumbnail_url")
    val imageThumbnailURL: String? = null,

    @SerialName("image_original_url")
    val imageOriginalURL: String? = null,

    @SerialName("animation_url")
    val animationURL: String? = null,

    @SerialName("animation_original_url")
    val animationOriginalURL: String? = null,

    val name: String? = null,
    val description: String? = null,

    @SerialName("external_link")
    val externalLink: String? = null,

    @SerialName("asset_contract")
    val assetContract: AssetContract? = null,

    val permalink: String? = null,
    val collection: Collection? = null,
    val decimals: Long? = null,

    @SerialName("token_metadata")
    val tokenMetadata: String? = null,

    val owner: Creator? = null,

    @SerialName("sell_orders")
    val sellOrders: List<SellOrder>? = null,

    val creator: Creator? = null,
    val traits: List<Trait>? = null,

    @SerialName("last_sale")
    val lastSale: LastSale? = null,

//    @SerialName("top_bid")
//    val topBid: JsonObject? = null,

//    @SerialName("listing_date")
//    val listingDate: JsonObject? = null,

    @SerialName("is_presale")
    val isPresale: Boolean? = null,

    @SerialName("transfer_fee_payment_token")
    val transferFeePaymentToken: OpenSeaFungibleToken? = null,

    @SerialName("transfer_fee")
    val transferFee: String? = null
)

@Serializable
data class LastSale(
    val asset: LastSaleAsset? = null,

//    @SerialName("asset_bundle")
//    val assetBundle: JsonObject? = null,

    @SerialName("event_type")
    val eventType: String? = null,

    @SerialName("event_timestamp")
    val eventTimestamp: String? = null,

//    @SerialName("auction_type")
//    val auctionType: JsonObject? = null,

    @SerialName("total_price")
    val totalPrice: String? = null,

    @SerialName("payment_token")
    val paymentToken: PaymentToken? = null,

    val transaction: TransactionClass? = null,

    @SerialName("created_date")
    val createdDate: String? = null,

    val quantity: String? = null
)

@Serializable
data class LastSaleAsset(
    @SerialName("token_id")
    val tokenID: String? = null,

    val decimals: Long? = null
)

@Serializable
data class PaymentToken(
    val id: Long? = null,
    val symbol: String? = null,
    val address: String? = null,

    @SerialName("image_url")
    val imageURL: String? = null,

    val name: String? = null,
    val decimals: Long? = null,

    @SerialName("eth_price")
    val ethPrice: String? = null,

    @SerialName("usd_price")
    val usdPrice: String? = null
)

@Serializable
data class TransactionClass(
    @SerialName("block_hash")
    val blockHash: String? = null,

    @SerialName("block_number")
    val blockNumber: String? = null,

    @SerialName("from_account")
    val fromAccount: Creator? = null,

    val id: Long? = null,
    val timestamp: String? = null,

    @SerialName("to_account")
    val toAccount: Creator? = null,

    @SerialName("transaction_hash")
    val transactionHash: String? = null,

    @SerialName("transaction_index")
    val transactionIndex: String? = null
)

@Serializable
data class OpenSeaFungibleToken(
    val image_url: String?,
    val eth_price: String?,
    val usd_price: String?,
    val name: String?,
    val symbol: String?,
    val decimals: Long?,
    val address: String?,
)

@Serializable
data class AssetContract(
    val address: String? = null,

    @SerialName("asset_contract_type")
    val assetContractType: String? = null,

    @SerialName("created_date")
    val createdDate: String? = null,

    val name: String? = null,

    @SerialName("nft_version")
    val nftVersion: String? = null,

    @SerialName("opensea_version")
    val openseaVersion: String? = null,

    val owner: Long? = null,

    @SerialName("schema_name")
    val schemaName: String? = null,

    val symbol: String? = null,

    @SerialName("total_supply")
    val totalSupply: String? = null,

    val description: String? = null,

    @SerialName("external_link")
    val externalLink: String? = null,

    @SerialName("image_url")
    val imageURL: String? = null,

    @SerialName("default_to_fiat")
    val defaultToFiat: Boolean? = null,

    @SerialName("dev_buyer_fee_basis_points")
    val devBuyerFeeBasisPoints: Long? = null,

    @SerialName("dev_seller_fee_basis_points")
    val devSellerFeeBasisPoints: Long? = null,

    @SerialName("only_proxied_transfers")
    val onlyProxiedTransfers: Boolean? = null,

    @SerialName("opensea_buyer_fee_basis_points")
    val openseaBuyerFeeBasisPoints: Long? = null,

    @SerialName("opensea_seller_fee_basis_points")
    val openseaSellerFeeBasisPoints: Long? = null,

    @SerialName("buyer_fee_basis_points")
    val buyerFeeBasisPoints: Long? = null,

    @SerialName("seller_fee_basis_points")
    val sellerFeeBasisPoints: Long? = null,

    @SerialName("payout_address")
    val payoutAddress: String? = null
)

@Serializable
data class Collection(
    @SerialName("banner_image_url")
    val bannerImageURL: String? = null,

    @SerialName("chat_url")
    val chatURL: String? = null,

    @SerialName("created_date")
    val createdDate: String? = null,

    @SerialName("default_to_fiat")
    val defaultToFiat: Boolean? = null,

    val description: String? = null,

    @SerialName("dev_buyer_fee_basis_points")
    val devBuyerFeeBasisPoints: String? = null,

    @SerialName("dev_seller_fee_basis_points")
    val devSellerFeeBasisPoints: String? = null,

    @SerialName("discord_url")
    val discordURL: String? = null,

    @SerialName("display_data")
    val displayData: DisplayData? = null,

    @SerialName("external_url")
    val externalURL: String? = null,

    val featured: Boolean? = null,

    @SerialName("featured_image_url")
    val featuredImageURL: String? = null,

    val hidden: Boolean? = null,

    @SerialName("safelist_request_status")
    val safelistRequestStatus: String? = null,

    @SerialName("image_url")
    val imageURL: String? = null,

    @SerialName("is_subject_to_whitelist")
    val isSubjectToWhitelist: Boolean? = null,

    @SerialName("large_image_url")
    val largeImageURL: String? = null,

    @SerialName("medium_username")
    val mediumUsername: String? = null,

    val name: String? = null,

    @SerialName("only_proxied_transfers")
    val onlyProxiedTransfers: Boolean? = null,

    @SerialName("opensea_buyer_fee_basis_points")
    val openseaBuyerFeeBasisPoints: String? = null,

    @SerialName("opensea_seller_fee_basis_points")
    val openseaSellerFeeBasisPoints: String? = null,

    @SerialName("payout_address")
    val payoutAddress: String? = null,

    @SerialName("require_email")
    val requireEmail: Boolean? = null,

    @SerialName("short_description")
    val shortDescription: String? = null,

    val slug: String? = null,

    @SerialName("telegram_url")
    val telegramURL: String? = null,

    @SerialName("twitter_username")
    val twitterUsername: String? = null,

    @SerialName("instagram_username")
    val instagramUsername: String? = null,

    @SerialName("wiki_url")
    val wikiURL: String? = null
)

@Serializable
data class DisplayData(
    @SerialName("card_display_style")
    val cardDisplayStyle: String? = null,

//    val images: JsonArray? = null
)

@Serializable
data class Creator(
    val user: User? = null,

    @SerialName("profile_img_url")
    val profileImgURL: String? = null,

    val address: String? = null,
    val config: String? = null
)

@Serializable
data class User(
    val username: String? = null
)

@Serializable
data class SellOrder(
    @SerialName("created_date")
    val createdDate: String? = null,

    @SerialName("closing_date")
    val closingDate: String? = null,

    @SerialName("closing_extendable")
    val closingExtendable: Boolean? = null,

    @SerialName("expiration_time")
    val expirationTime: Long? = null,

    @SerialName("listing_time")
    val listingTime: Long? = null,

    @SerialName("order_hash")
    val orderHash: String? = null,

    val metadata: OpenSeaMetadata? = null,
    val exchange: String? = null,
    val maker: FeeRecipient? = null,
    val taker: FeeRecipient? = null,

    @SerialName("current_price")
    val currentPrice: String? = null,

    @SerialName("current_bounty")
    val currentBounty: String? = null,

    @SerialName("bounty_multiple")
    val bountyMultiple: String? = null,

    @SerialName("maker_relayer_fee")
    val makerRelayerFee: String? = null,

    @SerialName("taker_relayer_fee")
    val takerRelayerFee: String? = null,

    @SerialName("maker_protocol_fee")
    val makerProtocolFee: String? = null,

    @SerialName("taker_protocol_fee")
    val takerProtocolFee: String? = null,

    @SerialName("maker_referrer_fee")
    val makerReferrerFee: String? = null,

    @SerialName("fee_recipient")
    val feeRecipient: FeeRecipient? = null,

    @SerialName("fee_method")
    val feeMethod: Long? = null,

    val side: Long? = null,

    @SerialName("sale_kind")
    val saleKind: Long? = null,

    val target: String? = null,

    @SerialName("how_to_call")
    val howToCall: Long? = null,

    val calldata: String? = null,

    @SerialName("replacement_pattern")
    val replacementPattern: String? = null,

    @SerialName("static_target")
    val staticTarget: String? = null,

    @SerialName("static_extradata")
    val staticExtradata: String? = null,

    @SerialName("payment_token")
    val paymentToken: String? = null,

    @SerialName("payment_token_contract")
    val paymentTokenContract: PaymentTokenContract? = null,

    @SerialName("base_price")
    val basePrice: String? = null,

    val extra: String? = null,
    val quantity: String? = null,
    val salt: String? = null,
    val v: Long? = null,
    val r: String? = null,
    val s: String? = null,

    @SerialName("approved_on_chain")
    val approvedOnChain: Boolean? = null,

    val cancelled: Boolean? = null,
    val finalized: Boolean? = null,

    @SerialName("marked_invalid")
    val markedInvalid: Boolean? = null,

    @SerialName("prefixed_hash")
    val prefixedHash: String? = null
)

@Serializable
data class FeeRecipient(
    val user: Long? = null,

    @SerialName("profile_img_url")
    val profileImgURL: String? = null,

    val address: String? = null,
    val config: String? = null
)

@Serializable
data class OpenSeaMetadata(
    val asset: MetadataAsset? = null,
    val schema: String? = null
)

@Serializable
data class MetadataAsset(
    val id: String? = null,
    val address: String? = null,
    val quantity: String? = null
)

@Serializable
data class PaymentTokenContract(
    val id: Long? = null,
    val symbol: String? = null,
    val address: String? = null,

    @SerialName("image_url")
    val imageURL: String? = null,

    val name: String? = null,
    val decimals: Long? = null,

    @SerialName("eth_price")
    val ethPrice: String? = null,

    @SerialName("usd_price")
    val usdPrice: String? = null
)

@Serializable
data class Trait(
    @SerialName("trait_type")
    val traitType: String? = null,

    val value: String? = null,

    @SerialName("display_type")
    val displayType: String? = null,

//    @SerialName("max_value")
//    val maxValue: JsonObject? = null,

    @SerialName("trait_count")
    val traitCount: Long? = null,

//    val order: JsonObject? = null
)
