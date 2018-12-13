package com.curonsys.army_android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.curonsys.army_android.R;

import java.util.ArrayList;

public class MyContentDetailAdapter  extends RecyclerView.Adapter<MyContentDetailAdapter.ViewHolder> {
    private static final String TAG = MyContentDetailAdapter.class.getSimpleName();

    private ArrayList<String> mList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mDetailView;

        public ViewHolder(View itemView) {
            super(itemView);

            mDetailView = (TextView) itemView.findViewById(R.id.my_content_detail);
        }
    }

    public MyContentDetailAdapter(ArrayList<String> list) {
        mList = list;
    }

    @Override
    public MyContentDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.my_content_detail_layout, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TextView textView = viewHolder.mDetailView;
        textView.setText(mList.get(position));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: item " + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
