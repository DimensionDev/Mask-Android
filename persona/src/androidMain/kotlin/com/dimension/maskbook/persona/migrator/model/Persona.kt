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
package com.dimension.maskbook.persona.migrator.model

import com.dimension.maskbook.persona.model.LinkedProfileDetailsState

@kotlinx.serialization.Serializable
data class Persona(
    val identifier: String,
    val linkedProfiles: Map<String, LinkedProfileDetails>,
    val nickname: String? = null,
    val privateKey: JsonWebKey? = null,
    val publicKey: JsonWebKey? = null,
    val localKey: JsonWebKey? = null,
    val mnemonic: Mnemonic? = null,
    val hasLogout: Boolean = false,
    val uninitialized: Boolean = false,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
) {
    @kotlinx.serialization.Serializable
    data class LinkedProfileDetails(
        val connectionConfirmState: LinkedProfileDetailsState
    )

    @kotlinx.serialization.Serializable
    data class Mnemonic(
        val words: String,
        val parameter: Parameter,
    ) {
        @kotlinx.serialization.Serializable
        data class Parameter(
            val path: String,
            val withPassword: Boolean,
        )
    }
}

//   struct Persona {
//     enum LinkedProfileConfirmState: String, Codable {
//         case pending, confirmed, denied
//     }
//
//     struct LinkedProfileDetails: Codable {
//         let connectionConfirmState: LinkedProfileConfirmState
//     }
//
//     struct Mnemonic: Codable {
//         enum CodingKeys: String, CodingKey {
//             case words
//             case parameter
//         }
//
//         struct Parameter: Codable {
//             enum CodingKeys: String, CodingKey {
//                 case path
//                 case withPassword
//             }
//
//             let path: String
//             let withPassword: Bool
//         }
//         let words: String
//         let parameter: Parameter
//     }
//
//     enum CodingKeys: String, CodingKey {
//         case identifier
//         case nickname
//         case privateKey
//         case publicKey
//         case localKey
//         case mnemonic
//         case hasLogout
//         case uninitialized
//         case createdAt
//         case updatedAt
//         case linkedProfiles
//     }
//
//     var createdAt: TimeInterval
//     var identifier: String
//     var linkedProfiles: [String: LinkedProfileDetails]
//     var nickname: String?
//     var updatedAt: TimeInterval
//     let privateKey: JsonWebKey?
//     let publicKey: JsonWebKey?
//     let localKey: JsonWebKey?
//     let mnemonic: Mnemonic?
//     let hasLogout: Bool?
//     let uninitialized: Bool?
// }
