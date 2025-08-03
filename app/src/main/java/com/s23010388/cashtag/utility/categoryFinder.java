package com.s23010388.cashtag.utility;

import java.util.HashMap;
import java.util.Map;

public class categoryFinder {
    Map<String, String> categoryMap = new HashMap<String, String>() {{
        // Household
        put("Toilet", "Household");
        put("Detergent", "Household");
        put("Soap", "Household");
        put("Shampoo", "Household");
        put("Toilet Paper", "Household");
        put("Bleach", "Household");
        put("Mop", "Household");
        put("Dishwash", "Household");

        // Grocery
        put("Rice", "Grocery");
        put("Lentils", "Grocery");
        put("Sugar", "Grocery");
        put("Salt", "Grocery");
        put("Flour", "Grocery");
        put("Bread", "Grocery");
        put("Pasta", "Grocery");
        put("Cooking Oil", "Grocery");

        // Food
        put("Fish", "Food");
        put("Meat", "Food");
        put("Chicken", "Food");
        put("Eggs", "Food");
        put("Cheese", "Food");
        put("Butter", "Food");

        // Spices
        put("Turmeric", "Spices");
        put("Chili Powder", "Spices");
        put("Cumin", "Spices");
        put("Coriander", "Spices");
        put("Pepper", "Spices");

        // Drinks
        put("Milk", "Beverages");
        put("Tea", "Beverages");
        put("Coffee", "Beverages");
        put("Juice", "Beverages");
        put("Soft Drink", "Beverages");
        put("Water Bottle", "Beverages");

    }};

    public String findCategory(String title) {
        for (String keyword : categoryMap.keySet()) {
            if (title.toLowerCase().contains(keyword.toLowerCase())) {
                return categoryMap.get(keyword);
            }
        }
        return "Other";
    }
}
