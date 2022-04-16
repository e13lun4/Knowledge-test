package com.example.quizsystem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CategoryCCDAdapter extends RecyclerView.Adapter<CategoryCCDAdapter.ViewHolder> {

    private List<CategoryModel> categoryCCDList;

    public CategoryCCDAdapter(List<CategoryModel> categoryList) {
        this.categoryCCDList = categoryList;
    }

    @NonNull
    @Override
    public CategoryCCDAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_ccd_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryCCDAdapter.ViewHolder viewHolder, int pos) {
        String title = categoryCCDList.get(pos).getName();
        viewHolder.setData(title, pos, this);
    }

    @Override
    public int getItemCount() {
        return categoryCCDList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryCCDName;
        private ImageView deleteButton;
        private Dialog loadingDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryCCDName = itemView.findViewById(R.id.categoryCCDName);
            deleteButton = itemView.findViewById(R.id.categoryCCDDeleteButton);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }
        private void setData(String title, int pos, CategoryCCDAdapter adapter){
            categoryCCDName.setText(title);
            deleteButton.setOnClickListener(view -> {
                AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Удалить категорию")
                        .setMessage("Вы хотите удалить категорию?")
                        .setPositiveButton("Удалить", (dialogInterface, i) -> {
                            deleteCategory(pos, itemView.getContext(), adapter);
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

        @SuppressLint("NotifyDataSetChanged")
        private void deleteCategory(final int id, Context context, CategoryCCDAdapter adapter){
            loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            Map<String, Object> categoryDoc = new ArrayMap<>();
            int index = 1;

            for (int i = 0; i < categoryCCDList.size(); i++){
                if (i != id){
                    categoryDoc.put("CAT" + String.valueOf(index) + "_ID", categoryCCDList.get(i).getId());
                    categoryDoc.put("CAT" + String.valueOf(index) + "_NAME", categoryCCDList.get(i).getName());
                    index++;
                }
            }

            categoryDoc.put("COUNT", index-1);

            firestore.collection("QUIZ").document("Categories")
                    .set(categoryDoc)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Категория удалена успешно", Toast.LENGTH_SHORT).show();
                        CategoryCCDActivity.categoryCCDList.remove(id);
                        adapter.notifyDataSetChanged();
                        loadingDialog.dismiss();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    });
//            String documentID = firestore.collection("QUIZ").document().getId();
//            firestore.collection("QUIZ").document(documentID)
//                    .delete()
//                    .addOnSuccessListener(unused -> {
//                        Log.d("Удалено", "DocumentSnapshot successfully deleted!");
//                    }).addOnFailureListener(e -> {
//                        Log.w("Не удалено", "Error deleting document", e);
//                    });
        }
    }
}
