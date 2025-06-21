package com.s23010388.cashtag.storage;

import com.s23010388.cashtag.models.Expense;
import java.util.ArrayList;
import java.util.List;

public class ExpenseManager {
    private static final List<Expense> expenseList = new ArrayList<>();

    public static void addExpense(Expense expense) {
        expenseList.add(expense);
    }

    public static List<Expense> getAllExpenses() {
        return new ArrayList<>(expenseList); // return a copy to avoid mutation
    }

    public static double getTotalSpent() {
        double total = 0;
        for (Expense expense : expenseList) {
            total += expense.getAmount();
        }
        return total;
    }

    public static List<Expense> getExpensesByCategory(String category) {
        List<Expense> result = new ArrayList<>();
        for (Expense e : expenseList) {
            if (e.getCategory().equalsIgnoreCase(category)) {
                result.add(e);
            }
        }
        return result;
    }
}
