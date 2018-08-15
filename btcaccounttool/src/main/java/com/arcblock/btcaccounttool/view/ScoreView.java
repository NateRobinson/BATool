package com.arcblock.btcaccounttool.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;

public class ScoreView extends View {
	public ScoreView(Context context) {
		super(context);
	}

	public ScoreView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public ScoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	final float radius = ConvertUtils.dp2px(60);

	RectF arcRectF = new RectF();
	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint paintBottom = new Paint(Paint.ANTI_ALIAS_FLAG);

	float progress = 0;

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
		invalidate();
	}

	{
		paint.setTextSize(ConvertUtils.dp2px(38));
		paint.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float centerX = getWidth() / 2;
		float centerY = getHeight() / 2;

		paint.setColor(Color.parseColor("#03A9F4"));
		paintBottom.setColor(Color.parseColor("#dddddd"));
		paint.setStyle(Paint.Style.STROKE);
		paintBottom.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paintBottom.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(ConvertUtils.dp2px(10));
		paintBottom.setStrokeWidth(ConvertUtils.dp2px(10));
		arcRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
		canvas.drawArc(arcRectF, 135, 270, false, paintBottom);
		canvas.drawArc(arcRectF, 135, progress * 2.7f, false, paint);


		paint.setColor(Color.parseColor("#03A9F4"));
		paint.setStyle(Paint.Style.FILL);
		canvas.drawText((int) progress + "", centerX, centerY - (paint.ascent() + paint.descent()) / 2, paint);
	}
}
