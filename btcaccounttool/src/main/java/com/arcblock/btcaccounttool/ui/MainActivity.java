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

import android.Manifest;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arcblock.btcaccounttool.R;
import com.arcblock.btcaccounttool.utils.StatusBarUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_AND_STORAGE = 1;
    private static final int GO_TO_SCAN_PAGE = 2;
    private LinearLayout search_ll;
    private EditText search_et;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        this.getWindow().setBackgroundDrawableResource(R.color.transparent);

        StatusBarUtils.MIUISetStatusBarLightMode(this.getWindow(), false);
        StatusBarUtils.FlymeSetStatusBarLightMode(this.getWindow(), false);

        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        findViewById(R.id.title_tv).requestFocus();

        search_et = findViewById(R.id.search_et);
        search_ll = findViewById(R.id.search_ll);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            search_ll.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        outline.setRoundRect(new Rect(0, 0, view.getWidth(), view.getHeight()), ConvertUtils.dp2px(5));
                    }
                }
            });
            search_ll.setTranslationZ(ConvertUtils.dp2px(6));
        }

        search_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (TextUtils.isEmpty(search_et.getText().toString())) {
                        ToastUtils.showShort("Please input an Address.");
                        return true;
                    }
                    // todo 校验address
                    Intent intent = new Intent(MainActivity.this, AccountDetailActivity.class);
                    intent.putExtra(AccountDetailActivity.ADDRESS_KEY, search_et.getText().toString());
                    startActivity(intent);
                    search_et.setText("");
                }
                return false;
            }
        });


        findViewById(R.id.about_us_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.richest_account_list_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RichestAccountsListActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.stared_account_list_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StaredAccountListActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.scan_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToScan();
            }
        });
    }

    @AfterPermissionGranted(REQUEST_CAMERA_AND_STORAGE)
    private void goToScan() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            Intent intent = new Intent(this, ScanActivity.class);
            startActivityForResult(intent, GO_TO_SCAN_PAGE);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "need camera and storage permissions",
                    REQUEST_CAMERA_AND_STORAGE, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_TO_SCAN_PAGE && resultCode == RESULT_OK) {
            String address = data.getStringExtra(CodeUtils.RESULT_STRING);
            Intent intent = new Intent(this, AccountDetailActivity.class);
            intent.putExtra(AccountDetailActivity.ADDRESS_KEY, address);
            startActivity(intent);
        }
    }
}
