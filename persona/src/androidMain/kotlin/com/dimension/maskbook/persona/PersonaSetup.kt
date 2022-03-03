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
package com.dimension.maskbook.persona

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.ui.tab.TabScreen
import com.dimension.maskbook.persona.data.JSMethod
import com.dimension.maskbook.persona.export.PersonaServices
import com.dimension.maskbook.persona.export.model.ConnectAccountData
import com.dimension.maskbook.persona.repository.IContactsRepository
import com.dimension.maskbook.persona.repository.IPersonaRepository
import com.dimension.maskbook.persona.repository.ISocialsRepository
import com.dimension.maskbook.persona.repository.PersonaRepository
import com.dimension.maskbook.persona.repository.personaDataStore
import com.dimension.maskbook.persona.ui.scenes.generatedRoute
import com.dimension.maskbook.persona.ui.tab.PersonasTabScreen
import com.dimension.maskbook.persona.viewmodel.ExportPrivateKeyViewModel
import com.dimension.maskbook.persona.viewmodel.PersonaViewModel
import com.dimension.maskbook.persona.viewmodel.RenamePersonaViewModel
import com.dimension.maskbook.persona.viewmodel.SwitchPersonaViewModel
import com.dimension.maskbook.persona.viewmodel.contacts.ContactsViewModel
import com.dimension.maskbook.persona.viewmodel.post.PostViewModel
import com.dimension.maskbook.persona.viewmodel.social.DisconnectSocialViewModel
import com.dimension.maskbook.persona.viewmodel.social.FaceBookConnectSocialViewModel
import com.dimension.maskbook.persona.viewmodel.social.FacebookSocialViewModel
import com.dimension.maskbook.persona.viewmodel.social.TwitterConnectSocialViewModel
import com.dimension.maskbook.persona.viewmodel.social.TwitterSocialViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

object PersonaSetup : ModuleSetup {

    override fun NavGraphBuilder.route(navController: NavController) {
        generatedRoute(navController)
    }

    override fun dependencyInject() = module {
        single {
            PersonaRepository(get<Context>().personaDataStore, get(), get())
        } binds arrayOf(
            IPersonaRepository::class,
            ISocialsRepository::class,
            IContactsRepository::class,
        )

        single { JSMethod(get()) }

        single<PersonaServices> { PersonaServicesImpl(get()) }
        single { PersonasTabScreen() } bind TabScreen::class

        viewModel { PersonaViewModel(get(), get()) }
        viewModel { TwitterSocialViewModel(get()) }
        viewModel { FacebookSocialViewModel(get()) }
        viewModel { TwitterConnectSocialViewModel(get()) }
        viewModel { FaceBookConnectSocialViewModel(get()) }
        viewModel { DisconnectSocialViewModel(get()) }
        viewModel { SwitchPersonaViewModel(get()) }
        viewModel { (personaId: String) -> RenamePersonaViewModel(get(), personaId) }
        viewModel { ExportPrivateKeyViewModel(get()) }
        viewModel { PostViewModel(get(), get()) }
        viewModel { ContactsViewModel(get()) }
        viewModel { ContactsViewModel(get(), get()) }

        viewModel { (data: ConnectAccountData) ->
            com.dimension.maskbook.persona.viewmodel.social.UserNameModalViewModel(
                get(),
                data
            )
        }
    }

    override fun onExtensionReady() {
        KoinPlatformTools.defaultContext().get().get<IPersonaRepository>().init()
    }
}
