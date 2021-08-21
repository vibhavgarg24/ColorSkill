package com.example.colorskill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.ViewHolder>{

    private Context context;
    private List<Integer> colorList;
    private int[] previewPicMatrix;

    public PreviewAdapter(Context context, List<Integer> colorList, int[] previewPicMatrix) {
        this.context = context;
        this.colorList = colorList;
        this.previewPicMatrix = previewPicMatrix;
    }

    public PreviewAdapter(Context context, List<Integer> colorList) {
        this.context = context;
        this.colorList = colorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.preview_layout, parent, false);
        return new PreviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        if (position < 10) {
//            holder.previewView.setBackgroundColor(colorList.get(2));
//        } else {
//            holder.previewView.setBackgroundColor(colorList.get(1));
//        }

        holder.previewView.setBackgroundColor(colorList.get(previewPicMatrix[position]));

    }

    @Override
    public int getItemCount() {
        return 25;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View previewView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            previewView = itemView.findViewById(R.id.previewView);
        }
    }
}
