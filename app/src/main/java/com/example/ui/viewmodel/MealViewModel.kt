package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.Category
import com.example.domain.entities.Meal
import com.example.domain.repositories.MealRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class MealViewModel(private val repository: MealRepository) : ViewModel() {

    // --- HOME / EXPLORE SCREEN STATES ---
    private val _mealOfTheDayState = MutableStateFlow<UiState<Meal>>(UiState.Loading)
    val mealOfTheDayState: StateFlow<UiState<Meal>> = _mealOfTheDayState.asStateFlow()

    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<Category>>> = _categoriesState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _categoryMealsState = MutableStateFlow<UiState<List<Meal>>>(UiState.Idle)
    val categoryMealsState: StateFlow<UiState<List<Meal>>> = _categoryMealsState.asStateFlow()

    // --- SEARCH SCREEN STATES ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchState = MutableStateFlow<UiState<List<Meal>>>(UiState.Idle)
    val searchState: StateFlow<UiState<List<Meal>>> = _searchState.asStateFlow()

    // --- DIET & COURSE FILTERS ---
    private val _selectedDietaryFilter = MutableStateFlow("All")
    val selectedDietaryFilter: StateFlow<String> = _selectedDietaryFilter.asStateFlow()

    private val _selectedCourseFilter = MutableStateFlow("All")
    val selectedCourseFilter: StateFlow<String> = _selectedCourseFilter.asStateFlow()

    fun selectDietaryFilter(filter: String) {
        _selectedDietaryFilter.value = filter
    }

    fun selectCourseFilter(filter: String) {
        _selectedCourseFilter.value = filter
    }

    // --- DETAIL SCREEN STATES ---
    // Maps mealId to its corresponding UiState
    private val _mealDetailStates = MutableStateFlow<Map<String, UiState<Meal>>>(emptyMap())
    val mealDetailStates: StateFlow<Map<String, UiState<Meal>>> = _mealDetailStates.asStateFlow()

    // --- GEMINI AI ASSISTANT STATES ---
    private val _tweakResult = MutableStateFlow("")
    val tweakResult: StateFlow<String> = _tweakResult.asStateFlow()

    private val _isTweakLoading = MutableStateFlow(false)
    val isTweakLoading: StateFlow<Boolean> = _isTweakLoading.asStateFlow()

    private val _tweakPref = MutableStateFlow("")
    val tweakPref: StateFlow<String> = _tweakPref.asStateFlow()

    private val _subsResult = MutableStateFlow("")
    val subsResult: StateFlow<String> = _subsResult.asStateFlow()

    private val _isSubsLoading = MutableStateFlow(false)
    val isSubsLoading: StateFlow<Boolean> = _isSubsLoading.asStateFlow()

    private val _nutritionResult = MutableStateFlow("")
    val nutritionResult: StateFlow<String> = _nutritionResult.asStateFlow()

    private val _isNutritionLoading = MutableStateFlow(false)
    val isNutritionLoading: StateFlow<Boolean> = _isNutritionLoading.asStateFlow()

    private val _chefQuestion = MutableStateFlow("")
    val chefQuestion: StateFlow<String> = _chefQuestion.asStateFlow()

    private val _chefResponse = MutableStateFlow("")
    val chefResponse: StateFlow<String> = _chefResponse.asStateFlow()

    private val _isChefLoading = MutableStateFlow(false)
    val isChefLoading: StateFlow<Boolean> = _isChefLoading.asStateFlow()

    // --- FAVOURITES STATE ---
    val favoriteMeals: StateFlow<List<Meal>> = repository.getFavoriteMeals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadHomeScreenData()
        observeSearchQuery()
    }

    fun loadHomeScreenData() {
        viewModelScope.launch {
            _mealOfTheDayState.value = UiState.Loading
            _categoriesState.value = UiState.Loading
            try {
                // Fetch random meal
                val randomMeal = repository.getRandomMeal()
                _mealOfTheDayState.value = UiState.Success(randomMeal)
                
                // Fetch categories
                val categories = repository.getCategories()
                _categoriesState.value = UiState.Success(categories)
                
                // Select first category by default
                if (categories.isNotEmpty()) {
                    selectCategory(categories.first().name)
                }
            } catch (e: Exception) {
                _mealOfTheDayState.value = UiState.Error(e.localizedMessage ?: "Failed to connect")
                _categoriesState.value = UiState.Error(e.localizedMessage ?: "Failed to fetch categories")
            }
        }
    }

    fun selectCategory(categoryName: String) {
        _selectedCategory.value = categoryName
        viewModelScope.launch {
            _categoryMealsState.value = UiState.Loading
            try {
                val meals = repository.getMealsByCategory(categoryName)
                _categoryMealsState.value = UiState.Success(meals)
            } catch (e: Exception) {
                _categoryMealsState.value = UiState.Error(e.localizedMessage ?: "Failed to load meals")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchState.value = UiState.Idle
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isNotBlank()) {
                        executeSearch(query)
                    }
                }
        }
    }

    fun executeSearch(query: String) {
        viewModelScope.launch {
            _searchState.value = UiState.Loading
            try {
                val results = repository.searchMeals(query)
                _searchState.value = UiState.Success(results)
            } catch (e: Exception) {
                _searchState.value = UiState.Error(e.localizedMessage ?: "Search failed")
            }
        }
    }

    private var activeMealId: String? = null

    fun loadMealDetail(mealId: String, forceRefresh: Boolean = false) {
        if (activeMealId != mealId) {
            activeMealId = mealId
            clearAiStates()
        }

        val currentStates = _mealDetailStates.value
        if (currentStates[mealId] is UiState.Success && !forceRefresh) {
            // Already loaded, don't re-trigger loading
            return
        }

        viewModelScope.launch {
            _mealDetailStates.update { it + (mealId to UiState.Loading) }
            try {
                // Fetch fresh from remote API
                val mealDetails = repository.getMealById(mealId)
                _mealDetailStates.update { it + (mealId to UiState.Success(mealDetails)) }
            } catch (e: Exception) {
                // Check if it's already saved in favourites. If so, we can fallback to the local DB version offline!
                val offlineMeal = favoriteMeals.value.firstOrNull { it.id == mealId }
                if (offlineMeal != null) {
                    _mealDetailStates.update { it + (mealId to UiState.Success(offlineMeal)) }
                } else {
                    _mealDetailStates.update { it + (mealId to UiState.Error(e.localizedMessage ?: "Failed to fetch recipe details")) }
                }
            }
        }
    }

    fun updateTweakPref(pref: String) {
        _tweakPref.value = pref
    }

    fun updateChefQuestion(question: String) {
        _chefQuestion.value = question
    }

    fun getRecipeTweak(mealName: String, originalInstructions: String, preference: String) {
        _tweakPref.value = preference
        _isTweakLoading.value = true
        viewModelScope.launch {
            try {
                val result = com.example.data.remote.GeminiService.getRecipeTweak(
                    mealName = mealName,
                    dietaryPreference = preference,
                    originalInstructions = originalInstructions
                )
                _tweakResult.value = result
            } catch (e: Exception) {
                _tweakResult.value = "Error: ${e.localizedMessage}"
            } finally {
                _isTweakLoading.value = false
            }
        }
    }

    fun getIngredientSubstitutes(ingredients: List<String>) {
        _isSubsLoading.value = true
        viewModelScope.launch {
            try {
                val result = com.example.data.remote.GeminiService.getIngredientSubstitutes(ingredients)
                _subsResult.value = result
            } catch (e: Exception) {
                _subsResult.value = "Error: ${e.localizedMessage}"
            } finally {
                _isSubsLoading.value = false
            }
        }
    }

    fun getNutritionAnalysis(mealName: String, ingredients: List<String>) {
        _isNutritionLoading.value = true
        viewModelScope.launch {
            try {
                val result = com.example.data.remote.GeminiService.getNutritionAnalysis(mealName, ingredients)
                _nutritionResult.value = result
            } catch (e: Exception) {
                _nutritionResult.value = "Error: ${e.localizedMessage}"
            } finally {
                _isNutritionLoading.value = false
            }
        }
    }

    fun askChefAssistant(mealName: String, question: String, ingredients: List<String>, instructions: String) {
        _isChefLoading.value = true
        _chefQuestion.value = "" // Clear input on submit
        viewModelScope.launch {
            try {
                val result = com.example.data.remote.GeminiService.askChefAssistant(
                    mealName = mealName,
                    question = question,
                    ingredients = ingredients,
                    instructions = instructions
                )
                _chefResponse.value = result
            } catch (e: Exception) {
                _chefResponse.value = "Error: ${e.localizedMessage}"
            } finally {
                _isChefLoading.value = false
            }
        }
    }

    fun clearAiStates() {
        _tweakResult.value = ""
        _isTweakLoading.value = false
        _tweakPref.value = ""
        _subsResult.value = ""
        _isSubsLoading.value = false
        _nutritionResult.value = ""
        _isNutritionLoading.value = false
        _chefQuestion.value = ""
        _chefResponse.value = ""
        _isChefLoading.value = false
    }

    fun toggleFavorite(meal: Meal) {
        viewModelScope.launch {
            val isFav = repository.isFavorite(meal.id)
            if (isFav) {
                repository.removeFavoriteMealById(meal.id)
            } else {
                repository.saveFavoriteMeal(meal)
            }
        }
    }

    fun removeFavoriteById(id: String) {
        viewModelScope.launch {
            repository.removeFavoriteMealById(id)
        }
    }

    fun isFavoriteStream(mealId: String): Flow<Boolean> {
        return favoriteMeals.map { list -> list.any { it.id == mealId } }
    }
}

class MealViewModelFactory(private val repository: MealRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
