package com.hackathon.skillshare.adapter;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hackathon.skillshare.R;
import com.hackathon.skillshare.data.SkillData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillViewHolder> {

    ArrayList<SkillData> data;
    Context context;
    OnSkillSelected onSkillSelected;
    int mode;

    public SkillAdapter(ArrayList<SkillData> data, Context context, OnSkillSelected onSkillSelected, int mode){
        this.data = data;
        this.context = context;
        this.onSkillSelected = onSkillSelected;
        this.mode = mode;
    }

    @NonNull
    @Override
    public SkillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SkillViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_skill, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SkillViewHolder holder, int position) {

        if(mode == 1){
            holder.imgDelete.setVisibility(View.GONE);
        }else{
            holder.imgDelete.setVisibility(View.VISIBLE);
        }

        if(data.get(position).isSkillSelected()){
            holder.llSkill.setBackground(context.getResources().getDrawable(R.drawable.shape_skill_selected, context.getTheme()));
        }else{
            holder.llSkill.setBackground(context.getResources().getDrawable(R.drawable.shape_skill_not_selected, context.getTheme()));
        }

        Picasso.get().load(data.get(position).getSkill_image()).error(R.drawable.ic_no_thumb)
                .into(holder.imgSkill);
        holder.txtSkill.setText(data.get(position).getSkill_name());

        holder.imgSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.get(position).setSkillSelected(!data.get(position).isSkillSelected());
                onSkillSelected.onSkillSelected(data.get(position));
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSkillSelected.onSkillDelete(data.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SkillViewHolder extends RecyclerView.ViewHolder{

        TextView txtSkill;
        ImageView imgSkill;
        LinearLayout llSkill;
        ImageView imgDelete;

        public SkillViewHolder(@NonNull View itemView) {
            super(itemView);

            txtSkill = itemView.findViewById(R.id.txtRecSkill);
            imgSkill = itemView.findViewById(R.id.imgRecSkill);
            llSkill = itemView.findViewById(R.id.llSkillStatus);
            imgDelete = itemView.findViewById(R.id.imgRecSkillDelete);

        }
    }

    public interface OnSkillSelected{
        void onSkillSelected(SkillData skillData);
        void onSkillDelete(SkillData skillData);
    }
}
