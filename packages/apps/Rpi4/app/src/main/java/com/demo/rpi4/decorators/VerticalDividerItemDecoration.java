package com.demo.rpi4.decorators;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalDividerItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSpace;
    private boolean mIsVertical = false;

    public VerticalDividerItemDecoration(int space, boolean isVertical) {
        this.mSpace = space;
        this.mIsVertical = isVertical;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        // skip first item in the list
        if (parent.getChildAdapterPosition(view) != 0) {
            if (mIsVertical) {
                outRect.set(mSpace, 0, 0, 0);
            } else {
                outRect.set(0, mSpace, 0, 0);
            }
        }
    }
}
