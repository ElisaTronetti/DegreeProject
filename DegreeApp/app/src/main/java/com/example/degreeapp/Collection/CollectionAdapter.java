package com.example.degreeapp.Collection;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.degreeapp.Database.Item.Item;
import com.example.degreeapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionHolder>{
    List<Item> items = new ArrayList<>();
    private OnCollectionItemClickListener onCollectionItemClickListener;

    @NonNull
    @Override
    public CollectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_collection, parent, false);
        return new CollectionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionHolder holder, int position) {
        final Item currentItem = items.get(position);

        File file = new File(currentItem.getImage_url());

        holder.description.setText(currentItem.getTitle());
        holder.timestamp.setText(currentItem.getUnlocked_time());
        Picasso.get()
                .load(file)
                .placeholder(R.drawable.baseline_lock_black_24dp)
                .error(R.drawable.baseline_image_not_supported_black_24dp)
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("PICASSO", "Image collection ok");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("PICASSO", "Image collection error");
                    }
                });
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
        private TextView timestamp;

        CollectionHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.collection_image);
            description = itemView.findViewById(R.id.collection_description);
            timestamp = itemView.findViewById(R.id.collection_timestamp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(onCollectionItemClickListener != null){
                        onCollectionItemClickListener.onCollectionItemClickListener(items.get(position));
                    }
                }
            });

        }
    }


    //used to have a reference to the item in the activity to implement the behavior when
    //an item card view is clicked
    public interface OnCollectionItemClickListener{
        void onCollectionItemClickListener(Item item);
    }

    public void setOnCollectionItemClickListener(OnCollectionItemClickListener listener){
        this.onCollectionItemClickListener = listener;
    }
}
