package com.example.shraddha.igram;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class complaintAdapter extends RecyclerView.Adapter<complaintAdapter.ViewHolder> {
    private List<newComplaint> list;
    private Context context;

    public complaintAdapter(List<newComplaint> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public complaintAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_list_layout, parent, false);
        return new ViewHolder(v, context, list);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final newComplaint listItem = list.get(position);
        holder.dateOfComplaint.setText(listItem.getDateOfComplaint());
        holder.typeTextView.setText(listItem.getHandyman());
        holder.statusTextView.setText(listItem.getStatus());

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("complaint").child(listItem.getHandyman()).child(listItem.getComplaintId());

        holder.optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.optionMenu);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                databaseReference.removeValue();
                                list.clear();
                                Toast.makeText(context, "Complaint Deleted Sucessfully", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.update:
                                Intent intent = new Intent(context, updateComplaint.class);
                                intent.putExtra("hostel", listItem.getHostel());
                                intent.putExtra("room", listItem.getRoom());
                                intent.putExtra("dateOfcomplaint", listItem.getDateOfComplaint());
                                intent.putExtra("datePref1", listItem.getDatepref1());
                                intent.putExtra("timePref1", listItem.getTimepref1());
                                intent.putExtra("describe", listItem.getDescription());
                                intent.putExtra("complaintId", listItem.getComplaintId());
                                intent.putExtra("handyman", listItem.getHandyman());
                                intent.putExtra("studentId", listItem.getStudentId());
                                intent.putExtra("status", listItem.getStatus());

                                context.startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateOfComplaint, typeTextView, statusTextView, optionMenu;

        List<newComplaint> ncomplaint;
        Context ctx;

        public ViewHolder(View itemView, Context ctx, List<newComplaint> c) {
            super(itemView);
            this.ncomplaint = c;
            this.ctx = ctx;
            dateOfComplaint = (TextView) itemView.findViewById(R.id.dateOfComplaint);
            typeTextView = (TextView) itemView.findViewById(R.id.typeTextView);
            statusTextView = (TextView) itemView.findViewById(R.id.status);
            optionMenu = (TextView) itemView.findViewById(R.id.optionMenu);
        }
    }
}
