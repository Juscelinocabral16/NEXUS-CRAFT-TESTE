package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.repository.TutorialRepository
import com.example.ui.components.BlockCalculatorDialog
import com.example.ui.components.PhotoZoomDialog
import com.example.ui.screens.BuilderFocusScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.TutorialDetailScreen
import com.example.ui.theme.MineBuildsTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val database = AppDatabase.getInstance(applicationContext)
        val repository = TutorialRepository(database)
        MainViewModel.provideFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MineBuildsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MineBuildsApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MineBuildsApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val tutorials by viewModel.tutorialsList.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
    val onlyFavorites by viewModel.onlyFavorites.collectAsStateWithLifecycle()
    val tutorialDetail by viewModel.currentTutorialDetail.collectAsStateWithLifecycle()
    val activeStepIndex by viewModel.activeStepIndex.collectAsStateWithLifecycle()
    val zoomImageRes by viewModel.zoomImageRes.collectAsStateWithLifecycle()
    val calcInput by viewModel.calculatorInput.collectAsStateWithLifecycle()
    val calcResult by viewModel.calculatorResult.collectAsStateWithLifecycle()

    // Handle Android system back button
    BackHandler(enabled = true) {
        if (!viewModel.onBackNavigation()) {
            // When on home with no active search or filter, standard back
        }
    }

    when (currentScreen) {
        AppScreen.HOME, AppScreen.CALCULATOR -> {
            HomeScreen(
                tutorials = tutorials,
                searchQuery = searchQuery,
                onSearchChange = viewModel::onSearchQueryChanged,
                selectedCategory = selectedCategory,
                onSelectCategory = viewModel::onSelectCategory,
                selectedDifficulty = selectedDifficulty,
                onSelectDifficulty = viewModel::onSelectDifficulty,
                onlyFavorites = onlyFavorites,
                onToggleFavorites = viewModel::onToggleFavoritesOnly,
                onSelectTutorial = viewModel::onOpenTutorial,
                onToggleFavorite = viewModel::onToggleFavorite,
                onOpenCalculator = viewModel::onOpenCalculator
            )
        }

        AppScreen.DETAIL -> {
            TutorialDetailScreen(
                state = tutorialDetail,
                currentStepIndex = activeStepIndex,
                onStepIndexChange = viewModel::onSetStepIndex,
                onBack = { viewModel.onBackNavigation() },
                onToggleFavorite = viewModel::onToggleFavorite,
                onToggleStepCompleted = viewModel::onToggleStepCompleted,
                onToggleMaterialChecked = viewModel::onToggleMaterialChecked,
                onOpenFocusBuilder = viewModel::onOpenFocusBuilder,
                onOpenZoom = viewModel::onOpenZoom,
                onSaveNotes = viewModel::onSaveUserNotes,
                onResetProgress = viewModel::onResetProgress
            )
        }

        AppScreen.FOCUS_BUILDER -> {
            BuilderFocusScreen(
                state = tutorialDetail,
                currentStepIndex = activeStepIndex,
                onStepIndexChange = viewModel::onSetStepIndex,
                onClose = { viewModel.onBackNavigation() },
                onToggleStepCompleted = viewModel::onToggleStepCompleted,
                onOpenZoom = viewModel::onOpenZoom
            )
        }
    }

    // Modal Calculator Dialog
    if (currentScreen == AppScreen.CALCULATOR) {
        BlockCalculatorDialog(
            input = calcInput,
            result = calcResult,
            onInputChange = viewModel::onCalculatorInputChanged,
            onDismiss = { viewModel.onBackNavigation() }
        )
    }

    // Modal Photo Zoom Dialog
    if (zoomImageRes != null) {
        PhotoZoomDialog(
            imageRes = zoomImageRes,
            onDismiss = viewModel::onCloseZoom
        )
    }
}
