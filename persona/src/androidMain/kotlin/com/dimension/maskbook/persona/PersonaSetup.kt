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
import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.dimension.maskbook.common.ModuleSetup
import com.dimension.maskbook.common.ext.observeAsState
import com.dimension.maskbook.common.route.CommonRoute
import com.dimension.maskbook.common.route.Deeplinks
import com.dimension.maskbook.common.ui.tab.TabScreen
import com.dimension.maskbook.persona.export.PersonaServices
import com.dimension.maskbook.persona.export.model.PlatformType
import com.dimension.maskbook.persona.repository.IContactsRepository
import com.dimension.maskbook.persona.repository.IPersonaRepository
import com.dimension.maskbook.persona.repository.PersonaRepository
import com.dimension.maskbook.persona.repository.personaDataStore
import com.dimension.maskbook.persona.route.PersonaRoute
import com.dimension.maskbook.persona.ui.scenes.ExportPrivateKeyScene
import com.dimension.maskbook.persona.ui.scenes.LogoutDialog
import com.dimension.maskbook.persona.ui.scenes.PersonaMenuScene
import com.dimension.maskbook.persona.ui.scenes.RenamePersonaModal
import com.dimension.maskbook.persona.ui.scenes.SwitchPersonaModal
import com.dimension.maskbook.persona.ui.scenes.social.ConnectSocialModal
import com.dimension.maskbook.persona.ui.scenes.social.DisconnectSocialDialog
import com.dimension.maskbook.persona.ui.scenes.social.SelectPlatformModal
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
import com.dimension.maskbook.setting.export.SettingServices
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

object PersonaSetup : ModuleSetup {

    @OptIn(
        ExperimentalMaterialNavigationApi::class,
        ExperimentalAnimationApi::class
    )
    override fun NavGraphBuilder.route(navController: NavController, onBack: () -> Unit) {
        dialog(PersonaRoute.Logout) {
            val repository = get<IPersonaRepository>()
            LogoutDialog(
                onBack = {
                    navController.popBackStack()
                },
                onDone = {
                    repository.logout()
                    navController.popBackStack(CommonRoute.Main.Home, inclusive = false)
                }
            )
        }
        composable(PersonaRoute.PersonaMenu) {
            val persona by get<IPersonaRepository>().currentPersona.observeAsState(initial = null)
            val repository = get<SettingServices>()
            val backupPassword by repository.backupPassword.observeAsState(initial = "")
            val paymentPassword by repository.paymentPassword.observeAsState(initial = "")
            persona?.let {
                PersonaMenuScene(
                    personaData = it,
                    backupPassword = backupPassword,
                    paymentPassword = paymentPassword,
                    navController = navController,
                    onBack = {
                        navController.navigateUp()
                    }
                )
            }
        }
        bottomSheet(PersonaRoute.SwitchPersona) {
            val viewModel = getViewModel<SwitchPersonaViewModel>()
            val current by viewModel.current.observeAsState(initial = null)
            val items by viewModel.items.observeAsState(initial = emptyList())
            SwitchPersonaModal(
                currentPersonaData = current,
                items = items,
                onAdd = {
                    navController.navigate(Uri.parse(Deeplinks.Wallet.Register.CreatePersona))
                },
                onItemClicked = {
                    viewModel.switch(it)
                }
            )
        }
        bottomSheet(
            PersonaRoute.RenamePersona.path,
            arguments = listOf(
                navArgument("personaId") { type = NavType.StringType },
            )
        ) {
            val personaId = it.arguments?.getString("personaId")
            if (personaId != null) {
                val viewModel = getViewModel<RenamePersonaViewModel> {
                    parametersOf(personaId)
                }
                val name by viewModel.name.observeAsState(initial = "")
                RenamePersonaModal(
                    name = name,
                    onNameChanged = { value ->
                        viewModel.setName(value)
                    },
                    onDone = {
                        viewModel.confirm()
                        navController.popBackStack()
                    },
                )
            }
        }
        composable(PersonaRoute.ExportPrivateKey) {
            ExportPrivateKeyScene(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        bottomSheet(
            route = PersonaRoute.SelectPlatform.path,
            arguments = listOf(
                navArgument("personaId") { type = NavType.StringType },
            ),
        ) {
            val personaId = it.arguments?.getString("personaId").orEmpty()
            SelectPlatformModal(
                onDone = { platform ->
                    navController.navigate(PersonaRoute.ConnectSocial(personaId, platform.name))
                }
            )
        }
        bottomSheet(
            route = PersonaRoute.ConnectSocial.path,
            arguments = listOf(
                navArgument("personaId") { type = NavType.StringType },
                navArgument("platform") { type = NavType.StringType },
            ),
        ) {
            val personaId = it.arguments?.getString("personaId")
            val platform = it.arguments?.getString("platform")
            if (personaId != null && platform != null) {
                val repository = get<IPersonaRepository>()
                ConnectSocialModal(
                    onDone = {
                        repository.beginConnectingProcess(
                            personaId = personaId,
                            platformType = PlatformType.valueOf(platform),
                        )
                        onBack.invoke()
                    }
                )
            }
        }
        dialog(
            PersonaRoute.DisconnectSocial.path,
            arguments = listOf(
                navArgument("personaId") { type = NavType.StringType },
                navArgument("platform") { type = NavType.StringType },
                navArgument("socialId") { type = NavType.StringType },
            )
        ) {
            val personaId = it.arguments?.getString("personaId")
            val personaName = it.arguments?.getString("personaName").orEmpty()
            val platform = it.arguments?.getString("platform")?.let { PlatformType.valueOf(it) }
            val socialId = it.arguments?.getString("socialId")
            val socialName = it.arguments?.getString("socialName").orEmpty()
            val viewModel = getViewModel<DisconnectSocialViewModel>()
            if (personaId != null && platform != null && socialId != null) {
                DisconnectSocialDialog(
                    personaName = personaName,
                    socialName = socialName,
                    onBack = {
                        navController.popBackStack()
                    },
                    onConfirm = {
                        when (platform) {
                            PlatformType.Twitter ->
                                viewModel.disconnectTwitter(
                                    personaId = personaId,
                                    socialId = socialId
                                )
                            PlatformType.Facebook ->
                                viewModel.disconnectFacebook(
                                    personaId = personaId,
                                    socialId = socialId
                                )
                        }
                        navController.popBackStack()
                    },
                )
            }
        }
    }

    override fun dependencyInject() = module {
        single {
            PersonaRepository(get<Context>().personaDataStore, get(), get())
        } binds arrayOf(
            IPersonaRepository::class,
            IContactsRepository::class
        )

        single<PersonaServices> { PersonaServicesImpl(get()) }
        single { PersonasTabScreen() } bind TabScreen::class

        viewModel { PersonaViewModel(get()) }
        viewModel { TwitterSocialViewModel(get()) }
        viewModel { FacebookSocialViewModel(get()) }
        viewModel { TwitterConnectSocialViewModel(get()) }
        viewModel { FaceBookConnectSocialViewModel(get()) }
        viewModel { DisconnectSocialViewModel(get()) }
        viewModel { SwitchPersonaViewModel(get()) }
        viewModel { (personaId: String) -> RenamePersonaViewModel(get(), personaId) }
        viewModel { ExportPrivateKeyViewModel(get()) }
        viewModel { PostViewModel(get(), get()) }
        viewModel { ContactsViewModel(get(), get()) }
    }

    override fun onExtensionReady() {
        KoinPlatformTools.defaultContext().get().get<IPersonaRepository>().init()
    }
}
