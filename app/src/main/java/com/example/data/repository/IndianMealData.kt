package com.example.data.repository

import com.example.domain.entities.Meal

object IndianMealData {
    val meals = listOf(
        Meal(
            id = "indian_butter_chicken",
            name = "Classic Butter Chicken (Murgh Makhani)",
            category = "Chicken",
            area = "Indian",
            instructions = "1. Marinate chicken pieces in ginger-garlic paste, yogurt, kashmiri chili powder, garam masala, and lemon juice for 2 hours.\n" +
                    "2. Grill or pan-sear the marinated chicken until cooked and slightly charred. Set aside.\n" +
                    "3. For the makhani gravy: Melt butter in a pan, add cardamom, cloves, cinnamon, and ginger-garlic paste.\n" +
                    "4. Add pureed ripe tomatoes, cashew nuts paste, and simmer for 15 minutes.\n" +
                    "5. Blend the gravy to a smooth velvety texture, filter if desired, then return to pan.\n" +
                    "6. Add roasted chicken pieces, cream, kasuri methi (dried fenugreek leaves), and a touch of honey.\n" +
                    "7. Simmer for 5 minutes and garnish with fresh cream and chopped cilantro.",
            thumbnail = "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?auto=format&fit=crop&q=80&w=600",
            youtubeUrl = "https://www.youtube.com/watch?v=a03U45jFxOI",
            ingredients = listOf("Chicken", "Yogurt", "Ginger-Garlic Paste", "Butter", "Tomato Puree", "Cashew Paste", "Heavy Cream", "Kasuri Methi", "Garam Masala", "Chili Powder"),
            measures = listOf("800g", "1/2 cup", "2 tbsp", "100g", "2 cups", "3 tbsp", "1/2 cup", "1 tbsp", "1.5 tsp", "2 tsp")
        ),
        Meal(
            id = "indian_paneer_tikka",
            name = "Paneer Tikka Masala",
            category = "Vegetarian",
            area = "Indian",
            instructions = "1. Cut paneer (cottage cheese), bell peppers, and red onions into cubes.\n" +
                    "2. Whisk thick hung curd (yogurt) with mustard oil, tandoori masala, kashmiri chili powder, and lemon juice.\n" +
                    "3. Toss the paneer and vegetables in the marinade and rest for 30 minutes.\n" +
                    "4. Thread paneer and veggies onto wooden skewers and grill in an oven or on a hot tawa until edges are charred.\n" +
                    "5. To make tikka masala sauce: Sauté onions and ginger-garlic paste in a pan. Add ground coriander, turmeric, and chili powder.\n" +
                    "6. Pour in fresh tomato puree and cook until oil separates. Stir in cashew butter/paste and warm water to form a thick curry.\n" +
                    "7. Gently slide the grilled skewers/paneer and veggies into the bubbling gravy. Cook on low heat for 5 minutes.\n" +
                    "8. Garnish with a sprinkle of garam masala, coriander leaves, and ginger juliennes.",
            thumbnail = "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?auto=format&fit=crop&q=80&w=600",
            youtubeUrl = "https://www.youtube.com/watch?v=QAALghvK8Cg",
            ingredients = listOf("Paneer", "Yogurt", "Bell Peppers", "Onions", "Tomato Puree", "Ginger-Garlic Paste", "Tandoori Masala", "Heavy Cream", "Mustard Oil", "Cashew Paste"),
            measures = listOf("400g", "1/2 cup", "2 medium", "2 medium", "1.5 cups", "1.5 tbsp", "2 tsp", "2 tbsp", "1 tbsp", "2 tbsp")
        ),
        Meal(
            id = "indian_aloo_gobi",
            name = "Spicy Aloo Gobi (Vegan)",
            category = "Vegetarian",
            area = "Indian",
            instructions = "1. Cut cauliflower into medium medium-sized florets and potatoes (aloo) into small cubes.\n" +
                    "2. Heat vegetable oil in a heavy-bottomed wok. Sauté cumin seeds and a pinch of hing (asafoetida) until sizzling.\n" +
                    "3. Add chopped ginger, green chilies, and potato cubes. Stir fry for 5 minutes until potatoes start getting golden.\n" +
                    "4. Toss in the cauliflower florets, turmeric, coriander powder, cumin powder, and chili powder. Mix well.\n" +
                    "5. Cover with a tight-fitting lid and cook on low-medium heat for 15-20 minutes, stirring occasionally. Do not add water (cook in steam).\n" +
                    "6. Once the vegetables are tender, uncover, sprinkle amchur powder (dry mango powder) and garam masala.\n" +
                    "7. Stir gently on high flame for 2-3 minutes to crisp the edges slightly.\n" +
                    "8. Mix in plenty of fresh finely chopped coriander leaves and serve hot.",
            thumbnail = "https://images.unsplash.com/photo-1565557623262-b51c2513a641?auto=format&fit=crop&q=80&w=600",
            youtubeUrl = "https://www.youtube.com/watch?v=E_MvVjUInI0",
            ingredients = listOf("Potatoes", "Cauliflower Florets", "Vegetable Oil", "Cumin Seeds", "Ginger", "Green Chilies", "Turmeric Powder", "Coriander Powder", "Amchur Powder", "Garam Masala"),
            measures = listOf("2 large", "1 medium", "3 tbsp", "1 tsp", "1 inch", "2 pieces", "1/2 tsp", "1.5 tsp", "1 tsp", "1/2 tsp")
        ),
        Meal(
            id = "indian_chole_bhature",
            name = "Punjabi Chole Bhature",
            category = "Vegetarian",
            area = "Indian",
            instructions = "1. Soak chickpeas (Kabuli Chana) overnight with a tea bag for deep color.\n" +
                    "2. Boil chickpeas in a pressure cooker with black cardamom, bay leaves, cinnamon, and salt until completely soft.\n" +
                    "3. In a pot, heat oil, sauté chopped onions, ginger, and garlic paste until golden brown.\n" +
                    "4. Add tomato puree, chole masala, cumin, coriander, turmeric, and kashmiri chili powder. Cook until oil detaches.\n" +
                    "5. Stir in the boiled chickpeas along with some boiling water. Mash a few chickpeas to thicken the gravy.\n" +
                    "6. Simmer for 15 minutes, finish with amchur powder and a hot ghee tadka of slit green chilies and julienne ginger.\n" +
                    "7. For Bhature: Knead refined flour (maida) with yogurt, semolina, sugar, pinch of baking soda, and warm water. Let rest 2 hours.\n" +
                    "8. Roll out round/oval shapes and deep fry in piping hot oil until they puff up like large golden balloons. Serve immediately.",
            thumbnail = "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?auto=format&fit=crop&q=80&w=600",
            youtubeUrl = "https://www.youtube.com/watch?v=8m9gLd_oP7w",
            ingredients = listOf("Chickpeas", "Onions", "Tomato Puree", "Maida Flour", "Yogurt", "Chole Masala", "Ginger-Garlic Paste", "Green Chilies", "Ghee", "Baking Soda"),
            measures = listOf("2 cups", "2 large", "1 cup", "2 cups", "1/2 cup", "2 tbsp", "1 tbsp", "3 pieces", "2 tbsp", "1/4 tsp")
        ),
        Meal(
            id = "indian_chicken_biryani",
            name = "Hyderabadi Dum Chicken Biryani",
            category = "Chicken",
            area = "Indian",
            instructions = "1. Marinate chicken in mint, coriander, yogurt, lemon juice, ginger-garlic paste, biryani masala, and fried onions (birista) overnight.\n" +
                    "2. Par-boil premium Basmati rice in plenty of water seasoned with whole cloves, star anise, cardamoms, and bay leaves until 70% cooked.\n" +
                    "3. In a heavy handi pot, layer the marinated chicken at the bottom, then spread the par-boiled rice evenly over it.\n" +
                    "4. Layer with finely chopped mint, coriander, fried onions, a drizzle of pure ghee, and saffron-infused warm milk.\n" +
                    "5. Seal the pot's lid tightly using a wheat dough paste around the edges to trap the aromatic steam.\n" +
                    "6. Cook on medium-high heat for 10 minutes, then place a flat iron tawa under the handi, lowering the flame to absolute minimum.\n" +
                    "7. Let it slow-cook (Dum) for 35-40 minutes inside its own trapped steam.\n" +
                    "8. Break the seal, fluff carefully, showing off beautiful distinct white, yellow, and orange rice grains with succulent meat.",
            thumbnail = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&q=80&w=600",
            youtubeUrl = "https://www.youtube.com/watch?v=1F771S4680U",
            ingredients = listOf("Basmati Rice", "Chicken Pieces", "Yogurt", "Fried Onions", "Ghee", "Mint & Coriander", "Biryani Masala", "Ginger-Garlic Paste", "Saffron Milk", "Whole Spices"),
            measures = listOf("3 cups", "1 kg", "1 cup", "1 cup", "4 tbsp", "1 cup", "2.5 tbsp", "2 tbsp", "1/4 cup", "Adjustable")
        ),
        Meal(
            id = "indian_dosa",
            name = "Crispy South Indian Masala Dosa (Vegan)",
            category = "Vegetarian",
            area = "Indian",
            instructions = "1. Soak rice and whole white split urad dal (lentils) with fenugreek seeds for 5 hours. Grind into a silky-smooth thick batter.\n" +
                    "2. Ferment the batter in a warm dark spot for 12 hours until it rises, bubbles, and becomes pleasantly sour.\n" +
                    "3. Prepare potato masala: Sauté mustard seeds, curry leaves, ginger, onions, turmeric, and boiled hand-mashed potatoes.\n" +
                    "4. Heat a heavy flat iron tawa tandoor/griddle, grease with a drop of coconut oil, then splash water to cool down and wipe clean.\n" +
                    "5. Pour a ladle of fermented batter in the center. Using the back of the ladle, sweep in concentric circles outward to make a thin crepe.\n" +
                    "6. Drizzle coconut oil or ghee along the edges. Cook on medium-high until the bottom turns highly golden-brown and ultra-crisp.\n" +
                    "7. Place a scoop of potato masala in the center, fold fold into a cylinder or triangle.\n" +
                    "8. Serve instantly with thick coconut chutney and red sambar.",
            thumbnail = "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?auto=format&fit=crop&q=80&w=600",
            youtubeUrl = "https://www.youtube.com/watch?v=S8Y_26VfGjw",
            ingredients = listOf("Rice", "Urad Dal", "Potatoes", "Mustard Seeds", "Curry Leaves", "Onions", "Turmeric Powder", "Coconut Oil", "Ginger", "Coconut Chutney"),
            measures = listOf("3 cups", "1 cup", "4 medium", "1 tsp", "10 leaves", "1 large", "1/2 tsp", "2 tbsp", "1 tbsp", "Side")
        )
    )
}
