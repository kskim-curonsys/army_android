package com.curonsys.army_android.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CardsDecoration extends RecyclerView.ItemDecoration {
    private int mHeaderGap = 0, mRowGap = 0;

    public CardsDecoration(int headerGap, int rowGap) {
        this.mHeaderGap = headerGap;
        this.mRowGap = rowGap;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);

        outRect.top = mHeaderGap;
        outRect.bottom = mRowGap;
    }
}

