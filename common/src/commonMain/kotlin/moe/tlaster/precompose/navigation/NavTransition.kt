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
package moe.tlaster.precompose.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

/**
 * Create a navigation transition
 */
@OptIn(ExperimentalAnimationApi::class)
data class NavTransition(
    /**
     * Transition the scene that about to appear for the first time, similar to activity onCreate, factor from 0.0 to 1.0
     */
    val enterTransition: (AnimatedContentScope<*>.() -> EnterTransition) = { fadeIn(animationSpec = tween(700)) },
    /**
     * Transition the scene that about to disappear forever, similar to activity onDestroy, factor from 1.0 to 0.0
     */
    val exitTransition: (AnimatedContentScope<*>.() -> ExitTransition) = { fadeOut(animationSpec = tween(700)) },
    /**
     * Transition the scene that will be pushed into back stack, similar to activity onPause, factor from 1.0 to 0.0
     */
    val popEnterTransition: (AnimatedContentScope<*>.() -> EnterTransition) = enterTransition,
    /**
     * Transition the scene that about to show from the back stack, similar to activity onResume, factor from 0.0 to 1.0
     */
    val popExitTransition: (AnimatedContentScope<*>.() -> ExitTransition) = exitTransition,
) {
    companion object {
        val NoneEnter: (AnimatedContentScope<*>.() -> EnterTransition) = { EnterTransition.None }
        val NoneExit: (AnimatedContentScope<*>.() -> ExitTransition) = { ExitTransition.None }
    }
}
