package com.pints793.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pints793.mobile.auth.AuthStatus
import com.pints793.mobile.di.ServiceLocator
import com.pints793.mobile.ui.components.PageLayout
import com.pints793.mobile.ui.navigation.Route
import com.pints793.mobile.ui.screens.dashboard.DashboardScreen
import com.pints793.mobile.ui.screens.login.LoginScreen
import com.pints793.mobile.ui.theme.PintsTheme

@Composable
fun App() {
    PintsTheme {
        val auth = ServiceLocator.authRepository
        val status by auth.status.collectAsStateWithLifecycle()
        LaunchedEffect(Unit) { auth.bootstrap() }

        when (status) {
            AuthStatus.Unknown -> SplashScreen()
            AuthStatus.Authenticated, AuthStatus.Unauthenticated -> NavRoot(startAtLogin = status == AuthStatus.Unauthenticated)
        }
    }
}

@Composable
private fun SplashScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
        Text("793 Pints", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun NavRoot(startAtLogin: Boolean) {
    val nav = rememberNavController()
    val auth = ServiceLocator.authRepository

    LaunchedEffect(Unit) {
        auth.logoutEvents.collect { nav.navigate(Route.Login) { popUpTo(0) } }
    }

    NavHost(
        navController = nav,
        startDestination = if (startAtLogin) Route.Login else Route.Dashboard,
    ) {
        composable<Route.Login> {
            LoginScreen(onLoggedIn = {
                nav.navigate(Route.Dashboard) { popUpTo(0) }
            })
        }
        composable<Route.Dashboard> {
            DashboardScreen(
                onOpenOrganisations = { nav.navigate(Route.Organisations) },
                onOpenInvitations   = { nav.navigate(Route.Invitations) },
                onOpenProfile       = { nav.navigate(Route.Profile) },
                onOpenPinnedCellar  = { p ->
                    nav.navigate(
                        Route.Cellar(
                            orgId = p.organisationId,
                            orgName = p.organisationName,
                            cellarId = p.cellarId,
                            cellarName = p.cellarName,
                        )
                    )
                },
            )
        }

        // ── PLACEHOLDERS — port the corresponding React pages here. ─────────
        composable<Route.Organisations> { Stub("Organisations", nav::popBackStack) }
        composable<Route.Organisation> { entry ->
            val args = entry.toRoute<Route.Organisation>()
            Stub("Organisation: ${args.orgName}", nav::popBackStack)
        }
        composable<Route.Cellar> { entry ->
            val args = entry.toRoute<Route.Cellar>()
            Stub("Cellar: ${args.cellarName}", nav::popBackStack)
        }
        composable<Route.Profile>      { Stub("Profile",      nav::popBackStack) }
        composable<Route.EditProfile>  { Stub("Edit Profile", nav::popBackStack) }
        composable<Route.MemberProfile> { entry ->
            val args = entry.toRoute<Route.MemberProfile>()
            Stub("Member: ${args.userId}", nav::popBackStack)
        }
        composable<Route.Invitations>  { Stub("Invitations",  nav::popBackStack) }
    }
}

@Composable
private fun Stub(title: String, onBack: () -> Unit) {
    PageLayout(title = title, onBack = onBack) {
        Text("This screen is not yet implemented. See README.md → ‘Continuation checklist’.")
        Spacer(Modifier.height(8.dp))
        Text("Add a ViewModel and a Composable in `ui/screens/${title.lowercase().replace(' ', '_')}/` following the LoginScreen / DashboardScreen pattern.")
    }
}

