package com.example.degreeapp.Collection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.R;

import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionHolder>{
    List<Item> items = new ArrayList<>();

    @NonNull
    @Override
    public CollectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_collection, parent, false);
        return new CollectionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionHolder holder, int position) {
        final Item currentItem = items.get(position);

        //TODO handle image
        holder.description.setText(currentItem.getTitle());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(final List<Item> items){
        this.items = items;
        notifyDataSetChanged();
    }

    class CollectionHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private TextView description;

        public CollectionHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.collection_image);
            description = itemView.findViewById(R.id.collection_description);
        }
    }
}
