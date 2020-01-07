package com.android.haichun.myrxandroiddemo.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.haichun.myrxandroiddemo.R;
import com.android.haichun.myrxandroiddemo.adapter.GirlAdapter;
import com.android.haichun.myrxandroiddemo.databinding.ActivityMainBinding;
import com.android.haichun.myrxandroiddemo.model.GirlData;
import com.android.haichun.myrxandroiddemo.model.Image;
import com.android.haichun.myrxandroiddemo.model.PrettyGirl;
import com.android.haichun.myrxandroiddemo.retrofitUtils.GirlApi;
import com.android.haichun.myrxandroiddemo.retrofitUtils.GirlRetrofit;
import com.android.haichun.myrxandroiddemo.utils.ConfigUtils;
import com.android.haichun.myrxandroiddemo.utils.NetUtils;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding3.recyclerview.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding3.recyclerview.RxRecyclerView;
import com.jakewharton.rxbinding3.swiperefreshlayout.RxSwipeRefreshLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import retrofit2.adapter.rxjava2.Result;

public class MainActivity extends RxAppCompatActivity {

    private static final String TAG = "MainActivity";
    GirlAdapter mAdapter;
    private ActivityMainBinding mBinding;
    Consumer<? super Throwable> dataError = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            throwable.printStackTrace();
            mBinding.refreshLayout.setRefreshing(false);
            Snackbar.make(mBinding.refreshLayout, throwable.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    };
    private List<Image> mImageList;
    private boolean refreshing;
    private int page = 1;
    private GirlApi mGirlApi;
    private Function<? super GirlData, ? extends ObservableSource<? extends List<Image>>> imageFetcher
            = new Function<GirlData, ObservableSource<? extends List<Image>>>() {
        @Override
        public ObservableSource<? extends List<Image>> apply(GirlData girlData) throws Exception {
            for (PrettyGirl girl : girlData.results) {
                try {
                    Bitmap bitmap = Picasso.get().load(girl.url).get();
                    Image image = new Image();
                    image.width = bitmap.getWidth();
                    image.height = bitmap.getHeight();
                    image.url = girl.url;
                    Log.w(TAG, "apply: imageUrl" + image.url);
                    mImageList.add(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return Observable.just(mImageList);
        }
    };

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mBinding.toolbar);
        mImageList = new ArrayList<>();
        mGirlApi = new GirlRetrofit().getGirlApi();
        setupRecyclerView();
        swipeRefresh();
        fetchGirlData();
        onImageClick();
    }

    @SuppressLint("CheckResult")
    private void setupRecyclerView() {
        mAdapter = new GirlAdapter(this, mImageList);
        int spanCount = 2;
        if (ConfigUtils.isOrientationPortrait(this)) {
            spanCount = 2;
        } else if (ConfigUtils.isOrientationLandscape(this)) {
            spanCount = 3;
        }
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                spanCount, StaggeredGridLayoutManager.VERTICAL);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);

        RxRecyclerView.scrollEvents(mBinding.recyclerView)
                .compose(this.<RecyclerViewScrollEvent>bindToLifecycle())
                .map(new Function<RecyclerViewScrollEvent, Boolean>() {
                    @Override
                    public Boolean apply(RecyclerViewScrollEvent recyclerViewScrollEvent) throws Exception {
                        boolean isBottom = false;
                        if (ConfigUtils.isOrientationPortrait(getApplicationContext())) {
                            isBottom = layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1]
                                    >= mImageList.size() - 4;
                        } else if (ConfigUtils.isOrientationLandscape(getApplicationContext())) {
                            isBottom = layoutManager.findLastCompletelyVisibleItemPositions(new int[3])[2]
                                    >= mImageList.size() - 4;
                        }
                        return isBottom;
                    }
                }).filter(new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean aBoolean) throws Exception {
                return aBoolean && !mBinding.refreshLayout.isRefreshing();
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (refreshing) {
                    page = 0;
                    refreshing = false;
                }
                page += 1;
                Log.w(TAG, "accept: " + page);
                mBinding.refreshLayout.setRefreshing(true);
                fetchGirlData();
            }
        });
    }

    //下拉刷新时的方法
    @SuppressLint("CheckResult")
    private void swipeRefresh() {
        RxSwipeRefreshLayout.refreshes(mBinding.refreshLayout)
                .compose(this.<Unit>bindToLifecycle())
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        page = 1;
                        refreshing = true;
                        fetchGirlData();
                    }
                });
    }

    //获取数据
    @SuppressLint("CheckResult")
    private void fetchGirlData() {
        Observable<List<Image>> results = mGirlApi.fetchPrettyGirl(page)
                .compose(this.<Result<GirlData>>bindToLifecycle())
                .filter(new Predicate<Result<GirlData>>() {
                    @Override
                    public boolean test(Result<GirlData> girlDataResult) throws Exception {
                        return !girlDataResult.isError() && girlDataResult.response().isSuccessful();
                    }
                })
                .map(new Function<Result<GirlData>, GirlData>() {
                    @Override
                    public GirlData apply(Result<GirlData> girlDataResult) {
                        return girlDataResult.response().body();
                    }
                }).flatMap(imageFetcher)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
        results.filter(new Predicate<List<Image>>() {
            @Override
            public boolean test(List<Image> o) throws Exception {
                return o.size() != 0;
            }
        }).compose(this.<List<Image>>bindToLifecycle())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        mBinding.refreshLayout.setRefreshing(false);
                    }
                }).subscribe(new Consumer<List<Image>>() {
            @Override
            public void accept(List<Image> images) throws Exception {
                Log.w(TAG, "accept: mImageSize" + images.size());
                //mImageList.addAll(images);
                mAdapter.notifyDataSetChanged();
                mBinding.refreshLayout.setRefreshing(false);
                Log.w(TAG, "accept: mImageListSize" + mImageList.size());
            }
        }, dataError);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetUtils.checkNet(this)) {
            Snackbar.make(mBinding.recyclerView, "没有连接网络", Snackbar.LENGTH_LONG).show();
        }
        //fetchGirlData();
    }

    public void onImageClick() {
        mAdapter.setTouchListener(new GirlAdapter.OnTouchListener() {
            @Override
            public void onImageClick(final View v, final Image image) {
                Picasso.get().load(image.url).fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(getApplicationContext(), PictureActivity.class);
                        intent.putExtra("url", image.url);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ActivityOptionsCompat compat = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(MainActivity.this, v, "girl");
                        ActivityCompat.startActivity(MainActivity.this, intent, compat.toBundle());
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            Intent intent = new Intent(this, AboutActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

