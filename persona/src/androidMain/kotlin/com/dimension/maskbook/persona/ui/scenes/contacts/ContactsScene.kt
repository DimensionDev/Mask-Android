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
package com.dimension.maskbook.persona.ui.scenes.contacts

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.dimension.maskbook.common.ui.widget.MaskListItem
import com.dimension.maskbook.common.ui.widget.MaskSearchInput
import com.dimension.maskbook.common.ui.widget.NameImage
import com.dimension.maskbook.common.ui.widget.SinglelineText
import com.dimension.maskbook.common.ui.widget.button.MaskButton
import com.dimension.maskbook.common.ui.widget.button.PrimaryButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.model.ContactData
import com.dimension.maskbook.persona.model.icon

@OptIn(ExperimentalAnimationApi::class)
fun LazyListScope.ContactsScene(
    items: List<ContactData>,
    input: String,
    onSearchInputChanged: (String) -> Unit,
    onSearchFocusChanged: (Boolean) -> Unit,
    onInvite: (ContactData?) -> Unit,
) {
    if (input.isEmpty() && items.isEmpty()) {
        item {
            ContactsEmptyScene(
                icon = {
                    Image(
                        painterResource(id = R.drawable.ic_group_129),
                        contentDescription = null,
                    )
                },
                text = {
                    Text(text = stringResource(R.string.scene_persona_contacts_empty_contacts_tips))
                },
                trailing = {
                    PrimaryButton(onClick = { onInvite(null) }) {
                        Text(text = stringResource(R.string.common_controls_invite))
                    }
                }
            )
        }
    } else {
        item {
            MaskSearchInput(
                modifier = Modifier.onFocusChanged {
                    onSearchFocusChanged.invoke(it.isFocused)
                },
                value = input,
                onValueChanged = { onSearchInputChanged(it) },
                placeholder = {
                    Text(text = stringResource(R.string.scene_persona_contacts_search_account))
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        if (items.isEmpty()) {
            item {
                ContactsEmptyScene(
                    icon = {
                        Image(
                            painterResource(id = R.drawable.ic_contacts_search_empty),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                        )
                    },
                    text = {
                        Text(text = stringResource(R.string.scene_persona_contacts_empty_search_tips))
                    }
                )
            }
        } else {
            ContactsScene(
                items = items,
                onItemClick = { onInvite(it) },
            )
        }
    }
}

@Composable
fun ContactsEmptyScene(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        icon()
        text()
        trailing?.invoke()
    }
}

fun LazyListScope.ContactsScene(
    items: List<ContactData>,
    onItemClick: (ContactData) -> Unit,
) {
    itemsIndexed(items) { index, item ->
        Column {
            MaskButton(onClick = {}) {
                MaskListItem(
                    icon = {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            if (item.avatar.isEmpty()) {
                                NameImage(
                                    name = item.name,
                                    modifier = Modifier.size(38.dp),
                                )
                            } else {
                                Image(
                                    painter = rememberImagePainter(item.avatar),
                                    contentDescription = null,
                                    modifier = Modifier.size(38.dp).clip(shape = CircleShape),
                                )
                            }
                            Image(
                                painter = painterResource(item.network.icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .border(1.dp, MaterialTheme.colors.background, shape = CircleShape)
                                    .clip(shape = CircleShape),
                            )
                        }
                    },
                    text = {
                        Row {
                            SinglelineText(
                                text = item.name,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                            Spacer(Modifier.width(4.dp))
                            if (item.linkedPersona) {
                                Image(
                                    painter = painterResource(R.drawable.mask),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    },
                    secondaryText = {
                        Text(text = '@' + item.id.substringAfter('/'))
                    },
                    trailing = {
                        if (!item.linkedPersona) {
                            PrimaryButton(
                                onClick = { onItemClick(item) },
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 5.5.dp
                                ),
                            ) {
                                Text(
                                    text = stringResource(R.string.common_controls_invite),
                                    style = MaterialTheme.typography.caption,
                                )
                            }
                        }
                    }
                )
            }
            if (index != items.lastIndex) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
