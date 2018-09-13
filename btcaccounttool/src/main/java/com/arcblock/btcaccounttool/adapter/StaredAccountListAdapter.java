package com.arcblock.btcaccounttool.adapter;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.view.View;

import com.arcblock.btcaccounttool.R;
import com.arcblock.btcaccounttool.bean.StaredAccountBean;
import com.arcblock.btcaccounttool.db.AccountToolDB;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class StaredAccountListAdapter extends BaseQuickAdapter<StaredAccountBean, BaseViewHolder> {
    public StaredAccountListAdapter(int layoutResId, @Nullable List<StaredAccountBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final StaredAccountBean item) {
        helper.setText(R.id.order_tv, helper.getAdapterPosition() + 1 + "");
        helper.setText(R.id.address_tv, item.getAccountAddress());
        helper.getView(R.id.stared_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        AccountToolDB.getDatabase(mContext).staredAccountDao().update(new StaredAccountBean(1, item.getAccountAddress()));
                        return null;
                    }
                }.execute();
            }
        });
    }
}
