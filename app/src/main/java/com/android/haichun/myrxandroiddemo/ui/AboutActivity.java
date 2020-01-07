package com.android.haichun.myrxandroiddemo.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.android.haichun.myrxandroiddemo.R;
import com.android.haichun.myrxandroiddemo.databinding.ActivityAboutBinding;
import com.jakewharton.rxbinding3.appcompat.RxToolbar;
import com.jakewharton.rxbinding3.view.RxView;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.databinding.DataBindingUtil;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class AboutActivity extends RxAppCompatActivity {

    ActivityAboutBinding mBinding;
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        setSupportActionBar(mBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        RxToolbar.navigationClicks(mBinding.toolbar)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(this.<Unit>bindToLifecycle())
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        onBackPressed();
                    }
                });
        RxView.clicks(mBinding.cardView)
                .throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Unit>bindToLifecycle())
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://github.com/Assassinss/pretty-girl"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        RxView.clicks(mBinding.cardGankio)
                .throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Unit>bindToLifecycle())
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://gank.io"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
    }


}
