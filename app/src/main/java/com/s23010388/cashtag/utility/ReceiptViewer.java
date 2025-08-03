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

import com.s23010388.cashtag.R;
import com.s23010388.cashtag.models.Receipt;
import com.s23010388.cashtag.storage.AppDatabase;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptViewer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptViewer extends Fragment {

    private static final String ARG_SHOP_ID = "shop_id";
    private int shopId;

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
        LinearLayout containerLayout = view.findViewById(R.id.receiptContainer);
        List<Receipt> receipts = AppDatabase.getInstance(requireContext())
                .receiptDao().getReceiptsByShop(shopId);

        if (receipts.isEmpty()) {
            Toast.makeText(requireContext(), "No receipts available for this shop.", Toast.LENGTH_SHORT).show();
        } else {
            for (Receipt receipt : receipts) {
                ImageView imageView = new ImageView(requireContext());
                imageView.setImageURI(Uri.parse(receipt.imagePath));
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(0, 16, 0, 16);
                containerLayout.addView(imageView);
            }
        }

        return view;
    }
}

