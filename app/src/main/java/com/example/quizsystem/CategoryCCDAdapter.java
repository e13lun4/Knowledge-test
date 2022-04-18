package com.example.quizsystem;

import static com.example.quizsystem.CategoriesCCDActivity.categoriesCCDList;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ALL")
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
        private Dialog editDialog;
        private EditText currentCategoryName;
        private Button editCategoryNameButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryCCDName = itemView.findViewById(R.id.categoryCCDName);
            deleteButton = itemView.findViewById(R.id.categoryCCDDeleteButton);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            editDialog = new Dialog(itemView.getContext());
            editDialog.setContentView(R.layout.edit_category_dialog);
            editDialog.setCancelable(true);
            editDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            currentCategoryName = editDialog.findViewById(R.id.editCurrentCategoryName);
            editCategoryNameButton = editDialog.findViewById(R.id.changeCategoryDialogButton);

        }
        private void setData(String title, int pos, CategoryCCDAdapter adapter){
            categoryCCDName.setText(title);

            itemView.setOnClickListener(view -> {
                CategoriesCCDActivity.selectedCategoryIndex = pos;
                Intent intent = new Intent(itemView.getContext(), VictorinsCCDActivity.class);
                intent.putExtra("CATEGORY", title);
                itemView.getContext().startActivity(intent);
            });

            itemView.setOnLongClickListener(view -> {

                currentCategoryName.setText(categoryCCDList.get(pos).getName());
                editDialog.show();

                return false;
            });

            editCategoryNameButton.setOnClickListener(view -> {
                if (currentCategoryName.getText().toString().isEmpty()){
                    currentCategoryName.setError("Введите название категории");
                    return;
                }

                updateCategoryName(currentCategoryName.getText().toString(), pos, itemView.getContext(), adapter);

            });

            deleteButton.setOnClickListener(view -> {
                AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Удалить категорию")
                        .setMessage("Вы точно хотите удалить категорию?")
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
//            nen
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
                        categoriesCCDList.remove(id);
                        adapter.notifyDataSetChanged();
                        loadingDialog.dismiss();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    });
        }

        @SuppressLint("NotifyDataSetChanged")
        private void updateCategoryName(String categoryNewName, int pos, Context context, CategoryCCDAdapter adapter){
            editDialog.dismiss();
            loadingDialog.show();

            Map<String, Object> categoryDate = new ArrayMap<>();
            categoryDate.put("NAME", categoryNewName);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("QUIZ").document(categoryCCDList.get(pos).getId())
                    .update(categoryDate)
                    .addOnSuccessListener(unused -> {

                        Map<String, Object> categoryDocument = new ArrayMap<>();
                        categoryDocument.put("CAT" + String.valueOf(pos+1) + "_NAME", categoryNewName);

                        firestore.collection("QUIZ").document("Categories")
                                .update(categoryDocument)
                                .addOnSuccessListener(unused1 -> {

                                    Toast.makeText(context, "Название категории успешно изменено", Toast.LENGTH_SHORT).show();
                                    categoriesCCDList.get(pos).setName(categoryNewName);
                                    adapter.notifyDataSetChanged();

                                    loadingDialog.dismiss();

                                }).addOnFailureListener(e -> {
                                    Toast.makeText(context, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                });

                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    });

        }

    }
}
