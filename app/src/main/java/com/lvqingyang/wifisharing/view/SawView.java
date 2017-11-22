package com.lvqingyang.wifisharing.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/11/22
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class SawView extends View {
    private Paint mPaint;
    private Path mSawPath;
    private final static int SAW_RADIOUS=10;
    private final static int TOP_SPACING=2;

    public SawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initPaint();
    }

    private void initPaint() {
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                SAW_RADIOUS*2+TOP_SPACING);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initSaw(w, h);
        invalidate();
    }

    private void initSaw(int w, int h) {
        mSawPath=new Path();
        mSawPath.addRect(0, TOP_SPACING/2, w, SAW_RADIOUS, Path.Direction.CW);

        Path circlePath=new Path();
        int count=w/(SAW_RADIOUS*2)+1;
        for (int i = 0; i < count; i++) {
            boolean even=i%2==0;
            circlePath.reset();
            circlePath.addCircle((2*i+1)*SAW_RADIOUS,
                    SAW_RADIOUS+TOP_SPACING+(even?-2: 2),
                    SAW_RADIOUS, Path.Direction.CW);
            if (even) {
                mSawPath.op(circlePath, Path.Op.UNION);
            }else {
                mSawPath.op(circlePath, Path.Op.DIFFERENCE);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mSawPath, mPaint);
        mPaint.setColor(Color.parseColor("#dadada"));
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mSawPath, mPaint);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getMeasuredWidth(), TOP_SPACING, mPaint);
    }
}
