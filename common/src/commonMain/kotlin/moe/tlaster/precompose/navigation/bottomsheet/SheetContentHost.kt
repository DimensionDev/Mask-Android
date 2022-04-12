package moe.tlaster.precompose.navigation.bottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.ui.LocalLifecycleOwner
import moe.tlaster.precompose.ui.LocalViewModelStoreOwner

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun SheetContentHost(
    backStackEntry: BackStackEntry?,
    sheetState: ModalBottomSheetState,
    stateHolder: SaveableStateHolder,
    onSheetShown: (entry: BackStackEntry) -> Unit,
    onSheetDismissed: (entry: BackStackEntry) -> Unit,
) {
    val scope = rememberCoroutineScope()
    if (backStackEntry != null) {
        val currentOnSheetShown by rememberUpdatedState(onSheetShown)
        val currentOnSheetDismissed by rememberUpdatedState(onSheetDismissed)
        var hideCalled by remember(backStackEntry) { mutableStateOf(false) }
        LaunchedEffect(backStackEntry, hideCalled) {
            val sheetVisibility = snapshotFlow { sheetState.isVisible }
            sheetVisibility
                // We are only interested in changes in the sheet's visibility
                .distinctUntilChanged()
                // distinctUntilChanged emits the initial value which we don't need
                .drop(1)
                // We want to know when the sheet was visible but is not anymore
                .filter { isVisible -> !isVisible }
                // Finally, pop the back stack when the sheet has been hidden
                .collect { if (!hideCalled) currentOnSheetDismissed(backStackEntry) }
        }

        // We use this signal to know when its (almost) safe to `show` the bottom sheet
        // It will be set after the sheet's content has been `onGloballyPositioned`
        val contentPositionedSignal = remember(backStackEntry) {
            CompletableDeferred<Unit?>()
        }

        // Whenever the composable associated with the backStackEntry enters the composition, we
        // want to show the sheet, and hide it when this composable leaves the composition
        DisposableEffect(backStackEntry) {
            scope.launch {
                contentPositionedSignal.await()
                try {
                    // If we don't wait for a few frames before calling `show`, we will be too early
                    // and the sheet content won't have been laid out yet (even with our content
                    // positioned signal). If a sheet is tall enough to have a HALF_EXPANDED state,
                    // we might be here before the SwipeableState's anchors have been properly
                    // calculated, resulting in the sheet animating to the EXPANDED state when
                    // calling `show`. As a workaround, we wait for a magic number of frames.
                    // https://issuetracker.google.com/issues/200980998
                    repeat(AWAIT_FRAMES_BEFORE_SHOW) { awaitFrame() }
                    sheetState.show()
                } catch (sheetShowCancelled: CancellationException) {
                    // There is a race condition in ModalBottomSheetLayout that happens when the
                    // sheet content changes due to the anchors being re-calculated. This causes an
                    // animation to run for a short time, cancelling any currently running animation
                    // such as the one triggered by our `show` call.
                    // The sheet will still snap to the EXPANDED or HALF_EXPANDED state.
                    // In that case we want to wait until the sheet is visible. For safety, we only
                    // wait for 800 milliseconds - if the sheet is not visible until then, something
                    // has gone horribly wrong.
                    // https://issuetracker.google.com/issues/200980998
                    withTimeout(800) {
                        while (!sheetState.isVisible) {
                            awaitFrame()
                        }
                    }
                } finally {
                    // If, for some reason, the sheet is in a state where the animation is still
                    // running, there is a chance that it is already targeting the EXPANDED or
                    // HALF_EXPANDED state and will snap to that. In that case we can be fairly
                    // certain that the sheet will actually end up in that state.
                    if (sheetState.isVisible || sheetState.willBeVisible) {
                        currentOnSheetShown(backStackEntry)
                    }
                }
            }
            onDispose {
                scope.launch {
                    hideCalled = true
                    try {
                        sheetState.internalHide()
                    } finally {
                        hideCalled = false
                    }
                }
            }
        }

        stateHolder.SaveableStateProvider(backStackEntry.id) {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides backStackEntry,
                LocalLifecycleOwner provides backStackEntry,
            ) {
                Box(Modifier.onGloballyPositioned { contentPositionedSignal.complete(Unit) }) {
                    backStackEntry.route.content(backStackEntry)
                }
            }
        }

    } else {
        EmptySheet()
    }
}

@Composable
private fun EmptySheet() {
    // The swipeable modifier has a bug where it doesn't support having something with
    // height = 0
    // b/178529942
    // If there are no destinations on the back stack, we need to add something to work
    // around this
    Box(Modifier.height(1.dp))
}

private suspend fun awaitFrame() = withFrameNanos(onFrame = {})

/**
 * This magic number has been chosen through painful experiments.
 * - Waiting for 1 frame still results in the sheet fully expanding, which we don't want
 * - Waiting for 2 frames results in the `show` call getting cancelled
 * - Waiting for 3+ frames results in the sheet expanding to the correct state. Success!
 * We wait for a few frames more just to be sure.
 */
private const val AWAIT_FRAMES_BEFORE_SHOW = 3

// We have the same issue when we are hiding the sheet, but snapTo works better
@OptIn(ExperimentalMaterialApi::class)
private suspend fun ModalBottomSheetState.internalHide() {
    snapTo(ModalBottomSheetValue.Hidden)
}

@OptIn(ExperimentalMaterialApi::class)
private val ModalBottomSheetState.willBeVisible: Boolean
    get() = targetValue == ModalBottomSheetValue.HalfExpanded || targetValue == ModalBottomSheetValue.Expanded
