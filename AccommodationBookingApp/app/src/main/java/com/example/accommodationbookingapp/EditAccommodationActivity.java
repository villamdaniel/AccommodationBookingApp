package com.example.accommodationbookingapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditAccommodationActivity extends AppCompatActivity {

    private EditText editTextName, editTextLocation, editTextPrice;
    private Button buttonSaveChanges;
    private String accommodationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_accommodation);

        editTextName = findViewById(R.id.editTextName);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextPrice = findViewById(R.id.editTextPrice);
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);

        // Kapott szállás ID
        accommodationId = getIntent().getStringExtra("accommodationId");

        // Eredeti adatok lekérése és megjelenítése
        FirebaseFirestore.getInstance()
                .collection("accommodations")
                .document(accommodationId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Accommodation acc = documentSnapshot.toObject(Accommodation.class);
                    if (acc != null) {
                        editTextName.setText(acc.getName());
                        editTextLocation.setText(acc.getLocation());
                        editTextPrice.setText(String.valueOf(acc.getPricePerNight()));
                    }
                });

        buttonSaveChanges.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String location = editTextLocation.getText().toString();
            double price = Double.parseDouble(editTextPrice.getText().toString());

            FirebaseFirestore.getInstance()
                    .collection("accommodations")
                    .document(accommodationId)
                    .update("name", name,
                            "location", location,
                            "pricePerNight", price)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Sikeres mentés!", Toast.LENGTH_SHORT).show();
                        finish(); // visszalép
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }
}
