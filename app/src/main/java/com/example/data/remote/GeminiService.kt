package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val temperature: Float? = null,
    val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

interface GeminiApi {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @retrofit2.http.Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val client: OkHttpClient by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            redactHeader("x-goog-api-key")
        }
        OkHttpClient.Builder()
            .addInterceptor(logger)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GeminiApi::class.java)
    }

    // Modern model names from SKILL.md rules
    private const val MODEL_LITE = "gemini-3.1-flash-lite-preview"
    private const val MODEL_FLASH = "gemini-3.5-flash"
    private const val MODEL_PRO = "gemini-3.1-pro-preview"

    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    /**
     * Obtains nutritional details for a meal.
     * Uses gemini-3.5-flash or gemini-3.1-pro-preview for complex reasoning.
     */
    suspend fun getNutritionAnalysis(mealName: String, ingredients: List<String>): String {
        val ingredientList = ingredients.joinToString(", ")
        val prompt = "Provide a comprehensive, formatted nutritional analysis for the meal '$mealName' " +
                "made with these ingredients: $ingredientList. Estimate calories, macro distribution " +
                "(Protein, Carbs, Fat) in grams, and give a general health rating with 1-2 bullet points of tips or warnings."

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
            generationConfig = GeminiGenerationConfig(temperature = 0.4f),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = "You are an expert nutritional analyst and dietitian. Always keep your analysis concise, structured, friendly, and easy to read.")))
        )

        return try {
            val response = api.generateContent(MODEL_FLASH, getApiKey(), request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No nutrition data available."
        } catch (e: Exception) {
            "Unable to retrieve nutrition analysis: ${e.localizedMessage}"
        }
    }

    /**
     * Quick low-latency recommendation of ingredient substitutes.
     * Uses gemini-3.1-flash-lite-preview for rapid response.
     */
    suspend fun getIngredientSubstitutes(ingredients: List<String>): String {
        val listText = ingredients.joinToString(", ")
        val prompt = "Give a concise list of alternative/substitute options for these recipe ingredients: $listText. " +
                "Group them clearly and explain *why* (e.g., dairy-free, low-carb, common pantry alternative) in 1-2 sentences per item."

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
            generationConfig = GeminiGenerationConfig(temperature = 0.5f),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = "You are a professional chef. Provide super-fast, low-latency, highly bulleted, short ingredient substitute recommendations.")))
        )

        return try {
            val response = api.generateContent(MODEL_LITE, getApiKey(), request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No substitutes found."
        } catch (e: Exception) {
            "Unable to fetch substitutes: ${e.localizedMessage}"
        }
    }

    /**
     * Fast custom recipe tweaks (e.g., Vegetarian, Keto, Gluten-free, Spicy, Mild).
     * Uses gemini-3.1-flash-lite-preview for ultra-fast, low-latency customized tweaks.
     */
    suspend fun getRecipeTweak(mealName: String, dietaryPreference: String, originalInstructions: String): String {
        val prompt = "Tweak some steps or ingredients for the recipe '$mealName' to suit a '$dietaryPreference' lifestyle. " +
                "Highlight the precise changes in a clear, concise bulleted summary. Original recipe summary / context:\n$originalInstructions"

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
            generationConfig = GeminiGenerationConfig(temperature = 0.6f),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = "You are a dynamic recipe adapter. Give rapid, bulletpoint-based modifications for dietary preferences.")))
        )

        return try {
            val response = api.generateContent(MODEL_LITE, getApiKey(), request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No adaptation suggestions available."
        } catch (e: Exception) {
            "Unable to fetch recipe tweaks: ${e.localizedMessage}"
        }
    }

    /**
     * General chatbot companion on the recipes details screen.
     * Uses gemini-3.5-flash for intelligent culinary conversation.
     */
    suspend fun askChefAssistant(mealName: String, question: String, ingredients: List<String>, instructions: String): String {
        val ingredientText = ingredients.joinToString(", ")
        val systemMessage = "You are 'Chef Nest Bot', a smart, friendly culinary AI companion. " +
                "The user is viewing a recipe for '$mealName'. " +
                "Ingredients: $ingredientText. Instructions: $instructions."

        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = question)))),
            generationConfig = GeminiGenerationConfig(temperature = 0.7f),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemMessage)))
        )

        return try {
            val response = api.generateContent(MODEL_FLASH, getApiKey(), request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Chef Bot is currently speechless."
        } catch (e: Exception) {
            "Chef Bot is offline: ${e.localizedMessage}"
        }
    }
}
