package com.dimension.maskbook.wallet.repository.model

data class TransakConfig(
    val isStaging: Boolean,
    val walletAddress: String,
    val defaultCryptoCurrency: String = "ETH",
    val hideMenu: Boolean = true
) {
    val url: String
        get() = "https://$host${queryString()}"

    val host: String
        get() = if (isStaging) "staging-global.transak.com" else "global.transak.com"

    fun queryString(): String {
        //TODO add apiKey in order to integrate all other parameters
        return "?walletAddress=${walletAddress}" +
                "&defaultCryptoCurrency=${defaultCryptoCurrency}" +
                "&hideMenu=${hideMenu}" +
                if (isStaging) "apiKey=4fcd6904-706b-4aff-bd9d-77422813bbb7&environment=STAGING" else ""
    }

    companion object {
        fun host(isStaging: Boolean) = if (isStaging) "staging-global.transak.com" else "global.transak.com"
    }
}
