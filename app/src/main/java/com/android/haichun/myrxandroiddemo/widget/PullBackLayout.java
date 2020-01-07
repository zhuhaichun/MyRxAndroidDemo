package com.android.haichun.myrxandroiddemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

public class PullBackLayout extends FrameLayout {

    private ViewDragHelper mDragHelper;
    private PullCallBack mPullCallBack;
    private int mMinFlingVelocity;

    public void setPullCallBack(PullCallBack pullCallBack){
        this.mPullCallBack = pullCallBack;
    }

    public PullBackLayout(@NonNull Context context) {
        super(context);
    }

    public PullBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullBackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this,1f /8f,new DragCallBack());
        mMinFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    class DragCallBack extends ViewDragHelper.Callback{

        /**
         * 只有该方法返回值为true的时候才会执行Callback中的其他方法
         * @param child 捕获的View
         * @param pointerId 指针？
         */
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return 0;
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return 0;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return Math.max(0,top);
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return getHeight();
        }

        //捕获View的时候调用
        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            if(mPullCallBack != null){
                mPullCallBack.onPullStart();
            }
        }

        //停止拖拽的时候调用
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int slop = yvel > mMinFlingVelocity ? getHeight() / 6 : getHeight() /3;
            if(releasedChild.getTop() > slop){
                if(mPullCallBack != null){
                    mPullCallBack.onPullCompleted();
                }
            }else{
                mDragHelper.settleCapturedViewAt(0,0);
                invalidate();
            }
        }

        //当捕获的View位置变化是调用
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            float progress = Math.min(1f,((float) top / (float) getHeight()) * 2f);
            if(mPullCallBack != null){
                mPullCallBack.onPull(progress);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //这么做的目的是当图片缩小时应用会发生下标越界异常，
        // 接着捕捉异常返回false，子View可以继续处理事件分发，应用就不会crash了
        try {
            //使用ViewDragHelper处理事件分发
            return mDragHelper.shouldInterceptTouchEvent(ev);
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //使用ViewDragHelper处理event
        mDragHelper.processTouchEvent(event);
        return true;
    }

    public interface PullCallBack{
        void onPullStart();
        void onPullCompleted();
        void onPull(float progress);
    }

}
