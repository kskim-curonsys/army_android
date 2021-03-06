package com.curonsys.army_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.curonsys.army_android.R;
import com.curonsys.army_android.activity.ItemDetailActivity;
import com.curonsys.army_android.activity.ItemListActivity;
import com.curonsys.army_android.fragment.ContentModelDetailFragment;
import com.curonsys.army_android.model.ContentModel;
import com.curonsys.army_android.model.SkuDetailsModel;
import com.curonsys.army_android.util.PurchaseManager;

import java.io.File;
import java.util.List;

public class StoreListRecyclerViewAdapter
        extends RecyclerView.Adapter<StoreListRecyclerViewAdapter.ViewHolder> {

    private final ItemListActivity mParentActivity;
    private final List<ContentModel> mContents;
    private List<SkuDetailsModel> mSkus;
    PurchaseManager mPurchaseManager;

    private final View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ContentModel item = (ContentModel) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra(ContentModelDetailFragment.ARG_ITEM_ID, item.getContentId());
            intent.putExtra(ContentModelDetailFragment.ARG_ITEM, item);
            context.startActivity(intent);
        }
    };

    public StoreListRecyclerViewAdapter(ItemListActivity parent, List<ContentModel> items, List<SkuDetailsModel> skus,
                                        PurchaseManager purchase) {
        mContents = items;
        mSkus = skus;
        mParentActivity = parent;
        mPurchaseManager = purchase;
    }

    public void setSkus(List<SkuDetailsModel> skus) {
        mSkus = skus;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTitle.setText(mContents.get(position).getContentName());
        holder.mDescription.setText(mContents.get(position).getDescription());
        String price = getContentPrice(mContents.get(position).getSkuId());
        holder.mPrice.setText(price);

        String thumbpath = mContents.get(position).getThumb();
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

        holder.itemView.setTag(mContents.get(position));
        holder.itemView.setOnClickListener(mOnItemClickListener);
        holder.mPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String skuid = mContents.get(position).getSkuId();
                String skutype = getSkuType(skuid);

                mPurchaseManager.purchaseItem(skuid, skutype);
            }
        });
    }

    private String getContentPrice(String skuid) {
        // default price
        String price = "₩1,000";

        if (mSkus != null) {
            for (SkuDetailsModel details : mSkus) {
                if (skuid.compareTo(details.getSkuId()) == 0) {
                    price = details.getPrice();
                }
            }
        }

        return price;
    }

    private String getSkuType(String skuid) {
        String skutype = BillingClient.SkuType.INAPP;

        if (mSkus != null) {
            for (SkuDetailsModel details : mSkus) {
                if (skuid.compareTo(details.getSkuId()) == 0) {
                    skutype = details.getBillingType();
                }
            }
        }

        return skutype;
    }

    @Override
    public int getItemCount() {
        return mContents.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mTitle;
        final TextView mPrice;
        final ImageView mThumbnail;
        final TextView mDescription;
        final Button mPurchase;

        ViewHolder(View view) {
            super(view);

            mTitle = (TextView) view.findViewById(R.id.store_item_title);
            mPrice = (TextView) view.findViewById(R.id.store_item_price);
            mThumbnail = (ImageView) view.findViewById(R.id.store_item_sku_icon);
            mDescription = (TextView) view.findViewById(R.id.store_item_description);
            mPurchase = (Button) view.findViewById(R.id.store_item_purchase_button);
        }
    }
}
