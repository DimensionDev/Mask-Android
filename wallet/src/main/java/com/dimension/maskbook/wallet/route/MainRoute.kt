package com.dimension.maskbook.wallet.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.dimension.maskbook.wallet.ext.encodeUrl
import com.dimension.maskbook.wallet.ext.observeAsState
import com.dimension.maskbook.wallet.repository.*
import com.dimension.maskbook.wallet.ui.RouteType
import com.dimension.maskbook.wallet.ui.scenes.MainHost
import com.dimension.maskbook.wallet.ui.scenes.persona.*
import com.dimension.maskbook.wallet.ui.scenes.persona.social.ConnectSocialModal
import com.dimension.maskbook.wallet.ui.scenes.persona.social.DisconnectSocialDialog
import com.dimension.maskbook.wallet.ui.scenes.persona.social.SelectPlatformModal
import com.dimension.maskbook.wallet.viewmodel.persona.RenamePersonaViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.SwitchPersonaViewModel
import com.dimension.maskbook.wallet.viewmodel.persona.social.DisconnectSocialViewModel
import com.dimension.maskbook.wallet.viewmodel.wallets.UnlockWalletViewModel
import moe.tlaster.kroute.processor.Back
import moe.tlaster.kroute.processor.Path
import moe.tlaster.kroute.processor.Query
import moe.tlaster.kroute.processor.RouteGraphDestination
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf


@RouteGraphDestination(
    route = Root.Main.Home,
    deeplink = ["maskwallet://Home/{tab}"],
    packageName = RouteType.Composable.PackageName,
    functionName = RouteType.Composable.FunctionName,
)
@Composable
fun HomeRoute(
    @Path("tab") tab: String?,
    @Back onBack: () -> Unit,
    navController: NavController,
) {
    MainHost(
        initialTab = tab.orEmpty(),
        onBack = onBack,
        onPersonaCreateClick = {
            navController.navigate(Root.Register.WelcomeCreatePersona)
        },
        onPersonaRecoveryClick = {
            navController.navigate(Root.Register.Recovery.Home)
        },
        onPersonaNameClick = {
            navController.navigate(Root.Main.PersonaMenu)
        },
        onAddSocialClick = { persona, network ->
            val platform = when (network) {
                Network.Twitter -> PlatformType.Twitter
                Network.Facebook -> PlatformType.Facebook
                else -> null // TODO support other network
            }
            if (platform == null) {
                navController.navigate(Root.Main.SelectPlatform(persona.id))
            } else {
                navController.navigate(Root.Main.ConnectSocial(persona.id, platform.name))
            }
        },
        onRemoveSocialClick = { persona, social ->
            val platform = when (social.network) {
                Network.Twitter -> PlatformType.Twitter
                Network.Facebook -> PlatformType.Facebook
                else -> null // TODO support other network
            }
            if (platform != null) {
                navController.navigate(
                    Root.Main.DisconnectSocial(
                        personaId = persona.id,
                        platform = platform.name,
                        socialId = social.id,
                        personaName = persona.name,
                        socialName = social.name,
                    )
                )
            }
        },
        onLabsSettingClick = {
            navController.navigate(Root.Main.PluginSettings)
        },
        onLabsItemClick = { appKey ->
            when (appKey) {
                AppKey.Swap -> {
                    navController.navigate(Root.Main.MarketTrendSettings)
                }
                else -> Unit
            }
        }
    )
}

@RouteGraphDestination(
    route = Root.Main.Logout,
    packageName = RouteType.Dialog.PackageName,
    functionName = RouteType.Dialog.FunctionName,
)
@Composable
fun LogoutRoute(
    @Back onBack: () -> Unit,
    navController: NavController
) {
    val repository = get<IPersonaRepository>()
    LogoutDialog(
        onBack = {
            navController.popBackStack()
        },
        onDone = {
            repository.logout()
            navController.popBackStack(Root.Main.Home, inclusive = false)
        }
    )
}

@RouteGraphDestination(
    route = Root.Main.BackUpPassword.path,
    packageName = RouteType.Modal.PackageName,
    functionName = RouteType.Modal.FunctionName,
)
@Composable
fun BackUpPasswordRoute(
    @Path("target") target: String?,
    navController: NavController,
    @Back onBack: () -> Unit,
) {
    val viewModel = getViewModel<UnlockWalletViewModel>()
    val biometricEnable by viewModel.biometricEnabled.observeAsState(initial = false)
    val password by viewModel.password.observeAsState(initial = "")
    val passwordValid by viewModel.passwordValid.observeAsState(initial = false)
    val context = LocalContext.current
    BackUpPasswordModal(
        biometricEnabled = biometricEnable,
        password = password,
        onPasswordChanged = { viewModel.setPassword(it) },
        passwordValid = passwordValid,
        onConfirm = {
            if (biometricEnable) {
                viewModel.authenticate(
                    context = context,
                    onSuccess = {
                        target?.let {
                            navController.navigate(it, navOptions {
                                popUpTo(Root.Main.BackUpPassword.path) {
                                    inclusive = true
                                }
                            })
                        } ?: onBack.invoke()
                    }
                )
            } else {
                target?.let {
                    navController.navigate(it, navOptions {
                        popUpTo(Root.Main.BackUpPassword.path) {
                            inclusive = true
                        }
                    })
                } ?: onBack.invoke()
            }
        }
    )
}

