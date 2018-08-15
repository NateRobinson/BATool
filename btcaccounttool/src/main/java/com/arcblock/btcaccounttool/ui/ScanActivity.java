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

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.arcblock.btcaccounttool.R;
import com.arcblock.btcaccounttool.utils.StatusBarUtils;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class ScanActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		this.getWindow().setBackgroundDrawableResource(R.color.transparent);

		StatusBarUtils.MIUISetStatusBarLightMode(this.getWindow(), false);
		StatusBarUtils.FlymeSetStatusBarLightMode(this.getWindow(), false);

		setContentView(R.layout.activity_scan);

		findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		CaptureFragment captureFragment = new CaptureFragment();
		// 为二维码扫描界面设置定制化界面
		CodeUtils.setFragmentArgs(captureFragment, R.layout.my_custom_scan);
		captureFragment.setAnalyzeCallback(analyzeCallback);
		getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
	}

	/**
	 * 二维码解析回调函数
	 */
	CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
		@Override
		public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
			bundle.putString(CodeUtils.RESULT_STRING, result.replace("bitcoin:",""));
			resultIntent.putExtras(bundle);
			setResult(RESULT_OK, resultIntent);
			finish();
		}

		@Override
		public void onAnalyzeFailed() {
//			Intent resultIntent = new Intent();
//			Bundle bundle = new Bundle();
//			bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
//			bundle.putString(CodeUtils.RESULT_STRING, "");
//			resultIntent.putExtras(bundle);
//			setResult(RESULT_OK, resultIntent);
//			finish();
		}
	};
}
