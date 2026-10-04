package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.BuildDifficulty
import com.example.data.model.TutorialCategory
import com.example.data.repository.TutorialDetailUiState
import com.example.data.repository.TutorialItemUiState
import com.example.data.repository.TutorialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    DETAIL,
    FOCUS_BUILDER,
    CALCULATOR
}

data class CalculatorResult(
    val totalBlocks: Int = 0,
    val stacksOf64: Int = 0,
    val remainder: Int = 0,
    val shulkerBoxes: Int = 0,
    val doubleChests: Int = 0
)

class MainViewModel(
    private val repository: TutorialRepository
) : ViewModel() {

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<TutorialCategory?>(TutorialCategory.ALL)
    val selectedCategory: StateFlow<TutorialCategory?> = _selectedCategory.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow<BuildDifficulty?>(null)
    val selectedDifficulty: StateFlow<BuildDifficulty?> = _selectedDifficulty.asStateFlow()

    private val _onlyFavorites = MutableStateFlow(false)
    val onlyFavorites: StateFlow<Boolean> = _onlyFavorites.asStateFlow()

    private val _selectedTutorialId = MutableStateFlow<String?>(null)
    val selectedTutorialId: StateFlow<String?> = _selectedTutorialId.asStateFlow()

    private val _activeStepIndex = MutableStateFlow(0)
    val activeStepIndex: StateFlow<Int> = _activeStepIndex.asStateFlow()

    private val _zoomImageRes = MutableStateFlow<Int?>(null)
    val zoomImageRes: StateFlow<Int?> = _zoomImageRes.asStateFlow()

    private val _calculatorInput = MutableStateFlow("128")
    val calculatorInput: StateFlow<String> = _calculatorInput.asStateFlow()

    private val _calculatorResult = MutableStateFlow(calculate(128))
    val calculatorResult: StateFlow<CalculatorResult> = _calculatorResult.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tutorialsList: StateFlow<List<TutorialItemUiState>> = combine(
        _searchQuery,
        _selectedCategory,
        _selectedDifficulty,
        _onlyFavorites
    ) { query, category, difficulty, favsOnly ->
        repository.getAllTutorialsStream(query, category, difficulty, favsOnly)
    }.flatMapLatest { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentTutorialDetail: StateFlow<TutorialDetailUiState?> = _selectedTutorialId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getTutorialDetailStream(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSelectCategory(category: TutorialCategory?) {
        _selectedCategory.value = category
    }

    fun onSelectDifficulty(difficulty: BuildDifficulty?) {
        _selectedDifficulty.value = if (_selectedDifficulty.value == difficulty) null else difficulty
    }

    fun onToggleFavoritesOnly() {
        _onlyFavorites.value = !_onlyFavorites.value
    }

    fun onOpenTutorial(tutorialId: String) {
        _selectedTutorialId.value = tutorialId
        _activeStepIndex.value = 0
        _currentScreen.value = AppScreen.DETAIL
    }

    fun onBackNavigation(): Boolean {
        return when (_currentScreen.value) {
            AppScreen.FOCUS_BUILDER -> {
                _currentScreen.value = AppScreen.DETAIL
                true
            }
            AppScreen.DETAIL, AppScreen.CALCULATOR -> {
                _currentScreen.value = AppScreen.HOME
                _selectedTutorialId.value = null
                true
            }
            AppScreen.HOME -> {
                if (_searchQuery.value.isNotEmpty() || _selectedCategory.value != TutorialCategory.ALL || _selectedDifficulty.value != null || _onlyFavorites.value) {
                    _searchQuery.value = ""
                    _selectedCategory.value = TutorialCategory.ALL
                    _selectedDifficulty.value = null
                    _onlyFavorites.value = false
                    true
                } else {
                    false
                }
            }
        }
    }

    fun onOpenFocusBuilder() {
        _currentScreen.value = AppScreen.FOCUS_BUILDER
    }

    fun onOpenCalculator() {
        _currentScreen.value = AppScreen.CALCULATOR
    }

    fun onToggleFavorite(tutorialId: String, currentFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(tutorialId, currentFav)
        }
    }

    fun onToggleStepCompleted(tutorialId: String, stepNumber: Int) {
        viewModelScope.launch {
            repository.toggleStepCompleted(tutorialId, stepNumber)
        }
    }

    fun onToggleMaterialChecked(tutorialId: String, materialId: String, currentChecked: Boolean) {
        viewModelScope.launch {
            repository.toggleMaterialChecked(tutorialId, materialId, currentChecked)
        }
    }

    fun onSetStepIndex(index: Int) {
        _activeStepIndex.value = index
        val id = _selectedTutorialId.value
        if (id != null) {
            viewModelScope.launch {
                repository.setStepIndex(id, index)
            }
        }
    }

    fun onNextStep(totalSteps: Int) {
        if (_activeStepIndex.value < totalSteps - 1) {
            onSetStepIndex(_activeStepIndex.value + 1)
        }
    }

    fun onPreviousStep() {
        if (_activeStepIndex.value > 0) {
            onSetStepIndex(_activeStepIndex.value - 1)
        }
    }

    fun onResetProgress(tutorialId: String) {
        viewModelScope.launch {
            repository.resetProgress(tutorialId)
            _activeStepIndex.value = 0
        }
    }

    fun onSaveUserNotes(tutorialId: String, notes: String) {
        viewModelScope.launch {
            repository.saveNotes(tutorialId, notes)
        }
    }

    fun onCalculatorInputChanged(input: String) {
        val clean = input.filter { it.isDigit() }.take(6)
        _calculatorInput.value = clean
        val count = clean.toIntOrNull() ?: 0
        _calculatorResult.value = calculate(count)
    }

    fun onOpenZoom(imageRes: Int) {
        _zoomImageRes.value = imageRes
    }

    fun onCloseZoom() {
        _zoomImageRes.value = null
    }

    private fun calculate(total: Int): CalculatorResult {
        val stacks = total / 64
        val remainder = total % 64
        val shulker = total / (27 * 64)
        val chests = total / (54 * 64)
        return CalculatorResult(
            totalBlocks = total,
            stacksOf64 = stacks,
            remainder = remainder,
            shulkerBoxes = shulker,
            doubleChests = chests
        )
    }

    companion object {
        fun provideFactory(repository: TutorialRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(repository) as T
                }
            }
    }
}
