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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dimension.maskbook.common.ui.widget.MaskScaffold
import com.dimension.maskbook.common.ui.widget.MaskSingleLineTopAppBar
import com.dimension.maskbook.common.ui.widget.button.MaskIconCardButton
import com.dimension.maskbook.persona.R
import com.dimension.maskbook.persona.export.model.Network
import com.dimension.maskbook.persona.export.model.PersonaData
import com.dimension.maskbook.persona.export.model.SocialData
import com.dimension.maskbook.persona.viewmodel.PersonaViewModel
import org.koin.androidx.compose.getViewModel

@ExperimentalAnimationApi
@Composable
fun PersonaScene(
    onBack: () -> Unit,
    onPersonaCreateClick: () -> Unit,
    onPersonaRecoveryClick: () -> Unit,
    onPersonaNameClick: () -> Unit,
    onAddSocialClick: (PersonaData, Network?) -> Unit,
    onRemoveSocialClick: (PersonaData, SocialData) -> Unit,
    onSocialItemClick: (PersonaData, SocialData) -> Unit,
    onAddPersonaAvatar: () -> Unit,
    onPersonaAvatarClick: () -> Unit,
    onSynchronize: () -> Unit,
) {
    val viewModel: PersonaViewModel = getViewModel()
    val currentPersona by viewModel.currentPersona.collectAsState()
    val socialList by viewModel.socialList.collectAsState()
    val personaList by viewModel.personaList.collectAsState()

    if (currentPersona == null) return
    val persona = currentPersona?.persona
    if (persona == null) {
        EmptyPersonaScene(
            onPersonaCreateClick = onPersonaCreateClick,
            onPersonaRecoveryClick = onPersonaRecoveryClick,
            onPersonaSynchronizationClick = onSynchronize,
        )
        return
    }

    MaskScaffold(
        topBar = {
            MaskSingleLineTopAppBar(
                navigationIcon = {
                    MaskIconCardButton(onClick = onSynchronize) {
                        Icon(
                            painter = painterResource(id = R.drawable.scan),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                },
                actions = {
                    MaskIconCardButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                        )
                    }
                },
                title = {
                    Text(text = stringResource(R.string.scene_persona_welcome_persona))
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            PersonaInfoScene(
                socialList = socialList ?: emptyList(),
                currentPersona = persona,
                personaList = personaList ?: emptyList(),
                onAddSocialClick = { network ->
                    onAddSocialClick(persona, network)
                },
                onSocialItemClick = { data, isEditing ->
                    if (isEditing) {
                        onRemoveSocialClick(persona, data)
                    } else {
                        onSocialItemClick(persona, data)
                    }
                },
                onPersonaNameClick = onPersonaNameClick,
                onCurrentPersonaChanged = {
                    viewModel.setCurrentPersona(it)
                },
                onAvatarClick = {
                    if (persona.avatar.isNullOrEmpty()) {
                        onAddPersonaAvatar()
                    } else {
                        onPersonaAvatarClick()
                    }
                }
            )
        }
    }
}
