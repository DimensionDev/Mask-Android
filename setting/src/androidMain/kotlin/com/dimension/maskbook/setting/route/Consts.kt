package com.dimension.maskbook.setting.route

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

const val navigationComposeDialogPackage = "androidx.navigation.compose"
const val navigationComposeDialog = "dialog"

const val navigationComposeBottomSheetPackage = "com.dimension.maskbook.setting.route"
const val navigationComposeBottomSheet = "bottomSheet"

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.bottomSheet(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit
) {
    bottomSheet(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        content = content
    )
}
