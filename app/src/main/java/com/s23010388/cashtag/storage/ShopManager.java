package com.s23010388.cashtag.storage;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.s23010388.cashtag.R;
import com.s23010388.cashtag.models.Shop;
import com.s23010388.cashtag.utility.ReceiptViewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShopManager extends RecyclerView.Adapter<ShopManager.ShopViewHolder> implements Filterable {

    private final List<Shop> shopList;
    private final List<Shop> filteredShops;
    private final Context context;
    private final ActivityResultLauncher<Intent> imagePickerLauncher;
    private int selectedShopId = -1;
    private final List<Integer> drawableList;
    private final java.util.Set<Integer> usedDrawables = new java.util.HashSet<>();
    private final java.util.Map<Integer, Integer> assignedDrawables = new java.util.HashMap<>();



    public ShopManager(Context context, List<Shop> shopList, ActivityResultLauncher<Intent> imagePickerLauncher) {
        this.context = context;
        this.shopList = new java.util.ArrayList<>(shopList);
        this.filteredShops = new java.util.ArrayList<>(shopList);
        this.imagePickerLauncher = imagePickerLauncher;

        // Initialize drawable list
        drawableList = new java.util.ArrayList<>();
        drawableList.add(R.drawable.shop_card_wave_01);
        drawableList.add(R.drawable.shop_card_wave_02);
        drawableList.add(R.drawable.shop_card_wave_03);
        drawableList.add(R.drawable.shop_card_wave_04);

    }
    public int getSelectedShopId() {
        return selectedShopId;
    }
    public void updateList(List<Shop> newList) {
        shopList.clear();
        shopList.addAll(newList);
        filteredShops.clear();
        filteredShops.addAll(newList);
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
        Shop shop = filteredShops.get(position);
        holder.shopName.setText(shop.getName());

        int selectedDrawable;

        if (assignedDrawables.containsKey(shop.getId())) {
            // Use the already assigned drawable
            selectedDrawable = assignedDrawables.get(shop.getId());
        } else {
            // Assign a new random drawable (try to avoid duplicates)
            List<Integer> availableDrawables = new ArrayList<>(drawableList);
            availableDrawables.removeAll(assignedDrawables.values());

            if (!availableDrawables.isEmpty()) {
                selectedDrawable = availableDrawables.get(new Random().nextInt(availableDrawables.size()));
            } else {
                // All colors used, allow reuse
                selectedDrawable = drawableList.get(new Random().nextInt(drawableList.size()));
            }

            assignedDrawables.put(shop.getId(), selectedDrawable);
        }


        Drawable background = ContextCompat.getDrawable(context, selectedDrawable);
        if (background != null) {
            holder.shopBackground.setBackground(background);
        }



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
        return filteredShops.size();
    }

    @Override
    public Filter getFilter() {
        return shopFilter;
    }
    private final Filter shopFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Shop> filteredList = new java.util.ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(shopList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Shop shop : shopList) {
                    if (shop.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(shop);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredShops.clear();
            filteredShops.addAll((List<Shop>) results.values);
            notifyDataSetChanged();
        }
    };
    static class ShopViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout shopBackground;
        TextView shopName;
        MaterialButton showLocationBtn, showReceiptsBtn, addReceiptBtn;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            shopBackground = itemView.findViewById(R.id.shopBackground);
            shopName = itemView.findViewById(R.id.shopName);
            showLocationBtn = itemView.findViewById(R.id.ShowLocationBtn);
            showReceiptsBtn = itemView.findViewById(R.id.ShowBillsBtn);
            addReceiptBtn = itemView.findViewById(R.id.AddReceiptsBtn);
        }
    }

}
