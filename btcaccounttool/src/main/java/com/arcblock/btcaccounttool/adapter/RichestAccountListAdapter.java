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
package com.arcblock.btcaccounttool.adapter;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcblock.btcaccounttool.R;
import com.arcblock.btcaccounttool.bean.StaredAccountBean;
import com.arcblock.btcaccounttool.btc.RichestAccountsQuery;
import com.arcblock.btcaccounttool.db.AccountToolDB;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class RichestAccountListAdapter extends BaseQuickAdapter<RichestAccountsQuery.Datum, BaseViewHolder> {

	private List<StaredAccountBean> mStaredAccountBeans;

	public RichestAccountListAdapter(int layoutResId, @Nullable List<RichestAccountsQuery.Datum> data, List<StaredAccountBean> staredAccountBeans) {
		super(layoutResId, data);
		this.mStaredAccountBeans = staredAccountBeans;
	}

	public void setNewListData(List<RichestAccountsQuery.Datum> newList) {
		this.mData = newList;
	}

	@Override
	protected void convert(BaseViewHolder helper, RichestAccountsQuery.Datum item) {
		int posi = helper.getAdapterPosition();
		ImageView orderIv = helper.getView(R.id.order_iv);
		TextView orderTv = helper.getView(R.id.order_tv);
		if (posi < 3) {
			orderIv.setVisibility(View.VISIBLE);
			orderTv.setVisibility(View.GONE);
			if (posi == 0) {
				orderIv.setBackgroundResource(R.mipmap.order_first);
			} else if (posi == 1) {
				orderIv.setBackgroundResource(R.mipmap.order_second);
			} else if (posi == 2) {
				orderIv.setBackgroundResource(R.mipmap.order_third);
			}
		} else {
			orderIv.setVisibility(View.GONE);
			orderTv.setVisibility(View.VISIBLE);
			orderTv.setText(posi + 1 + "");
		}

		helper.setText(R.id.address_tv, item.getAddress());
		helper.setText(R.id.balance_tv, "Balance: " + item.getBalance() + " BTC");

		ImageView stared_iv = helper.getView(R.id.stared_iv);
		ImageView unstar_iv = helper.getView(R.id.unstar_iv);

		if (isStared(item.getAddress())) {
			unstar_iv.setVisibility(View.GONE);
			stared_iv.setVisibility(View.VISIBLE);
		} else {
			unstar_iv.setVisibility(View.VISIBLE);
			stared_iv.setVisibility(View.GONE);
		}

		// cancel star
		stared_iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... voids) {
						AccountToolDB.getDatabase(mContext).staredAccountDao().update(new StaredAccountBean(1, item.getAddress()));
						return null;
					}
				}.execute();
			}
		});

		// add to star
		unstar_iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... voids) {
						AccountToolDB.getDatabase(mContext).staredAccountDao().insert(new StaredAccountBean(0, item.getAddress()));
						return null;
					}
				}.execute();
			}
		});
	}

	private boolean isStared(String address) {
		for (int i = 0; i < mStaredAccountBeans.size(); i++) {
			if (TextUtils.equals(address, mStaredAccountBeans.get(i).getAccountAddress())) {
				return true;
			}
		}
		return false;
	}

}
