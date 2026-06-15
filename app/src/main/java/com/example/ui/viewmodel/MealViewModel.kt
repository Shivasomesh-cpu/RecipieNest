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

    fun loadMealDetail(mealId: String, forceRefresh: Boolean = false) {
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
