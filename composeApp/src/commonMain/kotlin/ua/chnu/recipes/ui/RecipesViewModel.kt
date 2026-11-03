package ua.chnu.recipes.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.chnu.recipes.data.Recipe
import ua.chnu.recipes.data.RecipesApi

/** Meal-type filter chips; [apiValue] is the value used by dummyjson's `mealType` field. */
enum class MealFilter(val apiValue: String?) {
    All(null),
    Breakfast("Breakfast"),
    Lunch("Lunch"),
    Dinner("Dinner"),
}

/** State holder for the recipe list screen (search + meal-type filter + loading/error state). */
class RecipesViewModel(
    private val api: RecipesApi,
    private val scope: CoroutineScope,
) {
    private val log = Logger.withTag("RecipesViewModel")

    var query by mutableStateOf("")
        private set
    var mealFilter by mutableStateOf(MealFilter.All)
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf(false)
        private set
    private var loaded by mutableStateOf<List<Recipe>>(emptyList())

    val recipes: List<Recipe>
        get() = mealFilter.apiValue
            ?.let { meal -> loaded.filter { recipe -> recipe.mealType.any { it.equals(meal, ignoreCase = true) } } }
            ?: loaded

    private var job: Job? = null

    fun onQueryChange(value: String) {
        query = value
        load(debounce = true)
    }

    fun onMealFilterChange(value: MealFilter) {
        log.d { "filter -> $value" }
        mealFilter = value
    }

    fun load(debounce: Boolean = false) {
        job?.cancel()
        job = scope.launch {
            if (debounce) delay(350)
            loading = true
            error = false
            try {
                loaded = api.getRecipes(query)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.e(e) { "Failed to load recipes" }
                error = true
            } finally {
                loading = false
            }
        }
    }
}
