package com.example.quizsystem;

import static com.example.quizsystem.CategoriesCCDActivity.categoriesCCDList;
import static com.example.quizsystem.CategoriesCCDActivity.selectedCategoryIndex;
import static com.example.quizsystem.VictorinsCCDActivity.selectedVictorinIndex;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;

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
        String victorinId = victorinIDs.get(position);
        holder.setData(position, victorinId, this);
    }

    @Override
    public int getItemCount() {
        return victorinIDs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView victorinName;
        private ImageView deleteVictorinButton;
        private Dialog loadingDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            victorinName = itemView.findViewById(R.id.categoryCCDName);
            deleteVictorinButton = itemView.findViewById(R.id.categoryCCDDeleteButton);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }

        @SuppressLint("SetTextI18n")
        private void setData(int pos, final String victorinId, VictorinCCDAdapter adapter){
            victorinName.setText("Викторина " + String.valueOf(pos+1));

            itemView.setOnClickListener(view -> {
                selectedVictorinIndex = pos;
                Intent intent = new Intent(itemView.getContext(), QuestionsCCDActivity.class);
                intent.putExtra("VICTORIN", "Викторина " + String.valueOf(pos+1));
                itemView.getContext().startActivity(intent);
            });

            deleteVictorinButton.setOnClickListener(view -> {
                AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Удалить викторину")
                        .setMessage("Вы точно хотите удалить викторину?")
                        .setPositiveButton("Удалить", (dialogInterface, i) -> {
                            deleteVictorin(pos, victorinId, itemView.getContext(), adapter);
                        })
                        .setNegativeButton("Отмена", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                dialog.getButton(dialog.BUTTON_POSITIVE).setBackgroundColor(Color.rgb(139, 0, 255));
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                dialog.getButton(dialog.BUTTON_NEGATIVE).setBackgroundColor(Color.rgb(139, 0, 255));
                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 50, 0);
                dialog.getButton(dialog.BUTTON_NEGATIVE).setLayoutParams(params);


            });

        }

        private void deleteVictorin(int pos, String victorinId, Context context, VictorinCCDAdapter adapter){
            loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("QUIZ").document(categoriesCCDList.get(selectedCategoryIndex).getId())
                    .collection(victorinId).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        WriteBatch batch = firestore.batch();
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                            batch.delete(document.getReference());
                        }

                        batch.commit().addOnSuccessListener(unused -> {
                            Map<String, Object> categoryDocument = new ArrayMap<>();
                            int index = 1;
                            for(int i = 0; i < victorinIDs.size(); i ++){
                                if (i != pos){
                                    categoryDocument.put("VICTORIN" + String.valueOf(index) + "_ID", victorinIDs.get(i));
                                    index++;
                                }
                            }

                            categoryDocument.put("VICTORINS", index-1);

                            firestore.collection("QUIZ").document(categoriesCCDList.get(selectedCategoryIndex).getId())
                                    .update(categoryDocument)
                                    .addOnSuccessListener(unused1 -> {
                                        Toast.makeText(context, "Викторина удалена успешно", Toast.LENGTH_SHORT).show();

                                        VictorinsCCDActivity.victorinsIDs.remove(pos);
                                        categoriesCCDList.get(selectedCategoryIndex).setNumberOfVictorins(String.valueOf(VictorinsCCDActivity.victorinsIDs.size()));
                                        adapter.notifyDataSetChanged();

                                        loadingDialog.dismiss();

                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                    });

                        }).addOnFailureListener(e -> {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        });

                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    });
        }

    }
}
