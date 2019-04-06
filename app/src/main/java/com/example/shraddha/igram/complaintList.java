package com.example.shraddha.igram;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class complaintList extends RecyclerView.Adapter<complaintList.ViewHolder> {

    private List<newComplaint> list;
    private Context context;

    public complaintList(List<newComplaint> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public complaintList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_list_layout, parent, true);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        newComplaint listItem = list.get(position);
        holder.typeTextView.setText(listItem.getHandyman());
        holder.dateOfComplaint.setText(listItem.getDateOfComplaint());
        holder.status.setText(listItem.getStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView typeTextView, dateOfComplaint, status;

        public ViewHolder(View itemView) {
            super(itemView);
            typeTextView = (TextView) itemView.findViewById(R.id.typeTextView);
            dateOfComplaint = (TextView) itemView.findViewById(R.id.dateOfComplaint);
            status = (TextView) itemView.findViewById(R.id.status);
        }
    }
}
