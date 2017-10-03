package com.lvqingyang.wifisharing.User.Credit;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.lvqingyang.wifisharing.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 一句话功能描述
 * 功能详细描述
 *
 * @author Lv Qingyang
 * @date 2017/10/1
 * @email biloba12345@gamil.com
 * @github https://github.com/biloba123
 * @blog https://biloba123.github.io/
 */
public class CreditSesameView extends View {
    String[] sesameStr;
    private int defaultSize;
    private int arcDistance;
    private int width;
    private int height;
    private static final int defaultPadding = 20;
    private static final float mStartAngle = 165.0F;
    private static final float mEndAngle = 210.0F;
    private Paint mMiddleArcPaint;
    private Paint mInnerArcPaint;
    private Paint mTextPaint;
    private Paint mCalibrationPaint;
    private Paint mSmallCalibrationPaint;
    private Paint mCalibrationTextPaint;
    private Paint mArcProgressPaint;
    private int radius;
    private RectF mMiddleRect;
    private RectF mInnerRect;
    private RectF mMiddleProgressRect;
    private int mMinNum;
    private int mMaxNum;
    private float mCurrentAngle;
    private float mTotalAngle;
    private String sesameLevel;
    private String evaluationTime;
    private Bitmap bitmap;
    private float[] pos;
    private float[] tan;
    private Matrix matrix;
    private Paint mBitmapPaint;

    public CreditSesameView(Context context) {
        this(context, (AttributeSet)null);
    }

