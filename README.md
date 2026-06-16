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

## 📸 Screenshots 
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>RecipeNest – App Screenshots</title>
<style>
  body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; max-width: 960px; margin: 0 auto; padding: 40px 24px; background: #fff; color: #1a1a1a; }
  h1 { font-size: 28px; margin-bottom: 6px; }
  .subtitle { color: #666; margin-bottom: 48px; font-size: 15px; }
  .feature { display: flex; gap: 32px; align-items: flex-start; margin-bottom: 60px; }
  .feature.reverse { flex-direction: row-reverse; }
  .feature img { width: 200px; border-radius: 18px; box-shadow: 0 4px 20px rgba(0,0,0,0.15); flex-shrink: 0; }
  .feature-text h2 { font-size: 20px; margin: 0 0 8px; }
  .feature-text p { font-size: 14px; color: #444; line-height: 1.7; margin: 0 0 12px; }
  .feature-text ul { margin: 0; padding-left: 18px; font-size: 14px; color: #444; line-height: 1.9; }
  hr { border: none; border-top: 1px solid #eee; margin: 0 0 60px; }
  @media (max-width: 600px) { .feature, .feature.reverse { flex-direction: column; } .feature img { width: 100%; max-width: 260px; } }
</style>
</head>
<body>

<h1>📱 RecipeNest</h1>
<p class="subtitle">An AI-powered recipe app focused on Indian cuisine — with smart dietary filters, step-by-step instructions, and a Gemini-powered kitchen assistant.</p>

<div class="feature">
  <img src="WhatsApp_Image_2026-06-16_at_2_34_20_PM.jpeg" alt="Home Screen – Light Mode">
  <div class="feature-text">
    <h2>🏠 Home Screen</h2>
    <p>The Explore tab greets users with a <strong>Meal of the Day</strong> card — a full-width food photo with cuisine tag and diet label. Below it, cuisine categories and the Indian Selection grid with calorie badges.</p>
    <ul>
      <li>Daily recipe recommendation with refresh button</li>
      <li>Horizontal category chips: Indian, Beef, Chicken…</li>
      <li>Diet filter row: All · Veg · Non-Veg · Vegan</li>
      <li>Course filter: All Courses · Starters · Mains · Snacks</li>
      <li>Recipe tiles showing difficulty and calorie count</li>
    </ul>
  </div>
</div>
<hr>

<div class="feature reverse">
  <img src="WhatsApp_Image_2026-06-16_at_2_34_20_PM__2_.jpeg" alt="Home Screen – Dark Mode">
  <div class="feature-text">
    <h2>🌙 Dark / Light Mode</h2>
    <p>A toggle in the top-right corner switches between <strong>Light Mode</strong> and <strong>Dark Mode</strong>. The orange accent system is preserved in both themes.</p>
    <ul>
      <li>One-tap toggle with moon/sun icon</li>
      <li>Consistent colour hierarchy across both themes</li>
      <li>Dark mode uses deep charcoal backgrounds for comfortable night cooking</li>
    </ul>
  </div>
</div>
<hr>

<div class="feature">
  <img src="WhatsApp_Image_2026-06-16_at_2_34_19_PM.jpeg" alt="Recipe Detail Screen">
  <div class="feature-text">
    <h2>🍽️ Recipe Detail — Overview</h2>
    <p>Tapping a recipe opens a detail screen with a <strong>hero food photo</strong>, nutrition summary, difficulty badge, category tags, and a YouTube link for video guidance.</p>
    <ul>
      <li>Estimated nutrition: Calories · Protein · Carbs · Fat</li>
      <li>Difficulty level (Easy / Medium / Hard)</li>
      <li>Category tags (e.g. Vegetarian · Indian · Breakfast)</li>
      <li>Red "Watch on YouTube" CTA button</li>
      <li>Floating orange heart button to save to Favourites</li>
    </ul>
  </div>
</div>
<hr>

<div class="feature reverse">
  <img src="WhatsApp_Image_2026-06-16_at_2_34_17_PM__2_.jpeg" alt="Ingredients Checklist">
  <div class="feature-text">
    <h2>✅ Ingredients Checklist</h2>
    <p>Every ingredient is a <strong>tappable checklist row</strong> — name in white, quantity in orange — so you can track what you have while shopping or prepping.</p>
    <ul>
      <li>Circle checkbox per ingredient</li>
      <li>Quantity highlighted in orange for quick scanning</li>
      <li>Scrollable list for longer recipes</li>
    </ul>
  </div>
</div>
<hr>

<div class="feature">
  <img src="WhatsApp_Image_2026-06-16_at_2_34_16_PM.jpeg" alt="Step-by-Step Instructions">
  <div class="feature-text">
    <h2>📋 Step-by-Step Instructions</h2>
    <p>Cooking steps are numbered with <strong>orange badge circles</strong>, making it easy to follow along at a glance — no collapsing, no truncation.</p>
    <ul>
      <li>Numbered steps with clear, plain-language directions</li>
      <li>Generous line height for easy reading mid-cook</li>
      <li>Continuous scroll — all steps visible</li>
    </ul>
  </div>
</div>
<hr>

<div class="feature reverse">
  <img src="WhatsApp_Image_2026-06-16_at_2_34_18_PM__1_.jpeg" alt="Gemini Kitchen Assistant – Tweak">
  <div class="feature-text">
    <h2>🤖 Gemini Kitchen Assistant — Tweak</h2>
    <p>The AI panel offers three modes. <strong>Tweak</strong> adapts the recipe for a dietary style — select Vegetarian, Vegan, or Gluten-Free and steps rewrite instantly via Gemini Flash Lite.</p>
    <ul>
      <li>One-tap dietary adaptation</li>
      <li>Low-latency responses (Flash Lite model)</li>
      <li>Updates both ingredients and cooking steps</li>
    </ul>
  </div>
</div>
<hr>

<div class="feature">
  <img src="WhatsApp_Image_2026-06-16_at_2_34_18_PM.jpeg" alt="Gemini Kitchen Assistant – Substitutes">
  <div class="feature-text">
    <h2>🔄 Gemini Kitchen Assistant — Substitutes</h2>
    <p>Missing an ingredient? The <strong>Substitutes</strong> tab finds professional replacements, categorised by type, with a reason for each swap.</p>
    <ul>
      <li>Grains & Legumes, Produce, Spices & Oils, Condiments</li>
      <li>Each substitute includes a brief rationale</li>
      <li>"Find Substitutes" button triggers an on-demand AI call</li>
    </ul>
  </div>
</div>
<hr>

<div class="feature reverse">
  <img src="WhatsApp_Image_2026-06-16_at_2_34_17_PM__1_.jpeg" alt="Gemini Kitchen Assistant – Nutrition">
  <div class="feature-text">
    <h2>🥗 Gemini Kitchen Assistant — Nutrition</h2>
    <p>The <strong>Nutrition</strong> tab generates an AI dietitian's analysis: macro breakdown, a star health rating, anti-inflammatory notes, and practical tips for balancing the meal.</p>
    <ul>
      <li>Dietitian's Health Rating (e.g. 4/5 stars)</li>
      <li>Gluten-free / vegan / probiotic flags</li>
      <li>Tips like "pair with sambar for more protein"</li>
      <li>Warnings on calorie-dense ingredients</li>
    </ul>
  </div>
</div>
<hr>

<div class="feature">
  <img src="WhatsApp_Image_2026-06-16_at_2_34_21_PM.jpeg" alt="Search Screen">
  <div class="feature-text">
    <h2>🔍 Search & Favourites</h2>
    <p>The bottom navigation has three tabs: <strong>Explore</strong>, <strong>Search</strong>, and <strong>Favourites</strong>. Recipes saved with the floating heart button are collected in Favourites for quick re-access.</p>
    <ul>
      <li>Full-text search across the recipe library</li>
      <li>Favourites persist across sessions</li>
      <li>Floating orange heart on every recipe screen</li>
    </ul>
  </div>
</div>

</body>
</html>
