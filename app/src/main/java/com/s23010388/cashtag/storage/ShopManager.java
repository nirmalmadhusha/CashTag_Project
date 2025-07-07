package com.s23010388.cashtag.storage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.s23010388.cashtag.R;
import com.s23010388.cashtag.models.Shop;
import com.s23010388.cashtag.utility.ReceiptViewer;

import java.util.List;
import java.util.Random;

public class ShopManager extends RecyclerView.Adapter<ShopManager.ShopViewHolder> {

    private final List<Shop> shopList;
    private final Context context;
    private final ActivityResultLauncher<Intent> imagePickerLauncher;
    private int selectedShopId = -1;
    private final List<Integer> colorList;
    private final java.util.Set<Integer> usedColors = new java.util.HashSet<>();


    public ShopManager(Context context, List<Shop> shopList, ActivityResultLauncher<Intent> imagePickerLauncher) {
        this.context = context;
        this.shopList = shopList;
        this.imagePickerLauncher = imagePickerLauncher;

        // Initialize color list
        colorList = new java.util.ArrayList<>();
        colorList.add(ContextCompat.getColor(context, R.color.shopColor1));
        colorList.add(ContextCompat.getColor(context, R.color.shopColor2));
        colorList.add(ContextCompat.getColor(context, R.color.shopColor3));
        colorList.add(ContextCompat.getColor(context, R.color.shopColor4));
    }
    public int getSelectedShopId() {
        return selectedShopId;
    }
    public void updateList(List<Shop> newList) {
        shopList.clear();
        shopList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shop_details, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        Shop shop = shopList.get(position);
        holder.shopName.setText(shop.getName());

        // color list
        // Pick a random color that hasn't been used yet
        List<Integer> availableColors = new java.util.ArrayList<>(colorList);
        availableColors.removeAll(usedColors);

        int selectedColor;

        if (!availableColors.isEmpty()) {
            selectedColor = availableColors.get(new Random().nextInt(availableColors.size()));
            usedColors.add(selectedColor);
        } else {
            // All colors used, reset and start again
            usedColors.clear();
            selectedColor = colorList.get(new Random().nextInt(colorList.size()));
            usedColors.add(selectedColor);
        }

        ((androidx.cardview.widget.CardView) holder.itemView).setCardBackgroundColor(selectedColor);



        holder.showLocationBtn.setOnClickListener(v -> {
            String[] latLng = shop.getLocation().split(",");
            if (latLng.length == 2) {
                String geoUri = "geo:" + latLng[0] + "," + latLng[1] + "?q=" + latLng[0] + "," + latLng[1] + "(" + shop.getName() + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                intent.setPackage("com.google.android.apps.maps");
                context.startActivity(intent);
            }
        });
        holder.addReceiptBtn.setOnClickListener(v -> {
            selectedShopId = shop.getId();
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            imagePickerLauncher.launch(Intent.createChooser(intent, "Select Receipt Image"));
        });
        holder.showReceiptsBtn.setOnClickListener(v -> {
            Fragment fragment = ReceiptViewer.newInstance(shop.getId());

            ((FragmentActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)  // Replace with your actual fragment container ID
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    static class ShopViewHolder extends RecyclerView.ViewHolder {
        TextView shopName;
        MaterialButton showLocationBtn, showReceiptsBtn, addReceiptBtn;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            shopName = itemView.findViewById(R.id.shopName);
            showLocationBtn = itemView.findViewById(R.id.ShowLocationBtn);
            showReceiptsBtn = itemView.findViewById(R.id.ShowBillsBtn);
            addReceiptBtn = itemView.findViewById(R.id.AddReceiptsBtn);
        }
    }
}
