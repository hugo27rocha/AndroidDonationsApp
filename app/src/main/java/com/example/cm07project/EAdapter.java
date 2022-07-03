package com.example.cm07project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class EAdapter extends RecyclerView.Adapter<EAdapter.EventViewHolder> {
    private final Context ct;
    private String[] data1;
    private String[] data2;

    public EAdapter(Context ct, String[] s1, String[] s2){
        this.ct = ct;
        data1 = s1;
        data2 = s2;
    }

    @NonNull
    @Override
    public EAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View v = inflater.inflate(R.layout.event_row, parent,false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EAdapter.EventViewHolder holder, int position) {
        holder.title.setText(data1[position]);
        holder.description.setText(data2[position]);
    }

    @Override
    public int getItemCount() {
        return data1.length;
    }

    public class EventViewHolder extends RecyclerView.ViewHolder{

        TextView description, title;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.textView);
            title = itemView.findViewById(R.id.textView2);
        }
    }
}
