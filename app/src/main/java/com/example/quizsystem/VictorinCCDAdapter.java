package com.example.quizsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VictorinCCDAdapter extends RecyclerView.Adapter<VictorinCCDAdapter.ViewHolder> {

    private List<String> victorinIDs;

    public VictorinCCDAdapter(List<String> victorinIDs) {
        this.victorinIDs = victorinIDs;
    }

    @NonNull
    @Override
    public VictorinCCDAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_ccd_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VictorinCCDAdapter.ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return victorinIDs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView victorinName;
        private ImageView deleteVictorinButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            victorinName = itemView.findViewById(R.id.categoryCCDName);
            deleteVictorinButton = itemView.findViewById(R.id.categoryCCDDeleteButton);
        }

        private void setData(int pos){
            victorinName.setText("VICTORIN " + String.valueOf(pos+1));
        }

    }
}
