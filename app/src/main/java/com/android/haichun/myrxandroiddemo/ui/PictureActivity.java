package com.android.haichun.myrxandroiddemo.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.android.haichun.myrxandroiddemo.R;
import com.android.haichun.myrxandroiddemo.databinding.ActivityPictureBinding;
import com.android.haichun.myrxandroiddemo.widget.PullBackLayout;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PictureActivity extends AppCompatActivity implements PullBackLayout.PullCallBack {
    ActivityPictureBinding mBinding;
    private ColorDrawable background;
    private PhotoViewAttacher mViewAttacher;
    private boolean systemUiIsShow = true;
    private static final int FLAG_HIDE_SYSTEM_UI = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE;
    private static final int FLAG_SHOW_SYSTEM_UI = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;
    private static final String TAG = "PICTURE_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_picture);

        String mGirlUtl = Objects.requireNonNull(getIntent().getExtras()).getString("url","");
        Picasso.get().load(mGirlUtl).into(mBinding.ivPhoto);
        //ViewCompat.setHasTransientState(mBinding.ivPhoto,false);
        ViewCompat.setTransitionName(mBinding.ivPhoto,"girl");
        background = new ColorDrawable(Color.BLACK);
        //将pictureActivity的背景设置成黑色
        mBinding.pullBackLayout.setBackground(background);
        mViewAttacher = new PhotoViewAttacher(mBinding.ivPhoto);
        mBinding.pullBackLayout.setPullCallBack(this);
        mViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if(systemUiIsShow){
                    hideSystemUI();
                    systemUiIsShow = false;
                }else{
                    showSystemUI();
                    systemUiIsShow = true;
                }
            }
        });
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(FLAG_SHOW_SYSTEM_UI);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(FLAG_HIDE_SYSTEM_UI);
    }

    @Override
    public void onPullStart() {
        showSystemUI();
        //background.setAlpha(100);
    }

    @Override
    public void onPullCompleted() {
        showSystemUI();
        onBackPressed();
    }

    @Override
    public void onPull(float progress) {
        showSystemUI();
        background.setAlpha((int)(0xff * (1f - progress)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mViewAttacher.cleanup();
    }
}
