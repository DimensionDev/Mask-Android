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
package com.dimension.maskbook.persona.ui.scenes

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
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.export.model.SocialData
import com.dimension.maskbook.wallet.ui.scenes.persona.EmptySocialScene
import com.dimension.maskbook.wallet.ui.scenes.persona.contacts.ContactsScene
import com.dimension.maskbook.wallet.ui.scenes.persona.post.PostScene
import com.dimension.maskbook.wallet.ui.scenes.persona.social.SocialScene
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
fun PersonaInfoScene(
    socialList: List<SocialData>,
    onAddSocialClick: (Network?) -> Unit,
    onSocialItemClick: (SocialData, isEditing: Boolean) -> Unit,
) {
    if (socialList.isEmpty()) {
        EmptySocialScene(
            onItemClick = { onAddSocialClick(it) }
        )
        return
    }

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
                PersonaInfoData.Social -> SocialScene(
                    socialList = socialList,
                    onAddSocialClick = { onAddSocialClick(null) },
                    onItemClick = onSocialItemClick,
                )
                PersonaInfoData.Contacts -> ContactsScene()
                PersonaInfoData.Post -> PostScene()
            }
        }
    }
}
