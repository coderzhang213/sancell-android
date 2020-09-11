package cn.sancell.xingqiu.im.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sancell.xingqiu.R;
import cn.sancell.xingqiu.SCApp;
import cn.sancell.xingqiu.base.BaseMVPActivity;
import cn.sancell.xingqiu.base.mvp.BaseView;
import cn.sancell.xingqiu.constant.Constants;
import cn.sancell.xingqiu.constant.IntentKey;
import cn.sancell.xingqiu.homeclassify.ProductInfoActivity;
import cn.sancell.xingqiu.homepage.adapter.HomepageLikeListAdapter;
import cn.sancell.xingqiu.homepage.bean.HomePageLikeListDataBean;
import cn.sancell.xingqiu.homepage.bean.LikeBean;
import cn.sancell.xingqiu.homepage.contract.SearchProductListContract;
import cn.sancell.xingqiu.util.PreferencesUtils;
import cn.sancell.xingqiu.util.ScreenUtils;
import cn.sancell.xingqiu.util.StatusBarUtil;
import cn.sancell.xingqiu.widget.MaxRecyclerView;
import cn.sancell.xingqiu.widget.SancellToobarClassicsHeader;
import cn.sancell.xingqiu.widget.SpaceItemDecoration;

/**
 * 搜索到的商品列表界面
 */
