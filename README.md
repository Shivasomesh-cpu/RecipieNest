# 🍳 RecipeNest — Smart Recipe Explorer & AI Sous Chef

**RecipeNest** is a dynamic, high-performance, edge-to-edge native Android application engineered strictly in **Kotlin** and **Jetpack Compose**. 

RecipeNest delivers a zero-latency, highly customized, and fully optimized native experience utilizing industry-standard Android Architecture Components, local Room database persistence, and advanced multi-tier Gemini large language model integration.

---

## 🎨 Visual Identity & Architecture Showcase

*   **100% Native Jetpack Compose**: Beautiful Material Design 3 (M3) components, fluid animations, dynamic typography scale, and standard edge-to-edge system bar handling (`enableEdgeToEdge`).
*   **Fully Offline-First Data Cache**: Powered by a robust SQLite caching engine via **Room DB**—favourites, customized ingredient lists, and previously fetched recipes remain instantly accessible even in zero-reception scenarios.
*   **Triple-Core AI Assistants**: Leverage powerful REST calls to multiple server-side Gemini models (such as `gemini-3.5-flash` and `gemini-3.1-flash-lite-preview`) to perform comprehensive calorie analyses, dynamic dietary substitutions, and host a live cooking chat companion.

---

## 🏗️ Architectural Pattern: MVVM (Clean Architecture)

RecipeNest is structured according to clean, decoupled MVVM layers to isolate presentation logic, business rules, and remote/local data sources:

```
com.example/
├── MainActivity.kt           # App container of modern type-safe Jetpack Navigation Graph
├── data/
│   ├── local/                # Persistent SQLite Storage Layer (Entity, DAO, Room RoomDatabase, Converters)
│   ├── remote/               # Networking pipelines (Retrofit 2 with Moshi + OkHttp with standard interceptors)
│   └── repository/           # Repository Pattern implementing local/remote single-source-of-truth coordination
├── domain/
│   ├── entities/             # Plain Kotlin Data Class Models (Meal, Category) decouples engine from frameworks
│   └── repositories/         # Business interface specifications 
└── ui/
    ├── screens/              # Modular Compose Screens (HomeScreen, DetailScreen, SearchScreen, FavouritesScreen)
    ├── theme/                # Global Material Design 3 central ColorScheme, standard shapes, and Typography
    ├── viewmodel/            # Lifecycle-bound State Controllers utilizing Coroutine StateFlow pipelines
    └── widgets/              # Reusable design atoms (ShimmerEffect, MealCard, EmptyState, IngredientTile)
```

### 📂 High-Quality Separation of Concerns:
1.  **Strict State Management**: Screens do not manipulate state directly. Events are funneled cleanly to the `MealViewModel`, which streams updates down via `StateFlow` collected inside Composable objects in a lifecycle-aware manner.
2.  **No Core Raw API Calls in Views**: Reusable screens bind purely to high-level domain entities, abstracting the API network and SQL database complexity away.
3.  **Global Interception Framework**: Network requests are managed via standard OkHttp interceptors. For example, Coil's image loading pipeline has been over-configured globally to inject a realistic browser `User-Agent` to circumvent Unsplash anti-hotlinking limitations.

---

## 🌟 Application Features

RecipeNest provides a fully rounded experience with optimal offline capabilities:

| Feature | Description | Tech Stack Component |
| :--- | :--- | :--- |
| **Multi-Screen Navigation** | **4 Distinct Screens**: Explore-Home, Universal Search, Local Bookmarked Favourites, and Rich Deep Details. | Jetpack Navigation Compose |
| **API Consumption** | Continuous streaming of rich, categorized dishes directly from external culinary feeds. | Retrofit 2, Moshi, OkHttp 3 |
| **Database Storage** | Local SQL schema persisting user favorites, customized configurations, and recipe notes cleanly. | Room SQLite Database & KSP |
| **Clean Architecture** | Abstract Repository Pattern with strict Separation of Concerns (Domain entities are separated from Data/UI). | MVVM, Clean Architecture |
| **AI Integrations** | Smart ingredient substituter, dietary recipe customizer, nutrition analyzer, and interactive chat assistant. | Server-side Gemini API Integration |

---

## 🛠️ Complete Tech Stack Specs

*   **Language**: Kotlin (100% Type-Safe)
*   **Asynchronous Engine**: Kotlin Coroutines & Kotlin StateFlows
*   **UI Toolkit**: Declarative Jetpack Compose using Material Design 3
*   **Local DBMS**: Room Persistence Library with standard Entity/DAO abstraction
*   **HTTP Pipelines**: Retrofit 2 + Moshi Converter Factory
*   **Image Cache**: Coil 3 asynchronous disk/memory image loader with customized OkHttp intercepts
*   **Safety Assertions**: Strict exception shielding (`try-catch` structures) and type-safe argument navigation

---

## 🚀 Setup & Execution Guide

Follow these simple instructions to compile, build, and run **RecipeNest** on your local system:

### 1. Prerequisites
*   Android Studio Ladybug (or newer)
*   Android SDK 35 (Target) & Minimum SDK 26 (Android Oreo)
*   Gradle 8.0+

### 2. Configure Local API Credentials
RecipeNest maintains strict security measures around sensitive API credentials to prevent public repository leaks. API Keys are automatically parsed via Gradle `BuildConfig` from a secure local environment template:

1.  Create a `.env` file in the project's root directory:
    ```env
    GEMINI_API_KEY=your_actual_google_ai_studio_api_key_here
    ```
2.  The Kotlin compiler will automatically parse `BuildConfig.GEMINI_API_KEY` at build time to safely integrate the AI services.

### 3. Build & Run
1.  Launch Android Studio and choose **Open an Existing Project**, selecting the root directory.
2.  Allow Gradle to automatically download and sync all standard dependencies defined inside `build.gradle.kts`.
3.  Connect an Active Android Virtual Device (AVD) or physical developer device via ADB.
4.  Click the teal **Run App** (Play icon) on the top bar.

---

## 📸 Screenshots & Dynamic Demo
*(Place high-resolution screenshots and animated GIFs of your user journeys here)*
