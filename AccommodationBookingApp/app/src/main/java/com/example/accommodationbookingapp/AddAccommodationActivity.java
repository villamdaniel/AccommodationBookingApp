package com.example.accommodationbookingapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAccommodationActivity extends AppCompatActivity {

    private EditText editTextName, editTextLocation, editTextPrice, editTextImageUrl;
    private CheckBox checkBoxAvailable;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accommodation);

        editTextName = findViewById(R.id.editTextName);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextImageUrl = findViewById(R.id.editTextImageUrl);
        checkBoxAvailable = findViewById(R.id.checkBoxAvailable);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v -> saveAccommodation());
    }

    private void saveAccommodation() {
        String name = editTextName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String imageUrl = editTextImageUrl.getText().toString().trim();
        boolean available = checkBoxAvailable.isChecked();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(location) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Töltsd ki az összes mezőt!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Az ár nem érvényes szám!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> newAccommodation = new HashMap<>();
        newAccommodation.put("name", name);
        newAccommodation.put("location", location);
        newAccommodation.put("pricePerNight", price);
        newAccommodation.put("available", available);
        newAccommodation.put("imageUrl", imageUrl);

        FirebaseFirestore.getInstance()
                .collection("accommodations")
                .add(newAccommodation)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Szállás sikeresen hozzáadva!", Toast.LENGTH_SHORT).show();
                    finish(); // visszatérés előző Activity-hez
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Hiba: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
