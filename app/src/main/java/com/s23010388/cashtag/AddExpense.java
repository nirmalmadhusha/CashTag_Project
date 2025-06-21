package com.s23010388.cashtag;

//ML kit imports
import android.graphics.Color;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.CharSequence;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.google.android.material.button.MaterialButton;
import com.s23010388.cashtag.models.Expense;
import com.s23010388.cashtag.storage.AppDatabase;
import com.s23010388.cashtag.utility.textCleaner;
import com.s23010388.cashtag.utility.textFinder;
import com.s23010388.cashtag.utility.categoryFinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddExpense#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddExpense extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddExpense() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddExpense.
     */
    // TODO: Rename and change types and number of parameters
    public static AddExpense newInstance(String param1, String param2) {
        AddExpense fragment = new AddExpense();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // camera Launcher
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri currentImageUri;
    // Save items
    List<EditText> titleFields = new ArrayList<>();
    List<EditText> categoryFields = new ArrayList<>();
    List<EditText> priceFields = new ArrayList<>();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Set launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && currentImageUri != null) {
                        ImageView previewImage = getView().findViewById(R.id.imagePreview);
                        previewImage.setImageURI(currentImageUri);
                        Toast.makeText(getContext(), "Photo Captured", Toast.LENGTH_SHORT).show();
                    }
                });
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        currentImageUri = result.getData().getData(); // store for later
                        ImageView previewImage = getView().findViewById(R.id.imagePreview);
                        previewImage.setImageURI(currentImageUri);
                        Toast.makeText(getContext(), "Image Selected", Toast.LENGTH_SHORT).show();
                    }

                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        // Configure button with Manual mode and Scan mode
        MaterialButton Manual_mode_select = view.findViewById(R.id.manual_mode);
        MaterialButton Scan_mode_select = view.findViewById(R.id.scan_mode);
        FrameLayout Expense_container = view.findViewById(R.id.expense_container);
        LayoutInflater Expense_inflater = LayoutInflater.from(getContext());

        // Manual Mode
        Manual_mode_select.setOnClickListener(v -> {
            Expense_container.removeAllViews(); // Clear views
            View ManualModeView = Expense_inflater.inflate(R.layout.manual_add, Expense_container, false);
            Expense_container.addView(ManualModeView);
            // Spinner dropdown list
            Spinner category_selector = ManualModeView.findViewById(R.id.entered_category);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.expense_categories, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            category_selector.setAdapter(adapter);
            // Date picker
            EditText editTextDate = ManualModeView.findViewById(R.id.entered_date);

            editTextDate.setOnClickListener(date -> {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        requireContext(),
                        (view1, selectedYear, selectedMonth, selectedDay) -> {
                            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            editTextDate.setText(selectedDate);
                        },
                        year, month, day
                );
                datePickerDialog.show();
            });
            // Button for addExpenses function
            Button AddExpensesBtn = ManualModeView.findViewById(R.id.addExpensesBtn);
            AddExpensesBtn.setOnClickListener(this::addExpenses);
        });
        // Scan mode
        Scan_mode_select.setOnClickListener(v -> {
            Expense_container.removeAllViews(); // Clear views
            View ScanModeView = Expense_inflater.inflate(R.layout.scan_mode, Expense_container, false);
            Expense_container.addView(ScanModeView);

            // Open camera button
            MaterialButton cameraButton = ScanModeView.findViewById(R.id.cameraButton);
            cameraButton.setOnClickListener(v1 -> openCamera());
            // Open gallery button
            MaterialButton galleryButton = ScanModeView.findViewById(R.id.galleryButton);
            galleryButton.setOnClickListener(v2 -> openGallery());
            //scan confirm button
            MaterialButton okButton = ScanModeView.findViewById(R.id.scanButton);
            okButton.setOnClickListener(view1 -> {
                if (currentImageUri != null) {
                    recognizeTextFromUri(currentImageUri);
                } else {
                    Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
                }
            Button saveButton = requireView().findViewById(R.id.saveBtn);
            saveButton.setOnClickListener(v3 -> sendEditedText());
            });
        });
        // default scan mode
        Scan_mode_select.performClick();
        return view;
    }

    public void addExpenses(View view) {
        View rootview = getView();
        if (rootview == null) return;

        EditText titleText = rootview.findViewById(R.id.entered_title);
        EditText amountText = rootview.findViewById(R.id.entered_amount);
        Spinner categoryText = rootview.findViewById(R.id.entered_category);
        EditText dateText = rootview.findViewById(R.id.entered_date);

        String title = titleText.getText().toString().trim();
        String amountStr = amountText.getText().toString().trim();
        String date = dateText.getText().toString().trim();
        String category = categoryText.getSelectedItem().toString();

        if (title.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // amount input
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }
        // convert string date to long
        long fullDate;
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            fullDate = sdf.parse(date).getTime();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }
        // Add Expense
        AppDatabase db = AppDatabase.getInstance(requireContext());
        db.expenseDao().insert(new Expense(title, amount, category, fullDate));


        Toast.makeText(requireContext(), "Expense added!", Toast.LENGTH_SHORT).show();
        titleText.setText(null);
        amountText.setText(null);
    }

    public void openCamera() {
        File imageFile = new File(requireContext().getCacheDir(), "captured_image.jpg");
        currentImageUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".provider",
                imageFile
        );

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
        cameraLauncher.launch(cameraIntent);
    }

    public void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
    }

    private void recognizeTextFromUri(Uri imageUri) {
        try {
            InputImage image = InputImage.fromFilePath(requireContext(), imageUri);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            recognizer.process(image)
                    .addOnSuccessListener(result -> {
                        Log.d("MLKit", "Full Recognized Text: " + result.getText());

                        List<Text.Line> allLines = new ArrayList<>();

                        // Step 1: Flatten all lines from all blocks
                        for (Text.TextBlock block : result.getTextBlocks()) {
                            allLines.addAll(block.getLines());
                        }

                        // Step 2: Sort all lines by their top coordinate (top-to-bottom)
                        allLines.sort(Comparator.comparing(line -> line.getBoundingBox().top));

                        // Step 3: Reconstruct line text left-to-right
                        List<String> cleanedLines = new ArrayList<>();
                        for (Text.Line line : allLines) {
                            List<Text.Element> elements = new ArrayList<>(line.getElements());

                            // Sort elements from left to right
                            elements.sort(Comparator.comparing(el -> el.getBoundingBox().left));

                            StringBuilder lineBuilder = new StringBuilder();
                            for (Text.Element element : elements) {
                                lineBuilder.append(element.getText()).append(" ");
                            }

                            String cleanedLine = lineBuilder.toString().trim();
                            if (!cleanedLine.isEmpty()) {
                                cleanedLines.add(cleanedLine);
                                Log.d("CleanedLine", cleanedLine); // Debug
                            }
                        }

                        // Send to parser later
                        //showExtractedText(cleanedLines.toString());
                        Log.d("List", "This is the output " + cleanedLines);
                        parseExtractedLines(cleanedLines);
                        Log.d("Cleaned List","Items and Prices" + textCleaner.cleanTokenList(cleanedLines));
                        Log.d("data logger","Date and Time : " + textFinder.findDate(cleanedLines));
                        Log.d("data logger","Total : " + textFinder.findTotal(cleanedLines));


                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Text recognition failed", Toast.LENGTH_SHORT).show();
                        Log.e("MLKit", "Error: " + e.getMessage());
                    });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }


    private void showExtractedText(String parsedText) {
        View rootView = getView();
        if (rootView == null) return;
        // Hide other UI elements
        rootView.findViewById(R.id.imagePreview).setVisibility(View.GONE);
        rootView.findViewById(R.id.cameraButton).setVisibility(View.GONE);
        rootView.findViewById(R.id.galleryButton).setVisibility(View.GONE);
        rootView.findViewById(R.id.scanButton).setVisibility(View.GONE);

        ScrollView container = rootView.findViewById(R.id.scrollTextResult);
        LinearLayout resultView = rootView.findViewById(R.id.extractedTextView);
        resultView.removeAllViews();

        Button saveButton = requireView().findViewById(R.id.saveBtn);
        saveButton.setVisibility(View.VISIBLE);
        // Show extracted text
        // Parse lines from the formatted string
        //String[] lines = parsedText.split("\n\n");

        //for (String line : lines) {
            //if (line.trim().isEmpty()) continue;
        Log.d("TextBefore", "showExtractedText: "+parsedText);
            EditText editText = new EditText(getContext());
            editText.setText(parsedText);
            editText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            editText.setPadding(5, 16, 5, 16);
            editText.setTextSize(14);
            editText.setBackgroundColor(Color.BLACK); // Optional styling
            resultView.addView(editText);
        //}
        container.setVisibility(View.VISIBLE);
        resultView.setVisibility(View.VISIBLE);
        /*if (resultView != null) {
            resultView.setVisibility(View.VISIBLE);
            resultView.setText(parsedText);*/


    }
    private void sendEditedText() {
        View rootView = getView();
        if (rootView == null) return;

        LinearLayout resultView = rootView.findViewById(R.id.extractedTextView);
        if (resultView.getChildCount() == 0) return;

        EditText fullTextField = (EditText) resultView.getChildAt(0);
        String fullText = fullTextField.getText().toString();

        // Clear old views
        resultView.removeAllViews();

        String[] lines = fullText.split("\n\n");
        String titleEdit = "", categoryEdit = "", priceEdit = "", dateEdit = "";
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] items = line.split("\n");


            for (String item : items) {
                if (item.startsWith("Date")){
                    dateEdit = (item.replace("Date : ","").trim());
                }
                if (item.startsWith("Title")) {
                    titleEdit = (item.replace("Title : ", "").trim());
                } else if (item.startsWith("Category")) {
                    categoryEdit = (item.replace("Category : ", "").trim());
                } else if (item.startsWith("Price")) {
                    priceEdit = (item.replace("Price : ", "").trim().replace("Rs", "").trim());
                }
            }
            if (!titleEdit.isEmpty() && !categoryEdit.isEmpty() && !priceEdit.isEmpty() && !dateEdit.isEmpty()){
                // Parse priceEdit to double
                double amountFinal;
                try {
                    amountFinal = Double.parseDouble(priceEdit);
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Invalid amount: " + priceEdit, Toast.LENGTH_SHORT).show();
                    continue;
                }
                // Parse dateEdit to long
                long fullDate = 0;

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    fullDate = sdf.parse(dateEdit).getTime();
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Invalid date: " + dateEdit, Toast.LENGTH_SHORT).show();
                    continue;
                }
                // log data to the database
                AppDatabase db = AppDatabase.getInstance(requireContext());
                db.expenseDao().insert(new Expense(titleEdit, amountFinal, categoryEdit, fullDate));

                Log.d("Finals", "-------- Expense Item --------");
                Log.d("Finals", "DateFinal: " + dateEdit);
                Log.d("Finals", "TitleFinal: "+titleEdit);
                Log.d("Finals", "CategoryFinal: "+categoryEdit);
                Log.d("Finals", "PriceFinal: "+priceEdit);
                Log.d("FinalsConverted", "DateConverted: "+fullDate);
                Log.d("FinalsConverted", "PriceConverted: "+amountFinal);

            }
        }
        Toast.makeText(requireContext(), "Expense added.", Toast.LENGTH_SHORT).show();
    }
    private boolean isPrice(String s) {
        return s.matches("^\\d{1,6}([.,]\\d{2})?$"); // e.g., 600.00 or 600,00 or 6000
    }
    private boolean isLikelyTitle(String s) {
        return !s.matches("^[\\d.,]+$"); // not just a number
    }
    private void parseExtractedLines(List<String> rawLines) {
        Log.d("RawLines", "RawData "+rawLines);
        List<String> lines = textCleaner.cleanTokenList(rawLines);
        List<String> TitleList = new ArrayList<>();
        List<String> PriceList = new ArrayList<>();
        StringBuilder resultBuilder = new StringBuilder();
        categoryFinder categoryCheck = new categoryFinder();
        // add date
        resultBuilder.append("Date : ").append(textFinder.findDate(rawLines)).append("\n\n");

        boolean RsPatternFound = false;

        for (int i=0; i < lines.size() -2  ; i++  ){
            String title = lines.get(i);
            String price1 = lines.get(i+1);
            String price2 = lines.get(i+2);

            if (price1.startsWith("Rs") && price2.startsWith("Rs") && !(title.startsWith("Rs"))){
                String category = categoryCheck.findCategory(title);
                TitleList.add(title);
                PriceList.add(price1);
                resultBuilder.append("Title : ").append(title).append("\n")
                        .append("Category : ").append(category).append("\n")
                        .append("Price : ").append(price1).append("\n\n");
                i += 2;
                RsPatternFound = true;
            }
            //if (!price1.startsWith("Rs"))
        }
        //  Check plain numeric price pattern
        if (!RsPatternFound) {
            for (int i = 1; i < lines.size(); i++) {
                String price = lines.get(i);
                String title = lines.get(i - 1);

                if (isPrice(price) && isLikelyTitle(title)) {
                    String category = categoryCheck.findCategory(title);
                    TitleList.add(title);
                    PriceList.add(price);
                    resultBuilder.append("Title : ").append(title).append("\n")
                            .append("Category : ").append(category).append("\n")
                            .append("Price : ").append(price).append("\n\n");
                }
            }
        }
        //add Total
        resultBuilder.append("Total : ").append(textFinder.findTotal(rawLines));

        showExtractedText(resultBuilder.toString());
    }

    // Clear unwanted words

}
