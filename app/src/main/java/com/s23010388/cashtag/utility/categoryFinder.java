package com.s23010388.cashtag.utility;

import java.util.HashMap;
import java.util.Map;

public class categoryFinder {
    Map<String, String> categoryMap = new HashMap<String, String>() {{
        put("Toilet", "Household");
        put("Detergent", "Household");
        put("Soap", "Household");
        put("Rice", "Grocery");
        put("Lentils", "Grocery");
        put("Sugar", "Grocery");
        put("Fish", "Food");
        put("Meat", "Food");
        put("Turmeric", "Spices");

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
