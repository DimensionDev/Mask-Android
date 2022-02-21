package com.dimension.maskbook.persona.repository

import com.dimension.maskbook.common.retrofit.retrofit
import com.dimension.maskbook.persona.services.NextIDService

enum class Action{
    Create,
    Delete
}

object NextIDPlatform {
    enum class Social{
        Twitter,
        Facebook,
        Instagram,
        Minds,
        Phone,
        Email,
    }

    enum class Wallet{
        Eth,
        WalletConnect
    }
}

interface INextIDRepository {
    fun createSocialProof(
        action: Action,
        platform: NextIDPlatform,
        identity: String,
        proofLocation: String,
        publicKey: String,
        extra: String
    )
}

class NextIDRepository : INextIDRepository {
    private val baseUrl = "https://proof-service.next.id"
    private val service = retrofit<NextIDService>(baseUrl)

    override fun createSocialProof(
        action: Action,
        platform: NextIDPlatform,
        identity: String,
        proofLocation: String,
        publicKey: String,
        extra: String
    ) {
        TODO("Not yet implemented")
    }

}