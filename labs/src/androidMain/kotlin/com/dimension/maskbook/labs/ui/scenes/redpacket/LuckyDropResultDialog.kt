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

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.dimension.maskbook.common.route.navigationComposeDialog
import com.dimension.maskbook.common.route.navigationComposeDialogPackage
import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.ui.widget.MaskDialog
import com.dimension.maskbook.labs.route.LabsRoute

@NavGraphDestination(
    route = LabsRoute.RedPacket.LuckyDropResult.path,
    packageName = navigationComposeDialogPackage,
    functionName = navigationComposeDialog,
)
@Composable
fun LuckyDropResultDialog(
    @Back onBack: () -> Unit,
    @Path("success") success: Boolean,
) {
    MaskDialog(
        onDismissRequest = { onBack.invoke() },
        text = {
            Text("LuckyDropResultDialog $success")
        }
    )
}
