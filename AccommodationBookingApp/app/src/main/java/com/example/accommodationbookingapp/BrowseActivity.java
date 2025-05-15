package com.example.accommodationbookingapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BrowseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Accommodation> accommodationList;
    private AccommodationAdapter adapter;
    private String queryType = "available"; // alapértelmezett lekérdezés

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        recyclerView = findViewById(R.id.recyclerViewAccommodations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        accommodationList = new ArrayList<>();
        adapter = new AccommodationAdapter(accommodationList);
        recyclerView.setAdapter(adapter);

        // Lekérdezés típusa Intent-ből
        if (getIntent() != null && getIntent().hasExtra("queryType")) {
            queryType = getIntent().getStringExtra("queryType");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAccommodations();
    }

    private void loadAccommodations() {
        Query query;

        switch (queryType) {
            case "cheapest":
                query = FirebaseFirestore.getInstance()
                        .collection("accommodations")
                        .whereEqualTo("available", true)
                        .orderBy("pricePerNight")
                        .limit(5);
                break;

            case "expensive":
                query = FirebaseFirestore.getInstance()
                        .collection("accommodations")
                        .whereEqualTo("available", true);
                break;

            case "available":
            default:
                query = FirebaseFirestore.getInstance()
                        .collection("accommodations")
                        .whereEqualTo("available", true);
                break;
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    accommodationList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Accommodation acc = doc.toObject(Accommodation.class);
                        acc.setId(doc.getId()); // ⬅️ Dokumentum ID beállítása

                        if (queryType.equals("expensive")) {
                            if (acc.getPricePerNight() > 20000) {
                                accommodationList.add(acc);
                            }
                        } else {
                            accommodationList.add(acc);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(BrowseActivity.this, "Hiba a Firestore lekérdezésnél: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
