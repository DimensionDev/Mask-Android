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
package com.dimension.maskbook.persona.ext

import com.dimension.maskbook.setting.export.model.BooleanWrapper
import com.dimension.maskbook.setting.export.model.JsonWebKey

fun com.dimension.maskwalletcore.JsonWebKey.toJWK() =
    JsonWebKey(
        kty = this.kty,
        kid = this.kid,
        use = this.use,
        key_ops = this.key_ops,
        alg = this.alg,
        ext = this.ext?.let { BooleanWrapper(it) },
        crv = this.crv,
        x = this.x,
        y = this.y,
        d = this.d,
        n = this.n,
        e = this.e,
        p = this.p,
        q = this.q,
        dp = this.dp,
        dq = this.dq,
        qi = this.qi,
        oth = this.oth?.let {
            it.map {
                JsonWebKey.RsaOtherPrimesInfo(
                    r = it.r,
                    d = it.d,
                    t = it.t
                )
            }
        },
        k = this.k,
    )
