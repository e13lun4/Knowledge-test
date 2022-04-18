package com.example.quizsystem;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

@SuppressWarnings("ALL")
public class CategoryAdapter extends BaseAdapter {
    private List<CategoryFModel> categoryList;

    public CategoryAdapter(List<CategoryFModel> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_iteam_layout, parent, false);
        }else{
            view = convertView;
        }

        view.setOnClickListener(v -> {
            SplashActivity.selectedCatIndex = position;
            Intent intent = new Intent(parent.getContext(), VictorinsActivity.class);
            parent.getContext().startActivity(intent);
        });

        ((TextView) view.findViewById(R.id.categoryName)).setText(categoryList.get(position).getName());
        float r = 150;
        ShapeDrawable shape = new ShapeDrawable(new RoundRectShape(new float[] { r, r, r, r, r, r, r, r },null,null));
        shape.getPaint().setColor(Color.rgb(128, 0, 255));
        view.setBackground(shape);
        return view;
    }
}
