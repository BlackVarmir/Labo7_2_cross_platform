package ua.chnu.recipes.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import ua.chnu.recipes.resources.Res
import ua.chnu.recipes.resources.difficulty_easy
import ua.chnu.recipes.resources.difficulty_hard
import ua.chnu.recipes.resources.difficulty_medium
import ua.chnu.recipes.resources.meal_all
import ua.chnu.recipes.resources.meal_breakfast
import ua.chnu.recipes.resources.meal_dinner
import ua.chnu.recipes.resources.meal_lunch

@Composable
fun MealFilter.label(): String = stringResource(
    when (this) {
        MealFilter.All -> Res.string.meal_all
        MealFilter.Breakfast -> Res.string.meal_breakfast
        MealFilter.Lunch -> Res.string.meal_lunch
        MealFilter.Dinner -> Res.string.meal_dinner
    }
)

/** Translates dummyjson's English difficulty value; unknown values are shown as they are. */
@Composable
fun difficultyLabel(difficulty: String): String = when (difficulty.lowercase()) {
    "easy" -> stringResource(Res.string.difficulty_easy)
    "medium" -> stringResource(Res.string.difficulty_medium)
    "hard" -> stringResource(Res.string.difficulty_hard)
    else -> difficulty
}
