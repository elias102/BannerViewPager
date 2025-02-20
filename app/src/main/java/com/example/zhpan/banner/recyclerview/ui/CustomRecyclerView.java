package com.example.zhpan.banner.recyclerview.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhpan.banner.R;
import com.example.zhpan.banner.recyclerview.listener.ICustomClickListener;
import com.example.zhpan.banner.recyclerview.module.ViewConfig;

import java.util.ArrayList;

/**
 * 自定义RecyclerView，主要用于添加不同类型的Head和Foot
 */
public class CustomRecyclerView extends RecyclerView {
  //保存头部的view
  private ArrayList<ViewConfig> mHeaderCouListInfo;
  //保存尾部的view
  private ArrayList<ViewConfig> mFooterCouListInfo;
  //记录head的个数
  private int headerCount;
  //记录foot的个数
  private int footerCount;
  //adapter，可能是CustomAdapter， 可能是自定义adapter
  private Adapter mAdapter;
  private Context mContext;
  private ICustomClickListener customClickListener;

  public CustomRecyclerView(@NonNull Context context) {
    this(context, null);
  }

  public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  private void init(Context context) {
    mHeaderCouListInfo = new ArrayList<>();
    mFooterCouListInfo = new ArrayList<>();
    mContext = context;
  }

  public ArrayList<ViewConfig> getmHeadCouListInfo() {
    return mHeaderCouListInfo;
  }

  public ArrayList<ViewConfig> getmFootCouListInfo() {
    return mFooterCouListInfo;
  }

  /**
   * 添加HeadView的方法
   */

  public void addHeaderView(View view) {
    addHeaderView(view, false);
  }

  public void addHeaderView(View view, boolean isCache) {
    setHeadViewConfig(view, ViewConfig.HEADVIEW, headerCount, 100000, isCache);
    headerCount = mHeaderCouListInfo.size();

    if (mAdapter != null) {
      if (!(mAdapter instanceof CustomAdapter)) {
        wrapHeadAdapter();
      }
    }
  }

  public void addFootView(View view) {
    setFootViewConfig(view, ViewConfig.FOOTVIEW, footerCount, 100000);
    footerCount = mFooterCouListInfo.size();
    if (mAdapter != null) {
      if (!(mAdapter instanceof CustomAdapter)) {
        wrapHeadAdapter();
      }
    }
  }

  /**
   * 将adapter构建为customadapter用来填充头部尾部布局
   */
  private void wrapHeadAdapter() {
    mAdapter = new CustomAdapter(mHeaderCouListInfo, mFooterCouListInfo, mAdapter, mContext, this);
  }

  @Override
  public void setAdapter(@Nullable Adapter adapter) {
    if (mHeaderCouListInfo.size() > 0 || mFooterCouListInfo.size() > 0) {
      mAdapter = new CustomAdapter(mHeaderCouListInfo, mFooterCouListInfo, adapter, mContext, this);
    } else {
      mAdapter = adapter;
    }
    /**
     * 设置头尾的两个缓存为size  变相解决复用问题
     */
    getRecycledViewPool().setMaxRecycledViews(ViewConfig.FOOTVIEW_TYPE,
        mFooterCouListInfo.size() + 1);
    getRecycledViewPool().setMaxRecycledViews(ViewConfig.HEADVIEW_TYPE,
        mHeaderCouListInfo.size() + 1);

    super.setAdapter(mAdapter);
  }

  /**
   * 配置头部view的信息
   */
  private void setHeadViewConfig(View view, String type, int count, int headCount,
      boolean isCache) {
    ViewConfig viewConfig = new ViewConfig();
    viewConfig.setTag(view.getClass() + type + count);
    viewConfig.setType(headCount);
    viewConfig.setView(R.layout.item_head_foot_parent);
    viewConfig.setCache(isCache);
    ViewGroup mHeadParent = (ViewGroup) view.getParent();
    if (mHeadParent != null) {
      mHeadParent.removeView(view);
    }
    viewConfig.setContentView(view);
    mHeaderCouListInfo.add(viewConfig);
  }

  /**
   * 配置尾部view的信息
   */
  private void setFootViewConfig(View view, String type, int count, int headCount) {
    ViewConfig viewConfig = new ViewConfig();
    viewConfig.setTag(view.getClass() + type + count);
    viewConfig.setType(headCount);
    viewConfig.setView(R.layout.item_head_foot_parent);
    ViewGroup mFootParent = (ViewGroup) view.getParent();
    if (mFootParent != null) {
      mFootParent.removeView(view);
    }
    viewConfig.setContentView(view);
    mFooterCouListInfo.add(viewConfig);
  }

  public CustomAdapter getHeadAndFootAdapter() {
    return (CustomAdapter) mAdapter;
  }

  public void setCustomClickListener(ICustomClickListener customClickListener) {
    this.customClickListener = customClickListener;
    getHeadAndFootAdapter().setCustomClickListener(customClickListener);
  }

  /**
   * 移除最后一个View， 就是加载更多的哪一个
   */
  public void removeLastFootView(int foorIndex) {
    this.mFooterCouListInfo.remove(foorIndex);
    footerCount = mFooterCouListInfo.size();
  }

  public void removeFirstHeadView() {
    this.mHeaderCouListInfo.remove(0);
    headerCount = mHeaderCouListInfo.size();
  }
}
