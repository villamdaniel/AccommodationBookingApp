package com.example.accommodationbookingapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AccommodationAdapter extends RecyclerView.Adapter<AccommodationAdapter.ViewHolder> {

    private final List<Accommodation> accommodationList;

    public AccommodationAdapter(List<Accommodation> accommodationList) {
        this.accommodationList = accommodationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accommodation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Accommodation acc = accommodationList.get(position);
        holder.name.setText(acc.getName());
        holder.location.setText(acc.getLocation());
        holder.price.setText("Ár: " + acc.getPricePerNight() + " Ft/éj");

        //  Animáció :DD
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(
                holder.itemView.getContext(), android.R.anim.slide_in_left));

        //  Szerkesztés gomb működés =D
        holder.buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditAccommodationActivity.class);
            intent.putExtra("accommodationId", acc.getId());
            holder.itemView.getContext().startActivity(intent);
        });
        // Törlés gomb működése :OO
        holder.buttonDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("accommodations")
                    .document(acc.getId())
                    .delete()
                    .addOnSuccessListener(unused -> {
                        accommodationList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, accommodationList.size());
                        Toast.makeText(holder.itemView.getContext(), "Szállás törölve", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(holder.itemView.getContext(), "Hiba a törlésnél: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }

    @Override
    public int getItemCount() {
        return accommodationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, price;
        Button buttonEdit, buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewName);
            location = itemView.findViewById(R.id.textViewLocation);
            price = itemView.findViewById(R.id.textViewPrice);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