@RouteGraphDestination(
    route = Root.Main.PersonaMenu,
    packageName = RouteType.Composable.PackageName,
    functionName = RouteType.Composable.FunctionName,
)
@Composable
fun PersonaMenuRoute(
    navController: NavController,
    @Back onBack: () -> Unit
) {
    val persona by get<IPersonaRepository>().currentPersona.observeAsState(initial = null)
    val repository = get<ISettingsRepository>()
    val backupPassword by repository.backupPassword.observeAsState(initial = "")
    val paymentPassword by repository.paymentPassword.observeAsState(initial = "")
    persona?.let {
        PersonaMenuScene(
            personaData = it,
            backupPassword = backupPassword,
            paymentPassword = paymentPassword,
            navController = navController,
            onBack = {
                onBack.invoke()
            }
        )
    }
}

@RouteGraphDestination(
    route = Root.Main.BackUpPassword.path,
    packageName = RouteType.Modal.PackageName,
    functionName = RouteType.Modal.FunctionName,
)
@Composable
fun SwitchPersonaRoute(
    navController: NavController,
) {
    val viewModel = getViewModel<SwitchPersonaViewModel>()
    val current by viewModel.current.observeAsState(initial = null)
    val items by viewModel.items.observeAsState(initial = emptyList())
    SwitchPersonaModal(
        currentPersonaData = current,
        items = items,
        onAdd = {
            navController.navigate(Root.Register.CreatePersona)
        },
        onItemClicked = {
            viewModel.switch(it)
        }
    )
}

@RouteGraphDestination(
    route = Root.Main.RenamePersona.path,
    packageName = RouteType.Composable.PackageName,
    functionName = RouteType.Composable.FunctionName,
)
@Composable
fun RenamePersonaRoute(
    @Path("personaId") personaId: String?,
    @Back onBack: () -> Unit,
) {
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
                onBack.invoke()
            },
        )
    }
}

@RouteGraphDestination(
    route = Root.Main.SelectPlatform.path,
    packageName = RouteType.Modal.PackageName,
    functionName = RouteType.Modal.FunctionName,
)
@Composable
fun SelectPlatformRoute(
    @Path("personaId") personaId: String?,
    navController: NavController,
) {
    SelectPlatformModal(
        onDone = { platform ->
            navController.navigate("ConnectSocial/${personaId.orEmpty().encodeUrl()}/${platform}")
        }
    )
}

@RouteGraphDestination(
    route = Root.Main.SelectPlatform.path,
    packageName = RouteType.Modal.PackageName,
    functionName = RouteType.Modal.FunctionName,
)
@Composable
fun ConnectSocialRoute(
    @Path("personaId") personaId: String?,
    @Path("platform") platform: String?,
    @Back onBack: () -> Unit
) {
    if (personaId != null && platform != null) {
        val repository = get<IPersonaRepository>()
        ConnectSocialModal(
            onDone = {
                repository.beginConnectingProcess(
                    personaId = personaId,
                    platformType = PlatformType.valueOf(platform),
                )
                onBack.invoke() // FIXME: 2022/1/26 onBack
            }
        )
    }
}

@RouteGraphDestination(
    route = Root.Main.Logout,
    packageName = RouteType.Dialog.PackageName,
    functionName = RouteType.Dialog.FunctionName,
)
@Composable
fun DisconnectSocialRoute(
    navController: NavController,
    @Path("personaId") personaId: String?,
    @Path("platform") platform: String?,
    @Path("socialId") socialId: String?,
    @Query("personaName") personaName: String?,
    @Query("socialName") socialName: String?,
) {
    val viewModel = getViewModel<DisconnectSocialViewModel>()
    if (personaId != null && platform != null && socialId != null) {
        DisconnectSocialDialog(
            personaName = personaName.orEmpty(),
            socialName = socialName.orEmpty(),
            onBack = {
                navController.popBackStack()
            },
            onConfirm = {
                when (PlatformType.valueOf(platform)) {
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
