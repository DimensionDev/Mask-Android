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

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ui.widget.HorizontalScenePadding
import com.dimension.maskbook.common.ui.widget.MaskAnimatedVisibility
import com.dimension.maskbook.common.ui.widget.MaskCard
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.TipMessageDialog
import com.dimension.maskbook.common.ui.widget.button.MaskIconButton
import com.dimension.maskbook.common.ui.widget.button.clickable
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.export.model.PersonaData
import com.dimension.maskbook.persona.export.model.SocialData
import com.dimension.maskbook.persona.repository.IPreferenceRepository
import com.dimension.maskbook.persona.ui.scenes.contacts.ContactsScene
import com.dimension.maskbook.persona.ui.scenes.post.PostScene
import com.dimension.maskbook.persona.ui.scenes.social.SocialScene
import com.dimension.maskbook.persona.viewmodel.contacts.ContactsViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import kotlin.math.absoluteValue

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

@Composable
fun PersonaInfoScene(
    socialList: List<SocialData>,
    currentPersona: PersonaData?,
    personaList: List<PersonaData>,
    onCurrentPersonaChanged: (PersonaData) -> Unit,
    onPersonaNameClick: () -> Unit,
    onAddSocialClick: (Network?) -> Unit,
    onSocialItemClick: (SocialData, isEditing: Boolean) -> Unit,
    onAvatarClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        var searchOnly by remember { mutableStateOf(false) }
        var selectedScene by remember { mutableStateOf(PersonaInfoData.Social) }
        var isEditing by rememberSaveable { mutableStateOf(false) }
        val finalSocialList = remember(socialList) {
            listOf(null) + socialList
        }
        LaunchedEffect(currentPersona) {
            isEditing = false
            selectedScene = PersonaInfoData.Social
        }

        val context = LocalContext.current

        val viewModel: ContactsViewModel = getViewModel()
        val contactItems by viewModel.items.collectAsState()
        val searchInput by viewModel.input.collectAsState()

        fun onInvite() {
            context.startActivity(
                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "${context.resources.getText(R.string.scene_share_shareLink)}\nhttps://mask.io/download-links/"
                    )
                    type = "text/plain"
                }
            )
        }

        Column(Modifier.fillMaxSize()) {
            MaskAnimatedVisibility(!searchOnly) {
                PersonaHeader(
                    currentPersona,
                    personaList,
                    onCurrentPersonaChanged,
                    onPersonaNameClick,
                    onAvatarClick,
                )
            }

            if (socialList.isEmpty()) {
                EmptySocialScene(
                    onItemClick = { onAddSocialClick(it) }
                )
            } else {
                MaskAnimatedVisibility(!searchOnly) {
                    TabRow(
                        modifier = Modifier
                            .padding(horizontal = HorizontalScenePadding)
                            .clip(RoundedCornerShape(8.dp)),
                        selectedTabIndex = items.indexOf(selectedScene),
                        backgroundColor = MaterialTheme.colors.background,
                        divider = {
                            TabRowDefaults.Divider(thickness = 0.dp, color = Color.Transparent)
                        },
                        indicator = { tabPositions ->
                            Box(
                                Modifier
                                    .tabIndicatorOffset(tabPositions[items.indexOf(selectedScene)])
                                    .fillMaxSize()
                                    .padding(4.dp)
                                    .zIndex(-1f),
                            ) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            color = MaterialTheme.colors.surface,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                )
                            }
                        },
                        tabs = {
                            items.forEach { data ->
                                Tab(
                                    selected = data == selectedScene,
                                    text = { Text(data.title) },
                                    onClick = {
                                        selectedScene = data
                                    },
                                    selectedContentColor = Color.Transparent,
                                    unselectedContentColor = Color.Transparent,
                                )
                            }
                        }
                    )
                }

                when (selectedScene) {
                    PersonaInfoData.Social -> {
                        SocialScene(
                            socialList = finalSocialList,
                            isEditing = isEditing,
                            setIsEditing = { isEditing = it },
                            onAddSocialClick = { onAddSocialClick(null) },
                            onItemClick = onSocialItemClick,
                        )
                    }
                    PersonaInfoData.Contacts -> {
                        ContactsScene(
                            items = contactItems,
                            input = searchInput,
                            onSearchInputChanged = { viewModel.onInputChanged(it) },
                            onInvite = { onInvite() },
                            searchOnly = searchOnly,
                            onSearchFocusChanged = { searchOnly = it }
                        )
                    }
                    PersonaInfoData.Post -> {
                        PostScene()
                    }
                }
            }
        }

        val preferenceRepository = get<IPreferenceRepository>()
        val shouldShowContactsTipDialog by preferenceRepository.shouldShowContactsTipDialog.collectAsState(initial = false)

        if (shouldShowContactsTipDialog && selectedScene == PersonaInfoData.Contacts) {
            TipMessageDialog(
                modifier = Modifier
                    .padding(horizontal = 22.5f.dp, vertical = 24.dp)
                    .align(Alignment.BottomCenter),
                onClose = {
                    preferenceRepository.setShowContactsTipDialog(false)
                },
                text = {
                    Text(
                        text = stringResource(R.string.scene_persona_contacts_message_tips),
                        color = Color.White,
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun PersonaHeader(
    currentPersona: PersonaData?,
    personaList: List<PersonaData>,
    onCurrentPersonaChanged: (PersonaData) -> Unit,
    onPersonaNameClick: () -> Unit,
    onAvatarClick: () -> Unit,
) {
    val pagerState = rememberPagerState()
    LaunchedEffect(personaList, currentPersona, pagerState.pageCount) {
        if (pagerState.pageCount > 0) {
            pagerState.scrollToPage(
                minOf(
                    pagerState.pageCount - 1,
                    personaList.indexOf(currentPersona).coerceAtLeast(0)
                )
            )
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        if (personaList.isNotEmpty()) {
            onCurrentPersonaChanged.invoke(personaList[pagerState.currentPage])
        }
    }

    HorizontalPager(
        state = pagerState,
        count = personaList.size,
        contentPadding = PaddingValues(35.dp),
    ) { page ->
        MaskCard(
            modifier = Modifier
                .graphicsLayer {
                    val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                    lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }
                }
                .fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.primary,
            elevation = 0.dp,
            contentColor = Color.White,
        ) {
            val item = personaList.getOrNull(page)
            MaskListItem(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                text = {
                    Text(text = item?.name ?: "", style = MaterialTheme.typography.button)
                },
                secondaryText = {
                    Text(
                        text = item?.identifier ?: "",
                        style = MaterialTheme.typography.body2,
                        maxLines = 2,
                        color = Color.White,
                    )
                },
                icon = {
                    if (item == null || item.avatar.isNullOrEmpty()) {
                        Image(
                            painterResource(R.drawable.ic_persona_default_avatar),
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onAvatarClick.invoke()
                                },
                        )
                    } else {
                        Image(
                            rememberImagePainter(item.avatar),
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onAvatarClick.invoke()
                                },
                        )
                    }
                },
                trailing = {
                    MaskIconButton(onClick = onPersonaNameClick) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = null)
                    }
                }
            )
        }
    }
}
