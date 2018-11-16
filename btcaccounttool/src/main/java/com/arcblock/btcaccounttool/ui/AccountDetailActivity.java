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

import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arcblock.btcaccounttool.BtcAccountToolApp;
import com.arcblock.btcaccounttool.R;
import com.arcblock.btcaccounttool.bean.StaredAccountBean;
import com.arcblock.btcaccounttool.btc.AccountByAddressQuery;
import com.arcblock.btcaccounttool.db.AccountToolDB;
import com.arcblock.btcaccounttool.utils.BtcValueUtils;
import com.arcblock.btcaccounttool.utils.StatusBarUtils;
import com.arcblock.btcaccounttool.view.ScoreView;
import com.arcblock.btcaccounttool.viewmodel.StaredAccountViewModel;
import com.arcblock.corekit.CoreKitQuery;
import com.arcblock.corekit.CoreKitResultListener;
import com.blankj.utilcode.util.ConvertUtils;
import com.uuzuche.lib_zxing.encoding.EncodingHandler;

public class AccountDetailActivity extends AppCompatActivity {

    public static final String ADDRESS_KEY = "address_key";
    private String address = "";
    private TextView address_tv;
    private TextView balance_tv;
    private ImageView un_star_iv;
    private ImageView stared_iv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        this.getWindow().setBackgroundDrawableResource(R.color.transparent);

        StatusBarUtils.MIUISetStatusBarLightMode(this.getWindow(), false);
        StatusBarUtils.FlymeSetStatusBarLightMode(this.getWindow(), false);

        setContentView(R.layout.activity_account_detail);

        address = getIntent().getExtras().getString(ADDRESS_KEY);

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

        un_star_iv = findViewById(R.id.un_star_iv);
        stared_iv = findViewById(R.id.stared_iv);

        ScoreView scoreView = findViewById(R.id.score_view);
        address_tv = findViewById(R.id.address_tv);
        balance_tv = findViewById(R.id.balance_tv);

        address_tv.setText(address);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(scoreView, "progress", 0, 98);
        objectAnimator.setDuration(1500);
        objectAnimator.start();

        // add star
        un_star_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        AccountToolDB.getDatabase(getApplicationContext()).staredAccountDao().insert(new StaredAccountBean(0, address));
                        return null;
                    }
                }.execute();
            }
        });
        // cancel star
        stared_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        AccountToolDB.getDatabase(getApplicationContext()).staredAccountDao().update(new StaredAccountBean(1, address));
                        return null;
                    }
                }.execute();
            }
        });

        findViewById(R.id.qr_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View qrCodeView = LayoutInflater.from(AccountDetailActivity.this).inflate(R.layout.dialog_qr_code, null);
                ImageView qr_code_iv = qrCodeView.findViewById(R.id.qr_code_iv);
                try {
                    qr_code_iv.setImageBitmap(EncodingHandler.createQRCode(address, ConvertUtils.dp2px(300)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new MaterialDialog.Builder(AccountDetailActivity.this)
                        .title("Account QR Code")
                        .customView(qrCodeView, true)
                        .positiveText("Ok")
                        .positiveColor(Color.parseColor("#212121"))
                        .show();
            }
        });
    }

    private void initData() {
        CoreKitQuery coreKitQuery = new CoreKitQuery(this, BtcAccountToolApp.INSTANCE.abCoreKitClient());
        coreKitQuery.query(AccountByAddressQuery.builder().address(address).build(), new CoreKitResultListener<AccountByAddressQuery.Data>() {
            @Override
            public void onSuccess(AccountByAddressQuery.Data data) {
                com.arcblock.btcaccounttool.btc.AccountByAddressQuery.AccountByAddress accountByAddress = data.getAccountByAddress();
                if (accountByAddress != null) {
                    address_tv.setText(accountByAddress.getAddress());
                    balance_tv.setText(BtcValueUtils.formatBtcValue(accountByAddress.getBalance()));
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

        StaredAccountViewModel staredAccountViewModel = ViewModelProviders.of(this).get(StaredAccountViewModel.class);
        staredAccountViewModel.getStaredAccountBean(address).observe(this, new Observer<StaredAccountBean>() {
            @Override
            public void onChanged(@Nullable StaredAccountBean staredAccountBean) {
                if (staredAccountBean != null) {
                    un_star_iv.setVisibility(View.GONE);
                    stared_iv.setVisibility(View.VISIBLE);
                } else {
                    un_star_iv.setVisibility(View.VISIBLE);
                    stared_iv.setVisibility(View.GONE);
                }
            }
        });
    }
}