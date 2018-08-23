package com.arcblock.btcaccounttool.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.arcblock.btcaccounttool.BtcAccountToolApp;
import com.arcblock.btcaccounttool.R;
import com.arcblock.btcaccounttool.adapter.RichestAccountListAdapter;
import com.arcblock.btcaccounttool.bean.StaredAccountBean;
import com.arcblock.btcaccounttool.btc.RichestAccountsQuery;
import com.arcblock.btcaccounttool.btc.type.PageInput;
import com.arcblock.btcaccounttool.utils.StatusBarUtils;
import com.arcblock.btcaccounttool.viewmodel.StaredAccountViewModel;
import com.arcblock.corekit.bean.CoreKitBean;
import com.arcblock.corekit.bean.CoreKitPagedBean;
import com.arcblock.corekit.utils.CoreKitBeanMapper;
import com.arcblock.corekit.utils.CoreKitDiffUtil;
import com.arcblock.corekit.utils.CoreKitPagedHelper;
import com.arcblock.corekit.viewmodel.CoreKitPagedViewModel;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

public class RichestAccountsListActivity extends AppCompatActivity {
	private RichestAccountListAdapter mRichestAccountListAdapter;

	private List<RichestAccountsQuery.Datum> mAccounts = new ArrayList<>();
	private CoreKitPagedViewModel<RichestAccountsQuery.Data, RichestAccountsQuery.Datum> mRichestAccountsViewModel;
	private List<StaredAccountBean> mStaredAccountBeans = new ArrayList<>();


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		this.getWindow().setBackgroundDrawableResource(R.color.transparent);

		StatusBarUtils.MIUISetStatusBarLightMode(this.getWindow(), false);
		StatusBarUtils.FlymeSetStatusBarLightMode(this.getWindow(), false);

		setContentView(R.layout.activity_richest_accounts_list);

		initView();
		initData();
	}

	private void initView() {
		findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		RecyclerView richestAccountListRcv = (RecyclerView) findViewById(R.id.richest_account_list_rcv);
		richestAccountListRcv.setLayoutManager(new LinearLayoutManager(this));

		mRichestAccountListAdapter = new RichestAccountListAdapter(R.layout.item_richest_account_list, mAccounts, mStaredAccountBeans);
		mRichestAccountListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
			@Override
			public void onLoadMoreRequested() {
				mRichestAccountsViewModel.loadMore();
			}
		}, richestAccountListRcv);
		mRichestAccountListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
				Intent intent = new Intent(RichestAccountsListActivity.this, AccountDetailActivity.class);
				intent.putExtra(AccountDetailActivity.ADDRESS_KEY, mAccounts.get(position).getAddress());
				startActivity(intent);
			}
		});
		richestAccountListRcv.setAdapter(mRichestAccountListAdapter);
	}

	private void initData() {
//1.init a corekitpagedhelper
		//	1.1 set initial query
		//  1.2 set loadmore query
		//  1.3 set refresh query
		final CoreKitPagedHelper coreKitPagedHelper = new CoreKitPagedHelper() {

			@Override
			public Query getInitialQuery() {
				return RichestAccountsQuery.builder().build();
			}

			@Override
			public Query getLoadMoreQuery() {
				PageInput pageInput = null;
				if (!TextUtils.isEmpty(getCursor())) {
					pageInput = PageInput.builder().cursor(getCursor()).build();
				}
				return RichestAccountsQuery.builder().paging(pageInput).build();
			}

			@Override
			public Query getRefreshQuery() {
				return RichestAccountsQuery.builder().build();
			}
		};

		//2. init data mapper
		CoreKitBeanMapper<Response<RichestAccountsQuery.Data>, List<RichestAccountsQuery.Datum>> blocksMapper = new CoreKitBeanMapper<Response<RichestAccountsQuery.Data>, List<RichestAccountsQuery.Datum>>() {

			@Override
			public List<RichestAccountsQuery.Datum> map(Response<RichestAccountsQuery.Data> dataResponse) {
				if (dataResponse != null && dataResponse.data().getRichestAccounts() != null) {
					// set page info to CoreKitPagedHelper
					if (dataResponse.data().getRichestAccounts().getPage() != null) {
						// set is have next flag to CoreKitPagedHelper
						coreKitPagedHelper.setHasMore(dataResponse.data().getRichestAccounts().getPage().isNext());
						// set new cursor to CoreKitPagedHelper
						coreKitPagedHelper.setCursor(dataResponse.data().getRichestAccounts().getPage().getCursor());
					}
					return dataResponse.data().getRichestAccounts().getData();
				}
				return null;
			}
		};

		//3. init the ViewModel with CustomClientFactory
		CoreKitPagedViewModel.CustomClientFactory factory = new CoreKitPagedViewModel.CustomClientFactory(blocksMapper, coreKitPagedHelper, BtcAccountToolApp.getInstance().abCoreKitClient());
		mRichestAccountsViewModel = ViewModelProviders.of(this, factory).get(CoreKitPagedViewModel.class);
		mRichestAccountsViewModel.getCleanQueryData().observe(this, new Observer<CoreKitPagedBean<List<RichestAccountsQuery.Datum>>>() {
			@Override
			public void onChanged(@Nullable CoreKitPagedBean<List<RichestAccountsQuery.Datum>> coreKitPagedBean) {
				//1. handle return data
				if (coreKitPagedBean.getStatus() == CoreKitBean.SUCCESS_CODE) {
					if (coreKitPagedBean.getData() != null) {
						// new a old list
						List<RichestAccountsQuery.Datum> oldList = new ArrayList<>();
						oldList.addAll(mAccounts);

						// set mBlocks with new data
						mAccounts = coreKitPagedBean.getData();
						DiffUtil.DiffResult result = DiffUtil.calculateDiff(new CoreKitDiffUtil<>(oldList, mAccounts), true);
						// need this line , otherwise the update will have no effect
						mRichestAccountListAdapter.setNewListData(mAccounts);
						result.dispatchUpdatesTo(mRichestAccountListAdapter);
					}
				}

				//2. view status change and loadMore component need
				if (coreKitPagedHelper.isHasMore()) {
					mRichestAccountListAdapter.setEnableLoadMore(true);
					mRichestAccountListAdapter.loadMoreComplete();
				} else {
					mRichestAccountListAdapter.loadMoreEnd();
				}
			}
		});

		StaredAccountViewModel staredAccountViewModel = ViewModelProviders.of(this).get(StaredAccountViewModel.class);
		staredAccountViewModel.getAllStaredAccountBean().observe(this, new Observer<List<StaredAccountBean>>() {
			@Override
			public void onChanged(@Nullable List<StaredAccountBean> staredAccountBeans) {
				Log.e("onChanged", "total=>" + staredAccountBeans.size() + " count");
				mStaredAccountBeans.clear();
				mStaredAccountBeans.addAll(staredAccountBeans);
				mRichestAccountListAdapter.notifyDataSetChanged();
			}
		});
	}

}
