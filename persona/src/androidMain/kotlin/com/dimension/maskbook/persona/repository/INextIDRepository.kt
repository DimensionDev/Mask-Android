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
package com.dimension.maskbook.persona.repository

import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.common.ext.encodeJson
import com.dimension.maskbook.common.retrofit.retrofit
import com.dimension.maskbook.persona.services.CreateProofParams
import com.dimension.maskbook.persona.services.NextIDResponseError
import com.dimension.maskbook.persona.services.NextIDService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Action {
    Create,
    Delete
}

object NextIDPlatform {
    enum class Social {
        Twitter,
        Facebook,
        Instagram,
        Minds,
        Phone,
        Email,
    }

    enum class Wallet {
        Eth,
        WalletConnect
    }
}

data class NextIDProof(
    val persona: String,
    val proofs: List<Proof>
) {
    data class Proof(
        val identity: String,
        val platform: String
    )
}

@kotlinx.serialization.Serializable
data class WalletProofExtra(
    val wallet_signature: String,
    val signature: String,
)

interface INextIDRepository {
    fun modifySocialProof(
        action: Action,
        platform: NextIDPlatform.Social,
        identity: String,
        proofLocation: String,
        publicKey: String,
        extra: String? = null
    )

    fun modifyWalletProof(
        action: Action,
        platform: NextIDPlatform.Wallet,
        identity: String,
        proofLocation: String,
        publicKey: String,
        extra: WalletProofExtra? = null
    )

    suspend fun getProofs(
        platform: String? = null,
        identity: List<String>
    ): List<NextIDProof>
}

class NextIDRepository : INextIDRepository {
    private val baseUrl = "https://proof-service.next.id"
    private val service = retrofit<NextIDService>(baseUrl)
    private val scope = CoroutineScope(Dispatchers.IO)
    override fun modifySocialProof(
        action: Action,
        platform: NextIDPlatform.Social,
        identity: String,
        proofLocation: String,
        publicKey: String,
        extra: String?
    ) = modifyProof(
        action = action,
        platform = platform.name.lowercase(),
        identity = identity,
        proofLocation = proofLocation,
        publicKey = publicKey,
        extra = extra
    )

    override fun modifyWalletProof(
        action: Action,
        platform: NextIDPlatform.Wallet,
        identity: String,
        proofLocation: String,
        publicKey: String,
        extra: WalletProofExtra?
    ) = modifyProof(
        action = action,
        platform = platform.name.lowercase(),
        identity = identity,
        proofLocation = proofLocation,
        publicKey = publicKey,
        extra = extra.encodeJson()
    )

    private fun modifyProof(
        action: Action,
        platform: String,
        identity: String,
        proofLocation: String,
        publicKey: String,
        extra: String?
    ) {
        scope.launch {
            val resp = service.modifyProof(
                CreateProofParams(
                    action = action.name.lowercase(),
                    platform = platform,
                    identity = identity,
                    proof_location = proofLocation,
                    public_key = publicKey,
                    extra = extra
                )
            )
            if (!resp.isSuccessful) {
                try {
                    val body = resp.errorBody()
                    throw Error(body?.string()?.decodeJson<NextIDResponseError>()?.message ?: "Unknown error")
                } catch (e: Exception) {
                    throw e
                }
            }
        }
    }

    override suspend fun getProofs(platform: String?, identity: List<String>) = withContext(scope.coroutineContext) {
        service.getProof(
            identity = identity.joinToString { it },
            platform = platform
        ).ids?.mapNotNull {
            NextIDProof(
                persona = it.persona ?: return@mapNotNull null,
                proofs = it.proofs?.map {
                    NextIDProof.Proof(
                        identity = it.identity ?: "",
                        platform = it.platform ?: ""
                    )
                } ?: emptyList()
            )
        } ?: emptyList()
    }
}