    public CreditSesameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreditSesameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.sesameStr = new String[]{"30", "较差", "60", "中等", "90", "良好", "120", "优秀", "150", "极好", "200"};
        this.mMinNum = 0;
        this.mMaxNum = 950;
        this.mCurrentAngle = 0.0F;
        this.mTotalAngle = 210.0F;
        this.sesameLevel = "";
        this.evaluationTime = "";
        this.init();
    }

    private void init() {
        this.defaultSize = this.dp2px(250);
        this.arcDistance = this.dp2px(14);
        this.mMiddleArcPaint = new Paint(1);
        this.mMiddleArcPaint.setStrokeWidth(8.0F);
        this.mMiddleArcPaint.setColor(-1);
        this.mMiddleArcPaint.setStyle(Paint.Style.STROKE);
        this.mMiddleArcPaint.setAlpha(80);
        this.mInnerArcPaint = new Paint(1);
        this.mInnerArcPaint.setStrokeWidth(30.0F);
        this.mInnerArcPaint.setColor(-1);
        this.mInnerArcPaint.setAlpha(80);
        this.mInnerArcPaint.setStyle(Style.STROKE);
        this.mTextPaint = new Paint(1);
        this.mTextPaint.setColor(-1);
        this.mTextPaint.setTextAlign(Align.CENTER);
        this.mCalibrationPaint = new Paint(1);
        this.mCalibrationPaint.setStrokeWidth(4.0F);
        this.mCalibrationPaint.setStyle(Style.STROKE);
        this.mCalibrationPaint.setColor(-1);
        this.mCalibrationPaint.setAlpha(120);
        this.mSmallCalibrationPaint = new Paint(1);
        this.mSmallCalibrationPaint.setStrokeWidth(1.0F);
        this.mSmallCalibrationPaint.setStyle(Style.STROKE);
        this.mSmallCalibrationPaint.setColor(-1);
        this.mSmallCalibrationPaint.setAlpha(130);
        this.mCalibrationTextPaint = new Paint(1);
        this.mCalibrationTextPaint.setTextSize(30.0F);
        this.mCalibrationTextPaint.setColor(-1);
        this.mArcProgressPaint = new Paint(1);
        this.mArcProgressPaint.setStrokeWidth(8.0F);
        this.mArcProgressPaint.setColor(-1);
        this.mArcProgressPaint.setStyle(Style.STROKE);
        this.mArcProgressPaint.setStrokeCap(Cap.ROUND);
        this.mBitmapPaint = new Paint();
        this.mBitmapPaint.setStyle(Style.FILL);
        this.mBitmapPaint.setAntiAlias(true);
        this.bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_circle);
        this.pos = new float[2];
        this.tan = new float[2];
        this.matrix = new Matrix();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension(this.resolveMeasure(widthMeasureSpec, this.defaultSize), this.resolveMeasure(heightMeasureSpec, this.defaultSize));
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        this.radius = this.width / 2;
        this.mMiddleRect = new RectF(20.0F, 20.0F, (float)(this.width - 20), (float)(this.height - 20));
        this.mInnerRect = new RectF((float)(20 + this.arcDistance), (float)(20 + this.arcDistance), (float)(this.width - 20 - this.arcDistance), (float)(this.height - 20 - this.arcDistance));
        this.mMiddleProgressRect = new RectF(20.0F, 20.0F, (float)(this.width - 20), (float)(this.height - 20));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawMiddleArc(canvas);
        this.drawInnerArc(canvas);
        this.drawSmallCalibration(canvas);
        this.drawCalibrationAndText(canvas);
        this.drawCenterText(canvas);
        this.drawRingProgress(canvas);
    }

    private void drawSmallCalibration(Canvas canvas) {
        canvas.save();
        canvas.rotate(-105.0F, (float)this.radius, (float)this.radius);
        int startDst = (int)((float)(20 + this.arcDistance) - this.mInnerArcPaint.getStrokeWidth() / 2.0F - 1.0F);
        int endDst = (int)((float)startDst + this.mInnerArcPaint.getStrokeWidth());

        for(int i = 0; i <= 35; ++i) {
            canvas.drawLine((float)this.radius, (float)startDst, (float)this.radius, (float)endDst, this.mSmallCalibrationPaint);
            canvas.rotate(6.0F, (float)this.radius, (float)this.radius);
        }

        canvas.restore();
    }

    private void drawRingProgress(Canvas canvas) {
        Path path = new Path();
        path.addArc(this.mMiddleProgressRect, 165.0F, this.mCurrentAngle);
        PathMeasure pathMeasure = new PathMeasure(path, false);
        pathMeasure.getPosTan(pathMeasure.getLength() * 1.0F, this.pos, this.tan);
        this.matrix.reset();
        this.matrix.postTranslate(this.pos[0] - (float)(this.bitmap.getWidth() / 2), this.pos[1] - (float)(this.bitmap.getHeight() / 2));
        canvas.drawPath(path, this.mArcProgressPaint);
        if(this.mCurrentAngle != 0.0F) {
            canvas.drawBitmap(this.bitmap, this.matrix, this.mBitmapPaint);
            this.mBitmapPaint.setColor(-1);
            canvas.drawCircle(this.pos[0], this.pos[1], 8.0F, this.mBitmapPaint);
        }
    }

    private void drawCenterText(Canvas canvas) {
        this.mTextPaint.setTextSize(30.0F);
        canvas.drawText("BETA", (float)this.radius, (float)(this.radius - 130), this.mTextPaint);
        this.mTextPaint.setTextSize(200.0F);
        this.mTextPaint.setStyle(Style.STROKE);
        canvas.drawText(String.valueOf(this.mMinNum), (float)this.radius, (float)(this.radius + 70), this.mTextPaint);
        this.mTextPaint.setTextSize(80.0F);
        canvas.drawText(this.sesameLevel, (float)this.radius, (float)(this.radius + 160), this.mTextPaint);
        this.mTextPaint.setTextSize(30.0F);
        canvas.drawText(this.evaluationTime, (float)this.radius, (float)(this.radius + 205), this.mTextPaint);
    }

    private void drawCalibrationAndText(Canvas canvas) {
        canvas.save();
        canvas.rotate(-105.0F, (float)this.radius, (float)this.radius);
        int startDst = (int)((float)(20 + this.arcDistance) - this.mInnerArcPaint.getStrokeWidth() / 2.0F - 1.0F);
        int endDst = (int)((float)startDst + this.mInnerArcPaint.getStrokeWidth());
        byte rotateAngle = 21;

        for(int i = 1; i < 12; ++i) {
            if(i % 2 != 0) {
                canvas.drawLine((float)this.radius, (float)startDst, (float)this.radius, (float)endDst, this.mCalibrationPaint);
            }

            float textLen = this.mCalibrationTextPaint.measureText(this.sesameStr[i - 1]);
            canvas.drawText(this.sesameStr[i - 1], (float)this.radius - textLen / 2.0F, (float)(endDst + 40), this.mCalibrationTextPaint);
            canvas.rotate((float)rotateAngle, (float)this.radius, (float)this.radius);
        }

        canvas.restore();
    }

    private void drawInnerArc(Canvas canvas) {
        canvas.drawArc(this.mInnerRect, 165.0F, 210.0F, false, this.mInnerArcPaint);
    }

    private void drawMiddleArc(Canvas canvas) {
        canvas.drawArc(this.mMiddleRect, 165.0F, 210.0F, false, this.mMiddleArcPaint);
    }

    public int resolveMeasure(int measureSpec, int defaultSize) {
        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch(MeasureSpec.getMode(measureSpec)) {
            case -2147483648:
                result = Math.min(specSize, defaultSize);
                break;
            case 0:
                result = defaultSize;
            case 1073741824:
                break;
            default:
                result = defaultSize;
        }

        return result;
    }

    public void setSesameValues(int values) {
        if(values <= 30) {
            this.mMaxNum = values;
            this.mTotalAngle = 0.0F;
            this.sesameLevel = "信用较差";
            this.evaluationTime = "评估时间:" + this.getCurrentTime();
        } else if(values <= 60) {
            this.mMaxNum = values;
            this.mTotalAngle = (float)((values - 30) * 40) / 30.0F + 2.0F;
            this.sesameLevel = "信用较差";
            this.evaluationTime = "评估时间:" + this.getCurrentTime();
        } else if(values <= 150) {
            this.mMaxNum = values;
            if(values > 60 && values <= 90) {
                this.sesameLevel = "信用中等";
                this.mTotalAngle = (float)((values - 60) * 40) / 30.0F + 43.0F;
            } else if(values > 90 && values <= 120) {
                this.sesameLevel = "信用良好";
                this.mTotalAngle = (float)((values - 60) * 40) / 30.0F + 45.0F;
            } else {
                this.sesameLevel = "信用优秀";
                this.mTotalAngle = (float)((values - 60) * 40) / 30.0F + 48.0F;
            }

            this.evaluationTime = "评估时间:" + this.getCurrentTime();
        } else if(values <= 200) {
            this.mMaxNum = values;
            this.mTotalAngle = (float)((values - 150) * 40) / 50.0F + 170.0F;
            this.sesameLevel = "信用极好";
            this.evaluationTime = "评估时间:" + this.getCurrentTime();
        } else {
            this.mTotalAngle = 240.0F;
        }

        this.startAnim();
    }

    public void startAnim() {
        ValueAnimator mAngleAnim = ValueAnimator.ofFloat(new float[]{this.mCurrentAngle, this.mTotalAngle});
        mAngleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mAngleAnim.setDuration(3000L);
        mAngleAnim.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                CreditSesameView.this.mCurrentAngle = ((Float)valueAnimator.getAnimatedValue()).floatValue();
                CreditSesameView.this.postInvalidate();
            }
        });
        mAngleAnim.start();
        ValueAnimator mNumAnim = ValueAnimator.ofInt(new int[]{this.mMinNum, this.mMaxNum});
        mNumAnim.setDuration(3000L);
        mNumAnim.setInterpolator(new LinearInterpolator());
        mNumAnim.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                CreditSesameView.this.mMinNum = ((Integer)valueAnimator.getAnimatedValue()).intValue();
                CreditSesameView.this.postInvalidate();
            }
        });
        mNumAnim.start();
    }

    public int dp2px(int values) {
        float density = this.getResources().getDisplayMetrics().density;
        return (int)((float)values * density + 0.5F);
    }

    public String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd");
        return format.format(new Date());
    }
}
