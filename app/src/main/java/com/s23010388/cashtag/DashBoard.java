package com.s23010388.cashtag;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.s23010388.cashtag.models.Expense;
import com.s23010388.cashtag.storage.AppDatabase;
import com.s23010388.cashtag.storage.ExpenseManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashBoard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashBoard extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashBoard() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashBoard.
     */
    // TODO: Rename and change types and number of parameters
    public static DashBoard newInstance(String param1, String param2) {
        DashBoard fragment = new DashBoard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dash_board, container, false);


        // New class instance named expense_manager
        double total = ExpenseManager.getTotalSpent();


        // TextView the total amount
        TextView total_text = view.findViewById(R.id.totalAmount);
        LinearLayout progressContainer = view.findViewById(R.id.progress_container);
        LayoutInflater inflater2 = LayoutInflater.from(getContext());

// Get all expenses
        AppDatabase db = AppDatabase.getInstance(requireContext());

       // List<Expense> allExpenses = db.expenseDao().getAll();
       // total_text.setText("Loading...");

        // Get the total spent
        Double totalSpent = db.expenseDao().getTotalSpent();

        // If totalSpent is null, set it to 0.0
        if (totalSpent == null) {
            totalSpent = 0.0;
        }
// buttons for change mode by date
        MaterialButton btnDay = view.findViewById(R.id.btn_day);
        MaterialButton btnMonth = view.findViewById(R.id.btn_month);
        MaterialButton btnYear = view.findViewById(R.id.btn_year);

        btnDay.setOnClickListener(v -> updateDashboard("day"));
        btnMonth.setOnClickListener(v -> updateDashboard("month"));
        btnYear.setOnClickListener(v -> updateDashboard("year"));

// Update the UI with the total spent
       // total_text.setText(String.format("Rs. %.2f", totalSpent));
// set the default view
        view.post(() -> updateDashboard("day"));

        //view.post(() -> calculateCategoryProgress());

        return view;
    }
    public void updateDashboard(String mode){
        TextView totalMainText = requireView().findViewById(R.id.totalViewMain);
        String textSubMain = "";
        if (mode.equals("day")){
            textSubMain = "Daily";
        } else if (mode.equals("month")) {
            textSubMain = "Monthly";
        } else if (mode.equals("year")) {
            textSubMain = "Yearly";
        }
        totalMainText.setText(String.format("Total %s Spendings",textSubMain));

        AppDatabase db = AppDatabase.getInstance(requireContext());
        List<Expense> expensesByDate = new ArrayList<>();

        List<Expense> allExpenses = db.expenseDao().getAll();
        long timeNow = System.currentTimeMillis();

        for (Expense expense : allExpenses){
            long date = expense.getDate();
            boolean match = false;

            if (mode.equals("day")) {
                match = isSameDay(timeNow,date);
            } else if (mode.equals("month")) {
                match = isSameMonth(timeNow,date);
            } else if (mode.equals("year")) {
                match = isSameYear(timeNow,date);
            }
            if (match) {
                expensesByDate.add(expense);

            }
        }
        //clear all views
        LinearLayout progressBarContainer = requireView().findViewById(R.id.progress_container);
        progressBarContainer.removeAllViews();

        //Update total amount text
        double totalSpent = 0;
        for (Expense expense : expensesByDate){
            totalSpent += expense.getAmount();
        }
        // display updated amount
        TextView total_text = requireView().findViewById(R.id.totalAmount);
        total_text.setText(String.format("Rs. %.2f", totalSpent));

        //update progress bars
        Map<String, Double> categoryTotalMap = new HashMap<>();
        for (Expense expense : expensesByDate) {
            categoryTotalMap.put(expense.getCategory(),
                    categoryTotalMap.getOrDefault(expense.getCategory(), 0.0) + expense.getAmount());
        }

        List<Map.Entry<String, Double>> sortedList = new ArrayList<>(categoryTotalMap.entrySet());
        sortedList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (Map.Entry<String, Double> entry : sortedList) {
            String category = entry.getKey();
            double categoryTotal = entry.getValue();
            double percentage = (totalSpent > 0) ? (categoryTotal / totalSpent * 100) : 0;
            updateProgressBar(category, percentage);
        }
    }
    // Category progress function
    public void calculateCategoryProgress(){
        AppDatabase db = AppDatabase.getInstance(requireContext());
        List<Expense> allExpenses = db.expenseDao().getAll();

        double totalSpent = 0;
        Map<String , Double> categoryTotalMap = new HashMap<>();

        for (Expense expense : allExpenses){
            totalSpent += expense.getAmount();
            categoryTotalMap.put(expense.getCategory(),categoryTotalMap.getOrDefault(expense.getCategory(),0.0)+expense.getAmount());

        }
        // Convert the map to a list and sort by value (highest first)
        List<Map.Entry<String, Double>> sortedList = new ArrayList<>(categoryTotalMap.entrySet());
        sortedList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));


        for (Map.Entry<String , Double> entry : sortedList){
            String category = entry.getKey();
            double categoryTotal = entry.getValue();
            double percentage = (categoryTotal/totalSpent * 100);

            updateProgressBar(category, percentage);

        }
    }

    public void updateProgressBar(String category, double percentage) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        // Make sure container is defined first
        LinearLayout progressContainer = requireView().findViewById(R.id.progress_container);

        // Inflate new progress item using container’s layout rules
        View progressItem = inflater.inflate(R.layout.category_progress, progressContainer, false);

        // Set category name
        TextView categoryName = progressItem.findViewById(R.id.categoryName);
        categoryName.setText(category);

        // Set percentage
        ProgressBar progressBar = progressItem.findViewById(R.id.categoryProgress);
        progressBar.setProgress((int) percentage);

        // Add the new view to the container
        progressContainer.addView(progressItem);
    }
    // Date match methods
    private boolean isSameDay(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isSameMonth(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    private boolean isSameYear(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
}