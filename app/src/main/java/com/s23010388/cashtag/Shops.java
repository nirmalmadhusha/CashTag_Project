package com.s23010388.cashtag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;

import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.location.Location;
import android.os.Looper;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.s23010388.cashtag.models.Receipt;
import com.s23010388.cashtag.models.Shop;
import com.s23010388.cashtag.storage.AppDatabase;
import com.s23010388.cashtag.storage.ShopManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Shops#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Shops extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleMap googleMapInstance;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private int currentShopIdForReceipt = -1;
    private ShopManager shopManager;
    private FusedLocationProviderClient fusedLocationClient;



    public Shops() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Shops.
     */
    // TODO: Rename and change types and number of parameters
    public static Shops newInstance(String param1, String param2) {
        Shops fragment = new Shops();
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
        View view = inflater.inflate(R.layout.fragment_shops, container, false);

        // get the current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Initialize the image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        int shopId = shopManager.getSelectedShopId();
                        if (selectedImageUri != null && shopId != -1) {
                            AppDatabase db = AppDatabase.getInstance(requireContext());
                            db.receiptDao().insert(new Receipt(shopId, selectedImageUri.toString()));
                            Toast.makeText(getContext(), "Receipt added!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        // UI setup
        Button addShopButton = view.findViewById(R.id.addShopBtn);
        addShopButton.setOnClickListener(v -> showAddShopDialog());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewShops);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AppDatabase db = AppDatabase.getInstance(getContext());
        List<Shop> initialShops = db.shopDao().getAllShops();

        shopManager = new ShopManager(getContext(), new ArrayList<>(initialShops), imagePickerLauncher);
        recyclerView.setAdapter(shopManager);


        return view;
    }

    private String selectedLocation = null;
    List<Shop> shopList = new ArrayList<>();

    private void showAddShopDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.add_shop_dialog, null);

        EditText editShopName = dialogView.findViewById(R.id.editShopName);
        MapView mapView = dialogView.findViewById(R.id.mapView);

        // Important: Call lifecycle methods manually
        mapView.onCreate(null);
        mapView.onResume(); // Needed to get the map to display immediately

        mapView.getMapAsync(googleMap -> {
            googleMapInstance = googleMap;

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);

                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                        Toast.makeText(getContext(), "Showing your location", Toast.LENGTH_SHORT).show();
                    } else {
                        // fallback to default if location is null
                        LatLng defaultLatLng = new LatLng(6.9271, 79.8612); // Colombo
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 12f));
                    }
                });
            } else {
                // Request location permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);

                // fallback to default location
                LatLng defaultLatLng = new LatLng(6.9271, 79.8612); // Colombo
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 12f));
            }

            googleMap.setOnMapClickListener(latLng -> {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected"));
                selectedLocation = latLng.latitude + ", " + latLng.longitude;
                Toast.makeText(getContext(), "Selected: " + selectedLocation, Toast.LENGTH_SHORT).show();
            });
        });

        EditText citySearchBox = dialogView.findViewById(R.id.search_box_places);

        citySearchBox.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            String cityName = citySearchBox.getText().toString().trim();
            if (!TextUtils.isEmpty(cityName)) {
                Geocoder geocoder = new Geocoder(getContext());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(cityName, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        LatLng cityLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                        googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, 24f));
                        Toast.makeText(getContext(), "Moved to " + cityName, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "City not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error finding city", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });

        AlertDialog dialog = builder.setTitle("Add New Shop")
                .setView(dialogView)
                .setPositiveButton("Add", (dialogInterface, which) -> {
                    String name = editShopName.getText().toString().trim();

                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getContext(), "Please enter a shop name", Toast.LENGTH_SHORT).show();
                    } else if (selectedLocation == null) {
                        Toast.makeText(getContext(), "Please select a location on the map", Toast.LENGTH_SHORT).show();
                    } else {
                        AppDatabase db = AppDatabase.getInstance(getContext());
                        db.shopDao().insert(new Shop(name, selectedLocation));
                        Toast.makeText(getContext(), "Shop saved!", Toast.LENGTH_SHORT).show();
                        List<Shop> updatedShops = db.shopDao().getAllShops();
                        shopManager.updateList(updatedShops);
                        Toast.makeText(getContext(), "Shop saved!", Toast.LENGTH_SHORT).show();


                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        // Set dialog size explicitly after showing
        Window window = dialog.getWindow();
        if (window != null) {
            int width = WindowManager.LayoutParams.MATCH_PARENT;
            int height = WindowManager.LayoutParams.WRAP_CONTENT;

            window.setLayout(width, height);
        }
    }

}