package com.curonsys.army_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.curonsys.army_android.R;
import com.curonsys.army_android.activity.ItemDetailActivity;
import com.curonsys.army_android.activity.ItemListActivity;
import com.curonsys.army_android.fragment.ContentModelDetailFragment;
import com.curonsys.army_android.model.ContentModel;

import java.io.File;
import java.util.List;

public class ContentsListRecyclerViewAdapter
        extends RecyclerView.Adapter<ContentsListRecyclerViewAdapter.ViewHolder> {

    private final ItemListActivity mParentActivity;
    private final List<ContentModel> mValues;
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ContentModel item = (ContentModel) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(ContentModelDetailFragment.ARG_ITEM_ID, item.getContentId());
                arguments.putSerializable(ContentModelDetailFragment.ARG_ITEM, item);
                ContentModelDetailFragment fragment = new ContentModelDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra(ContentModelDetailFragment.ARG_ITEM_ID, item.getContentId());
                intent.putExtra(ContentModelDetailFragment.ARG_ITEM, item);
                context.startActivity(intent);
            }
        }
    };

    public ContentsListRecyclerViewAdapter(ItemListActivity parent,
                                  List<ContentModel> items,
                                  boolean twoPane) {
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mName.setText(mValues.get(position).getContentName());
        holder.mDescription.setText(mValues.get(position).getDescription());

        String thumbpath = mValues.get(position).getThumb();
        String separator = thumbpath.substring(0, 7);

        if (separator.compareTo("models/") != 0) {
            File imgFile = new File(thumbpath);
            if (imgFile.exists()) {
                Bitmap thumbBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.mThumbnail.setImageBitmap(thumbBitmap);
            }
        } else {
            holder.mThumbnail.setImageResource(R.mipmap.ic_launcher_round);
        }

        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mName;
        final TextView mDescription;
        final ImageView mThumbnail;
        final Button mSelect;

        ViewHolder(View view) {
            super(view);
            mName = (TextView) view.findViewById(R.id.text_name);
            mDescription = (TextView) view.findViewById(R.id.text_description);
            mThumbnail = (ImageView) view.findViewById(R.id.image_thumbnail);
            mSelect = (Button) view.findViewById(R.id.button_select);
        }
    }
}

