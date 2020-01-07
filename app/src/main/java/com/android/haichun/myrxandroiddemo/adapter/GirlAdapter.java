package com.android.haichun.myrxandroiddemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.haichun.myrxandroiddemo.R;
import com.android.haichun.myrxandroiddemo.databinding.GirlItemBinding;
import com.android.haichun.myrxandroiddemo.model.Image;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class GirlAdapter extends RecyclerView.Adapter<GirlAdapter.GirlViewHolder> implements Consumer<List<Image>>{

    private Context mContext;
    private List<Image> mImageList;
    private Glide mGlide;
    private OnTouchListener mTouchListener;

    public GirlAdapter(Context context,List<Image> images){
        this.mContext = context;
        this.mImageList = images;
        mGlide = Glide.get(context);
        mGlide.setMemoryCategory(MemoryCategory.HIGH);
    }

    @NonNull
    @Override
    public GirlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GirlViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.girl_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull GirlViewHolder holder, int position) {
        Image image = mImageList.get(position);
        holder.image = image;
        holder.mBinding.setImage(image);
        holder.mBinding.executePendingBindings();

        Glide.with(mContext).load(holder.image.url).into(holder.mBinding.image);

    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    @Override
    public void accept(List<Image> images) throws Exception {
        notifyDataSetChanged();
    }

    class GirlViewHolder extends RecyclerView.ViewHolder{
        GirlItemBinding mBinding;
        Image image;
        @SuppressLint("CheckResult")
        public GirlViewHolder(View itemView){
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            RxView.clicks(itemView)
                    .throttleFirst(1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Exception {
                            if(mTouchListener != null){
                                mTouchListener.onImageClick(mBinding.image,image);
                            }
                        }
                    });
        }
    }
    public void setTouchListener(OnTouchListener touchListener){
        this.mTouchListener = touchListener;
    }
    public interface OnTouchListener{
        void onImageClick(View v,Image image);
    }
}
