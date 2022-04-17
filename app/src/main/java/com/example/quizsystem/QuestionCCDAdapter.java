package com.example.quizsystem;

import static com.example.quizsystem.CategoriesCCDActivity.categoriesCCDList;
import static com.example.quizsystem.CategoriesCCDActivity.selectedCategoryIndex;
import static com.example.quizsystem.QuestionsCCDActivity.questionsList;
import static com.example.quizsystem.VictorinsCCDActivity.selectedVictorinIndex;
import static com.example.quizsystem.VictorinsCCDActivity.victorinsIDs;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class QuestionCCDAdapter extends RecyclerView.Adapter<QuestionCCDAdapter.ViewHolder> {

    private List<QuestionModel> questionList;

    public QuestionCCDAdapter(List<QuestionModel> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionCCDAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_ccd_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionCCDAdapter.ViewHolder holder, int position) {
        holder.setData(position, this);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView deleteQuestionButton;
        private Dialog loadingDialog;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.categoryCCDName);
            deleteQuestionButton = itemView.findViewById(R.id.categoryCCDDeleteButton);

            loadingDialog = new Dialog(itemView.getContext());
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }

        @SuppressLint("SetTextI18n")
        private void setData(int pos, QuestionCCDAdapter adapter){
            title.setText("Вопрос " + String.valueOf(pos+1));

            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), QuestionDetailsActivity.class);
                intent.putExtra("ACTION", "EDIT");
                intent.putExtra("Q_ID", pos);
                itemView.getContext().startActivity(intent);
            });

            deleteQuestionButton.setOnClickListener(view -> {
                AlertDialog dialog = new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Удалить вопрос")
                        .setMessage("Вы точно хотите удалить вопрос?")
                        .setPositiveButton("Удалить", (dialogInterface, i) -> {
                            deleteQuestion(pos, itemView.getContext(), adapter);
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
        private void deleteQuestion(int pos, Context context, QuestionCCDAdapter adapter){
            loadingDialog.show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            firestore.collection("QUIZ").document(categoriesCCDList.get(selectedCategoryIndex).getId())
                    .collection(victorinsIDs.get(selectedVictorinIndex)).document(questionsList.get(pos).getQuestionID())
                    .delete()
                    .addOnSuccessListener(unused -> {

                        Map<String, Object> questionDocument = new ArrayMap<>();
                        int index = 1;
                        for(int i = 0; i < questionsList.size(); i++){
                            if (i != pos){
                                questionDocument.put("Q" + String.valueOf(index) + "_ID", questionsList.get(i).getQuestionID());
                                index++;
                            }
                        }

                        questionDocument.put("COUNT", String.valueOf(index - 1));

                        firestore.collection("QUIZ").document(categoriesCCDList.get(selectedCategoryIndex).getId())
                                .collection(victorinsIDs.get(selectedVictorinIndex)).document("QUESTIONS_LIST")
                                .set(questionDocument)
                                .addOnSuccessListener(unused1 -> {
                                    Toast.makeText(context, "Вопрос удален успешно", Toast.LENGTH_SHORT).show();

                                    questionsList.remove(pos);
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

        }

    }
}
