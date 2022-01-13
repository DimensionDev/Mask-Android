package com.dimension.maskbook.wallet.ui.scenes.persona

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.wallet.ui.scenes.persona.contacts.ContactsScene
import com.dimension.maskbook.wallet.ui.scenes.persona.post.PostScene
import com.dimension.maskbook.wallet.ui.scenes.persona.social.SocialScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

private enum class PersonaInfoData(val title: String) {
    Social(
        title = "Social",
    ),
    Contacts(
        title = "Contacts",
    ),
    Post(
        title = "Post",
    )
}

private val items = listOf(
    PersonaInfoData.Social,
    PersonaInfoData.Contacts,
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PersonaInfoScene() {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    Column {
        TabRow(
            modifier = Modifier.shadow(16.dp, clip = false),
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = MaterialTheme.colors.surface,
            divider = {
                TabRowDefaults.Divider(thickness = 0.dp)
            },
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        .height(15.dp), // 12(bottomPadding) + 3(height)
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth(0.1f)
                            .height(3.dp)
                            .background(
                                color = MaterialTheme.colors.primary,
                                shape = CircleShape,
                            )
                    )
                }
            },
            tabs = {
                items.forEachIndexed { index, data ->
                    Tab(
                        selected = index == pagerState.currentPage,
                        text = { Text(data.title) },
                        onClick = {
                            scope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                        selectedContentColor = MaterialTheme.colors.primary,
                        unselectedContentColor = MaterialTheme.colors.onBackground.copy(
                            alpha = ContentAlpha.medium
                        ),
                        modifier = Modifier.height(56.dp),
                    )
                }
            }
        )
        HorizontalPager(
            count = items.size,
            state = pagerState,
        ) { page ->
            when (items[page]) {
                PersonaInfoData.Social -> SocialScreen()
                PersonaInfoData.Contacts -> ContactsScene()
                PersonaInfoData.Post -> PostScene()
            }
        }
    }
}
