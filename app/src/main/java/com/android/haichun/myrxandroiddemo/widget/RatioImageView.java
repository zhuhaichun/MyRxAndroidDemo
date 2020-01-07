package com.android.haichun.myrxandroiddemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class RatioImageView extends ImageView {

    int originalWidth;
    int originalHeight;

    public RatioImageView(Context context) {
        super(context);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public void setOriginalWidth(int originalWidth){
        this.originalWidth = originalWidth;
    }
    public void setOriginalHeight(int originalHeight){
        this.originalHeight = originalHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(originalWidth > 0 && originalHeight > 0){
            float ratio = (float) originalWidth / originalHeight;
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            if(width > 0){
                height = (int)((float) width / ratio);
            }
            setMeasuredDimension(width,height);
        }else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
