package com.example.domain.entities

val Meal.isVegetarian: Boolean
    get() {
        if (id.startsWith("indian_")) {
            return !category.equals("Chicken", ignoreCase = true)
        }
        val categoryLower = category.lowercase()
        if (categoryLower == "vegetarian" || categoryLower == "vegan" || categoryLower == "dessert") {
            return true
        }
        
        val nameLower = name.lowercase()
        val textToSearch = (nameLower + " " + ingredients.joinToString(" ").lowercase())
        val meatKeywords = listOf(
            "chicken", "beef", "pork", "fish", "lamb", "mutton", "shrimp", "seafood",
            "bacon", "turkey", "meat", "steak", "prawn", "salmon", "crab", "tuna",
            "anchovy", "duck", "ham", "sausage", "pepperoni", "veal"
        )
        return meatKeywords.none { textToSearch.contains(it) }
    }

val Meal.isVegan: Boolean
    get() {
        if (category.equals("vegan", ignoreCase = true)) return true
        if (!isVegetarian) return false
        
        val dairyEggKeywords = listOf(
            "milk", "butter", "cheese", "cream", "egg", "yogurt", "ghee", "paneer", 
            "mayo", "honey", "custard", "whey"
        )
        val textToSearch = (name.lowercase() + " " + ingredients.joinToString(" ").lowercase())
        return dairyEggKeywords.none { textToSearch.contains(it) }
    }

val Meal.isNonVeg: Boolean
    get() = !isVegetarian

val Meal.difficulty: String
    get() {
        val ingredientCount = ingredients.size
        val instructionLen = instructions.length
        return when {
            ingredientCount <= 6 || instructionLen < 450 -> "Easy"
            ingredientCount >= 12 || instructionLen > 1100 -> "Hard"
            else -> "Medium"
        }
    }

val Meal.courseType: String
    get() {
        if (id == "indian_butter_chicken" || id == "indian_paneer_tikka" || id == "indian_chole_bhature" || id == "indian_chicken_biryani" || id == "indian_aloo_gobi") {
            return "Main Course"
        }
        if (id == "indian_samosa" || id == "indian_samosa_chaat" || id == "indian_pani_puri") {
            return "Snacks"
        }
        if (id == "indian_dosa") {
            return "Breakfast"
        }
        if (id == "indian_gulab_jamun") {
            return "Dessert"
        }
        
        val catLower = category.lowercase()
        val nameLower = name.lowercase()
        
        return when {
            catLower == "dessert" || nameLower.contains("cake") || nameLower.contains("pudding") || 
            nameLower.contains("cookie") || nameLower.contains("muffin") || nameLower.contains("pie") || 
            nameLower.contains("sweet") || nameLower.contains("tart") || nameLower.contains("brownie") || 
            nameLower.contains("fudge") || nameLower.contains("custard") -> "Dessert"
            
            catLower == "starter" || catLower == "side" || nameLower.contains("soup") || 
            nameLower.contains("salad") || nameLower.contains("roll") || nameLower.contains("bite") || 
            nameLower.contains("wing") || nameLower.contains("dip") || nameLower.contains("bruschetta") -> "Starter"
            
            catLower == "snack" || nameLower.contains("sandwich") || nameLower.contains("burger") || 
            nameLower.contains("fries") || nameLower.contains("chip") || nameLower.contains("toast") || 
            nameLower.contains("cracker") || nameLower.contains("popcorn") -> "Snacks"
            
            catLower == "breakfast" || nameLower.contains("pancake") || nameLower.contains("waffle") || 
            nameLower.contains("omelette") || nameLower.contains("egg") -> "Breakfast"
            
            else -> "Main Course"
        }
    }

data class NutritionInfo(
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int
)

val Meal.estimatedNutrition: NutritionInfo
    get() {
        // Precise ratings for custom Indian meals
        return when (id) {
            "indian_butter_chicken" -> NutritionInfo(450, 32, 14, 30)
            "indian_paneer_tikka" -> NutritionInfo(380, 16, 12, 28)
            "indian_aloo_gobi" -> NutritionInfo(180, 4, 22, 8)
            "indian_chole_bhature" -> NutritionInfo(550, 14, 68, 22)
            "indian_chicken_biryani" -> NutritionInfo(620, 35, 78, 16)
            "indian_dosa" -> NutritionInfo(240, 5, 42, 6)
            "indian_samosa" -> NutritionInfo(160, 3, 20, 8)
            "indian_samosa_chaat" -> NutritionInfo(340, 8, 48, 12)
            "indian_pani_puri" -> NutritionInfo(140, 2, 28, 3)
            "indian_gulab_jamun" -> NutritionInfo(160, 2, 24, 6)
            else -> {
                // Generative realistic baseline estimates based on classifications
                val course = courseType
                val nonVeg = isNonVeg
                when {
                    course == "Dessert" -> NutritionInfo(310, 4, 48, 11)
                    course == "Starter" && nonVeg -> NutritionInfo(240, 18, 8, 14)
                    course == "Starter" -> NutritionInfo(140, 3, 16, 7)
                    course == "Snacks" && nonVeg -> NutritionInfo(290, 15, 22, 16)
                    course == "Snacks" -> NutritionInfo(190, 4, 28, 7)
                    course == "Breakfast" && nonVeg -> NutritionInfo(350, 18, 20, 22)
                    course == "Breakfast" -> NutritionInfo(260, 8, 34, 10)
                    nonVeg -> NutritionInfo(510, 36, 18, 32) // Heavy main non-veg
                    else -> NutritionInfo(360, 12, 44, 15)    // Main veg/vegan
                }
            }
        }
    }