public class SearchGoodsMsgActivity extends BaseMVPActivity<SearchProductListContract.SearchProductListPresenter>
        implements SearchProductListContract.SearchProductListView, View.OnClickListener {
    @BindView(R.id.view_status)
    View view_status;
    @BindView(R.id.rl_top)
    RelativeLayout rl_top;
    @BindView(R.id.btn_back)
    ImageView btn_back;
    @BindView(R.id.ed_keyword)
    EditText ed_keyword;
    @BindView(R.id.tv_search)
    TextView tv_search;
    @BindView(R.id.iv_delete_keyword)
    ImageView iv_delete_keyword;

    @BindView(R.id.network_error)
    View mNetworkErrorLayout;
    @BindView(R.id.tv_refresh)
    TextView tv_refresh;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.empty)
    View mEmptyLayout;
    @BindView(R.id.rcv_product_list)
    MaxRecyclerView rcv_product_list;
    GoodMsgAdapter goodMsgAdapter;
    private List<LikeBean> data_productList = new ArrayList<>();

    @BindView(R.id.tv_like)
    TextView tv_like;
    @BindView(R.id.rcv_like_list)
    MaxRecyclerView rcv_like_list;
    GoodMsgAdapter goodsMsgLikeAdapter;
    private List<LikeBean> data_likeList = new ArrayList<>();
    private String moudleId = "1";
    private String keyWord;


    @Override
    protected SearchProductListContract.SearchProductListPresenter createPresenter() {
        return new SearchProductListContract.SearchProductListPresenter();
    }

    public static void start(Activity context, int requestCode, String keyWord, String moudleId) {
        Intent intent = new Intent(context, SearchGoodsMsgActivity.class);
        intent.putExtra(Constants.Key.KEY_1, keyWord);
        intent.putExtra(Constants.Key.KEY_2, moudleId);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_search_product_list;
    }

    @Override
    protected void initial() {
        ButterKnife.bind(this);
        int statusHeight = StatusBarUtil.getStatusBarHeight(this);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view_status.getLayoutParams();
            lp.height = statusHeight;
            view_status.setLayoutParams(lp);
        }
        keyWord = getIntent().getStringExtra(Constants.Key.KEY_1);
        moudleId = getIntent().getStringExtra(Constants.Key.KEY_2);
        ed_keyword.setText(keyWord);
        iv_delete_keyword.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        iv_delete_keyword.setOnClickListener(this);
        ed_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String sourceStr = editable.toString();
                if (sourceStr.length() > 0) {
                    keyWord = sourceStr;
                    iv_delete_keyword.setVisibility(View.VISIBLE);
                } else {
                    keyWord = PreferencesUtils.getString(Constants.Key.KEY_searchKeyWord, "");
                    iv_delete_keyword.setVisibility(View.GONE);
                }
            }
        });

        ed_keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //点击搜索的时候隐藏软键盘
                    ScreenUtils.hideKeyboard(ed_keyword);
                    // 在这里写搜索的操作,一般都是网络请求数据
                    mPresenter.AddHistory(keyWord, SearchGoodsMsgActivity.this, moudleId);
                    return true;
                }

                return false;
            }
        });

        refreshLayout.setRefreshHeader(new SancellToobarClassicsHeader(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (goodMsgAdapter != null) {
                    goodMsgAdapter.resetCurrentPage();
                }
                mPresenter.GetSearchProductList(1, keyWord, SearchGoodsMsgActivity.this);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (data_productList != null && data_productList.size() > 0) {
                    if (goodMsgAdapter != null) {
                        mPresenter.GetSearchProductList(goodMsgAdapter.getNextPage(), keyWord, SearchGoodsMsgActivity.this);
                    } else {
                        mPresenter.GetSearchProductList(1, keyWord, SearchGoodsMsgActivity.this);
                    }
                } else {
                    if (goodsMsgLikeAdapter != null) {
                        mPresenter.GetLikeList(goodsMsgLikeAdapter.getNextPage(), SearchGoodsMsgActivity.this);
                    } else {
                        mPresenter.GetLikeList(1, SearchGoodsMsgActivity.this);
                    }
                }
            }
        });

        rcv_product_list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rcv_product_list.addItemDecoration(new SpaceItemDecoration(2, ScreenUtils.dip2px(this, 4), ScreenUtils.dip2px(this, 12),
                ScreenUtils.dip2px(this, 8), ScreenUtils.dip2px(this, 12)));
        rcv_product_list.setHasFixedSize(true);
        rcv_product_list.setNestedScrollingEnabled(false);
        goodMsgAdapter = new GoodMsgAdapter(R.layout.item_homepage_like_list, data_productList);
        goodMsgAdapter.openLoadAnimation();
        rcv_product_list.setAdapter(goodMsgAdapter);
        goodMsgAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.rl_item:
                    //todo 返回消息
                    backResult(data_productList.get(position));

                    break;
            }
        });


        rcv_like_list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rcv_like_list.addItemDecoration(new SpaceItemDecoration(2, ScreenUtils.dip2px(this, 4), ScreenUtils.dip2px(this, 12),
                ScreenUtils.dip2px(this, 8), ScreenUtils.dip2px(this, 12)));
        rcv_like_list.setHasFixedSize(true);
        rcv_like_list.setNestedScrollingEnabled(false);
        goodsMsgLikeAdapter = new GoodMsgAdapter(R.layout.item_homepage_like_list, data_likeList);
        goodsMsgLikeAdapter.openLoadAnimation();
        rcv_like_list.setAdapter(goodsMsgLikeAdapter);
        goodsMsgLikeAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.rl_item:
                        //返回消息
                        backResult(data_likeList.get(position));
                        break;
                }
            }
        });
        mPresenter.AddHistory(keyWord, SearchGoodsMsgActivity.this, moudleId);

    }

    @Override
    protected BaseView getView() {
        return this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_search:
                ScreenUtils.hideKeyboard(ed_keyword);
                mPresenter.AddHistory(keyWord, SearchGoodsMsgActivity.this, moudleId);
                break;
            case R.id.iv_delete_keyword:
                ed_keyword.setText("");
                break;
        }
    }

    public void backResult(LikeBean bean) {
        if (bean == null) {
            SCApp.getInstance().showSystemCenterToast("发生了异常");
            return;
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(IntentKey.KEY_CONTENT, bean.getDesc());
        bundle.putString(IntentKey.KEY_URL, bean.getCoverPicThumb());
        bundle.putString(IntentKey.KEY_GOODID, bean.getId() + "");
        bundle.putString(IntentKey.KEY_TITLE, bean.getTitle());
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void toast(String text) {
        SCApp.getInstance().showSystemCenterToast(text);
    }

    @Override
    public void netWorkError() {
        mNetworkErrorLayout.setVisibility(View.VISIBLE);
        tv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.AddHistory(keyWord, SearchGoodsMsgActivity.this, moudleId);
            }
        });
    }

    @Override
    public void addHistroySuccess() {
        if (goodMsgAdapter != null) {
            goodMsgAdapter.resetCurrentPage();
        }
        mPresenter.GetSearchProductList(1, keyWord, SearchGoodsMsgActivity.this);
    }

    @Override
    public void getSearchListSuccess(HomePageLikeListDataBean.LikeListDataBean likeListDataBean, int page) {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        if (page == 1) {
            mNetworkErrorLayout.setVisibility(View.GONE);
            data_productList.clear();
            if (likeListDataBean != null && likeListDataBean.getDataList() != null && likeListDataBean.getDataList().size() > 0) {
                mEmptyLayout.setVisibility(View.GONE);
                tv_like.setVisibility(View.GONE);
                rcv_like_list.setVisibility(View.GONE);
                data_productList.addAll(likeListDataBean.getDataList());
                goodMsgAdapter.notifyDataSetChanged();
                goodMsgAdapter.correctCurrentPage();
                if (data_productList.size() == likeListDataBean.getDataCount()) {
                    refreshLayout.setNoMoreData(true);
                }
            } else {
                mEmptyLayout.setVisibility(View.VISIBLE);
                if (goodsMsgLikeAdapter != null) {
                    goodsMsgLikeAdapter.resetCurrentPage();
                }
                tv_like.setVisibility(View.VISIBLE);
                rcv_like_list.setVisibility(View.VISIBLE);
                mPresenter.GetLikeList(1, SearchGoodsMsgActivity.this);
            }

        } else {
            if (likeListDataBean != null && likeListDataBean.getDataList() != null && likeListDataBean.getDataList().size() > 0) {
                data_productList.addAll(likeListDataBean.getDataList());
                goodMsgAdapter.notifyDataSetChanged();
                goodMsgAdapter.correctCurrentPage();
            } else {
                refreshLayout.setNoMoreData(true);
            }
            if (data_productList.size() == likeListDataBean.getDataCount()) {
                refreshLayout.setNoMoreData(true);
            }
        }

    }

    @Override
    public void getLikeListSuccess(HomePageLikeListDataBean dataLike, int page) {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        if (page == 1) {
            mNetworkErrorLayout.setVisibility(View.GONE);
            data_likeList.clear();
            data_likeList.addAll(dataLike.getLikeData().getDataList());
            goodsMsgLikeAdapter.notifyDataSetChanged();
            goodsMsgLikeAdapter.correctCurrentPage();
        } else {
            data_likeList.addAll(dataLike.getLikeData().getDataList());
            goodsMsgLikeAdapter.notifyDataSetChanged();
            goodsMsgLikeAdapter.correctCurrentPage();
        }
        if (data_likeList.size() == dataLike.getLikeData().getDataCount()) {
            refreshLayout.setNoMoreData(true);
        }
    }
}