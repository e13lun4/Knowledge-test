package com.example.quizsystem;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressWarnings("ALL")
public class VictorinAdapter extends BaseAdapter {
    private int numOfVictorins;

    public VictorinAdapter(int numOfVictorins){
        this.numOfVictorins = numOfVictorins;
    }

    @Override
    public int getCount() {
        return numOfVictorins;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.victorin_item_layout, parent, false);
        }else{
            view = convertView;
        }

        view.setOnClickListener(view1 -> {
            Intent intent = new Intent(parent.getContext(), QuestionActivity.class);
            intent.putExtra("VICTORIN_NUMBER", position);
            parent.getContext().startActivity(intent);
        });

        ((TextView) view.findViewById(R.id.victorinName)).setText(String.valueOf(position+1));
        return view;
    }
}
