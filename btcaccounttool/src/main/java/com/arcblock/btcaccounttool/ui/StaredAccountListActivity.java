/*
 * Copyright (c) 2017-present ArcBlock Foundation Ltd <https://www.arcblock.io/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.arcblock.btcaccounttool.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.arcblock.btcaccounttool.R;
import com.arcblock.btcaccounttool.adapter.StaredAccountListAdapter;
import com.arcblock.btcaccounttool.bean.StaredAccountBean;
import com.arcblock.btcaccounttool.utils.StatusBarUtils;
import com.arcblock.btcaccounttool.viewmodel.StaredAccountViewModel;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

public class StaredAccountListActivity extends AppCompatActivity {

    private List<StaredAccountBean> mStaredAccountBeans = new ArrayList<>();
    private StaredAccountListAdapter mStaredAccountListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        this.getWindow().setBackgroundDrawableResource(R.color.transparent);

        StatusBarUtils.MIUISetStatusBarLightMode(this.getWindow(), false);
        StatusBarUtils.FlymeSetStatusBarLightMode(this.getWindow(), false);

        setContentView(R.layout.activity_stared_account_list);

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
        RecyclerView account_list_rcv = findViewById(R.id.account_list_rcv);
        account_list_rcv.setLayoutManager(new LinearLayoutManager(this));
        mStaredAccountListAdapter = new StaredAccountListAdapter(R.layout.item_stared_account_list, mStaredAccountBeans);
        mStaredAccountListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(StaredAccountListActivity.this, AccountDetailActivity.class);
                intent.putExtra(AccountDetailActivity.ADDRESS_KEY, mStaredAccountBeans.get(position).getAccountAddress());
                startActivity(intent);
            }
        });
        account_list_rcv.setAdapter(mStaredAccountListAdapter);

        mStaredAccountListAdapter.bindToRecyclerView(account_list_rcv);
        mStaredAccountListAdapter.setEmptyView(R.layout.empty_of_stared_account_list);
    }

    private void initData() {
        StaredAccountViewModel staredAccountViewModel = ViewModelProviders.of(this).get(StaredAccountViewModel.class);
        staredAccountViewModel.getAllStaredAccountBean().observe(this, new Observer<List<StaredAccountBean>>() {
            @Override
            public void onChanged(@Nullable List<StaredAccountBean> staredAccountBeans) {
                Log.e("onChanged", "total=>" + staredAccountBeans.size() + " count");
                mStaredAccountBeans.clear();
                mStaredAccountBeans.addAll(staredAccountBeans);
                mStaredAccountListAdapter.notifyDataSetChanged();
            }
        });
    }
}
