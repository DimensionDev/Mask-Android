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
package com.dimension.maskbook.labs.ui.scenes.redpacket

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.route.navigationComposeDialog
import com.dimension.maskbook.common.route.navigationComposeDialogPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.routeProcessor.annotations.Query
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.common.ui.widget.button.MaskIconButton
import com.dimension.maskbook.extension.export.ExtensionServices
import com.dimension.maskbook.labs.R
import com.dimension.maskbook.labs.route.LabsRoute
import com.dimension.maskbook.labs.ui.widget.RedPacketShareButton
import moe.tlaster.koin.compose.get

@NavGraphDestination(
    route = LabsRoute.RedPacket.LuckyDropResult.path,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun LuckyDropResultDialog(
    @Back onBack: () -> Unit,
    @Path("success") success: Boolean,
    @Query("amount") amount: String?,
    @Query("tweetUrl") tweetUrl: String?,
) {
    val extensionServices = get<ExtensionServices>()
    MaskDialog(
        onDismissRequest = onBack,
        backgroundColor = Color.Transparent,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(
                        if (success) {
                            R.drawable.ic_receive_redpacket_success
                        } else {
                            R.drawable.ic_receive_redpacket_failed
                        }
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth(),
                )
                Column(
                    modifier = Modifier
                        .padding(vertical = 25.dp, horizontal = 80.dp)
                        .align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = if (success) {
                            "%s %s".format(
                                amount.orEmpty(),
                                stringResource(R.string.scene_open_red_package_claimed),
                            )
                        } else {
                            stringResource(R.string.scene_open_red_package_better_luck_next_time)
                        },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h2,
                        color = Color.White,
                    )
                    Spacer(Modifier.height(8.5.dp))
                    if (success) {
                        RedPacketShareButton(
                            onClick = {
                                onBack.invoke()
                                tweetUrl?.let {
                                    extensionServices.loadUrl(it)
                                }
                            },
                            contentPadding = PaddingValues(vertical = 10.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(R.string.scene_wallet_receive_btn_share),
                                style = MaterialTheme.typography.h3,
                                color = Color(0xFF111418),
                            )
                        }
                    } else {
                        Spacer(Modifier.height(height = 45.dp))
                    }
                }
            }
            Spacer(Modifier.height(22.dp))
            MaskIconButton(onClick = onBack) {
                Image(
                    painter = painterResource(R.drawable.ic_close_circle),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
    }
}
