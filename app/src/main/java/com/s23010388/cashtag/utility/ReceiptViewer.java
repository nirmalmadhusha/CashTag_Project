package com.s23010388.cashtag.utility;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.s23010388.cashtag.R;
import com.s23010388.cashtag.models.Receipt;
import com.s23010388.cashtag.storage.AppDatabase;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptViewer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptViewer extends Fragment {

    private static final String ARG_SHOP_ID = "shop_id";
    private int shopId;
    private LinearLayout containerLayout;

    public ReceiptViewer() {
        // Required empty public constructor
    }

    public static ReceiptViewer newInstance(int shopId) {
        ReceiptViewer fragment = new ReceiptViewer();
        Bundle args = new Bundle();
        args.putInt(ARG_SHOP_ID, shopId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shopId = getArguments().getInt(ARG_SHOP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipt_viewer, container, false);

        // Now load receipts by shopId and display
        containerLayout = view.findViewById(R.id.receiptContainer);
        List<Receipt> receipts = AppDatabase.getInstance(requireContext())
                .receiptDao().getReceiptsByShop(shopId);

        if (receipts.isEmpty()) {
            Toast.makeText(requireContext(), "No receipts available for this shop.", Toast.LENGTH_SHORT).show();
        } else {
            for (Receipt receipt : receipts) {
                ImageView imageView = new ImageView(requireContext());
                // check file is available
                Uri uri = Uri.parse(receipt.imagePath);
                try {
                    requireContext().getContentResolver().openInputStream(uri).close();
                    imageView.setImageURI(Uri.parse(receipt.imagePath));
                } catch (Exception e){
                    imageView.setImageResource(R.drawable.ic_image_missing);
                    Toast.makeText(requireContext(), "Receipt image missing!", Toast.LENGTH_SHORT).show();
                }
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(0, 16, 0, 16);

                // delete option
                imageView.setOnLongClickListener(v -> {
                    new MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                            .setTitle("Delete Receipt")
                            .setMessage("Are you sure you want to delete this Receipt ?")
                            .setPositiveButton("Delete", (dialog, which) -> deleteReceipt(receipt,imageView))
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .show();

                    return true;
                });
                containerLayout.addView(imageView);
            }
        }

        return view;
    }

    private void deleteReceipt(Receipt receipt, View imageView){
        AppDatabase db = AppDatabase.getInstance(requireContext());
        db.receiptDao().delete(receipt);

        // Remove from UI
        containerLayout.removeView(imageView);

        Toast.makeText(requireContext(), "Receipt deleted", Toast.LENGTH_SHORT).show();
    }
}

