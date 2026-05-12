package com.example.profletterai.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.profletterai.ProfletterApp
import com.example.profletterai.ui.components.AppHeader
import com.example.profletterai.ui.screens.generate.GenerateScreen
import com.example.profletterai.ui.screens.profiles.ProfilesScreen
import com.example.profletterai.ui.screens.saved.SavedRecommendationsScreen
import com.example.profletterai.ui.theme.Indigo600
import com.example.profletterai.ui.viewmodel.GenerationViewModel
import com.example.profletterai.ui.viewmodel.ProfilesViewModel
import com.example.profletterai.ui.viewmodel.SavedRecommendationsViewModel

private sealed class Tab(val route: String, val label: String, val icon: ImageVector) {
    data object Generate : Tab("generate", "Generate", Icons.Filled.Edit)
    data object Saved : Tab("saved", "Saved", Icons.Filled.Bookmark)
    data object Profiles : Tab("profiles", "Profiles", Icons.Filled.Person)
}

private val tabs = listOf(Tab.Generate, Tab.Saved, Tab.Profiles)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val locator = remember { ProfletterApp.locator }

    val generationViewModel: GenerationViewModel = viewModel(
        factory = GenerationViewModel.Factory(
            orchestrator = locator.agentOrchestrator,
            planService = locator.planService,
            draftService = locator.draftService,
            recommendationRepository = locator.recommendationRepository
        )
    )
    val savedViewModel: SavedRecommendationsViewModel = viewModel(
        factory = SavedRecommendationsViewModel.Factory(locator.recommendationRepository)
    )
    val profilesViewModel: ProfilesViewModel = viewModel(
        factory = ProfilesViewModel.Factory(locator.professorProfileRepository)
    )

    val savedProfessors by profilesViewModel.profiles.collectAsState()
    val savedLetters by savedViewModel.items.collectAsState()

    fun navigateTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppHeader() },
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEach { tab ->
                    val selected = backStackEntry?.destination?.hierarchy
                        ?.any { it.route == tab.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = { navigateTab(tab.route) },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Indigo600,
                            selectedTextColor = Indigo600,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Tab.Generate.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Tab.Generate.route) {
                GenerateScreen(
                    viewModel = generationViewModel,
                    savedProfessors = savedProfessors,
                    savedLettersCount = savedLetters.size,
                    onNavigateToProfiles = { navigateTab(Tab.Profiles.route) },
                    onNavigateToSaved = { navigateTab(Tab.Saved.route) }
                )
            }
            composable(Tab.Saved.route) {
                SavedRecommendationsScreen(
                    items = savedViewModel.items,
                    selected = savedViewModel.selected,
                    onSelect = savedViewModel::select,
                    onClearSelection = savedViewModel::clearSelection,
                    onDelete = savedViewModel::delete
                )
            }
            composable(Tab.Profiles.route) {
                ProfilesScreen(
                    profiles = profilesViewModel.profiles,
                    isEnriching = profilesViewModel.isEnriching,
                    enrichmentError = profilesViewModel.enrichmentError,
                    onSave = profilesViewModel::upsert,
                    onDelete = profilesViewModel::delete,
                    onDismissError = profilesViewModel::clearError
                )
            }
        }
    }
}
