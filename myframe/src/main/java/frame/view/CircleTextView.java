package frame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.myframe.R;

import java.util.Random;

/**
 * Author：LvQingYang
 * Date：2017/8/18
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class CircleTextView extends View {
    private Paint mTextPaint,mBgPaint,mBorderPaint;
    //显示文字
    private String mText;
    //半径大小
    private float mRadius;
    //边框宽度
    private float mBorderWidth;
    //随机颜色备选
    public static int[] mColorArr=new int[]{
            R.color.accent_red,
            R.color.accent_pink,
            R.color.accent_purple,
            R.color.accent_deep_purple,
            R.color.accent_indago,
            R.color.accent_blue,
            R.color.accent_cyan,
            R.color.accent_teal,
            R.color.accent_green,
            R.color.accent_yellow,
            R.color.accent_amber,
            R.color.accent_orange,
            R.color.accent_brown,
            R.color.accent_grey,
            R.color.accent_black,
    };
    //默认字体大小
    private static final float DEFAULT_TEXT_SIZE = 36f;
    //默认半径
    private static final float DEFAULT_RADIUS = 128f;
    private static final String TAG = "CircleTextView";

    public CircleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mBgPaint=new Paint();
        mTextPaint=new Paint();
        TypedArray a = context.getTheme().obtainStyledAttributes(
                        attrs,
                        R.styleable.CircleTextView,
                        0, 0);
        try {
            //背景颜色
            int bgColor;
            if (a.getBoolean(R.styleable.CircleTextView_randColor,true)) {
                //随机颜色
                bgColor=getRandColor(context);
            }else {
                //自定义颜色
                bgColor=a.getColor(R.styleable.CircleTextView_bgColor,getRandColor(context));
            }
            mBgPaint.setColor(bgColor);
            //半径
            mRadius=a.getDimension(R.styleable.CircleTextView_radius,DEFAULT_RADIUS);

            //文字
            mText=a.getString(R.styleable.CircleTextView_text);
            //字体颜色，默认白色
            int textColor=a.getColor(R.styleable.CircleTextView_textColor, Color.WHITE);
            mTextPaint.setColor(textColor);
            //文字大小
            mTextPaint.setTextSize(a.getDimension(R.styleable.CircleTextView_textSize,DEFAULT_TEXT_SIZE));
            //粗体
            if (a.getBoolean(R.styleable.CircleTextView_boldText,true)) {
                mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            }

            //边框
            mBorderWidth=a.getDimension(R.styleable.CircleTextView_borderWidth,0f);
            if (mBorderWidth!=0f) {
                mBorderPaint=new Paint();
                mBorderPaint.setColor(a.getColor(R.styleable.CircleTextView_borderColor,Color.BLACK));
                mBorderPaint.setStyle(Paint.Style.STROKE);
                mBorderPaint.setStrokeWidth(mBorderWidth);
            }
        } finally {
            a.recycle();
        }
    }

    /**
     * 测量长度，测量规则，如果矩形区域足够画半径为radius的圆，则在中心作为圆心绘制
     * 如果区域不足够，则调整radius大小，使其能刚好画在区域内
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //不确定的宽和高将设为半径两倍，这样区域都是确定的
        int width=measure(widthMeasureSpec);
        int height=measure(heightMeasureSpec);

        int min=Math.min(height, width);
        if (Math.abs(min/2f-mBorderWidth-mRadius)<=0.00001f) {//矩形区域足够画圆
            setMeasuredDimension(width,height);
        }else {//矩形区域不足够画圆，取宽高小的除2作为半径，并把不确定的宽和高将设为半径两倍
            float rr=mRadius/(mBorderWidth+mRadius);
            mRadius = min / 2f*rr;
            mBorderWidth=min/2f-mRadius;
            setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
        }
    }

    private int measure(int measureSpec){
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                return (int) ((mRadius+mBorderWidth)*2);
            default:
                return MeasureSpec.getSize(measureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredWidth=getMeasuredWidth();
        int measuredHeight=getMeasuredHeight();

        if (mBorderPaint!=null) {
            //边框
//            canvas.drawCircle(measuredWidth/2f,measuredHeight/2f,mRadius+mBorderWidth,mBorderPaint);
            RectF rectF=new RectF();
            rectF.left=measuredWidth/2f-mRadius-mBorderWidth/2;
            rectF.top=measuredHeight/2f-mRadius-mBorderWidth/2;
            rectF.right=measuredWidth/2f+mRadius+mBorderWidth/2;
            rectF.bottom=measuredHeight/2f+mRadius+mBorderWidth/2;
            canvas.drawArc(rectF,0,360,false,mBorderPaint);
        }

        //画圆形背景
        canvas.drawCircle(measuredWidth/2f,measuredHeight/2f,mRadius,mBgPaint);
        //画字
        if (mText != null) {
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            int baseline = measuredHeight/2- (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.top;
            // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mText,measuredWidth/2f,baseline,mTextPaint);
        }
    }

    //随机获取颜色
    public static int getRandColor(Context context){
        return context.getResources().getColor(mColorArr[new Random().nextInt(mColorArr.length)]);
    }

    public void setText(String text){
        mText=text;
        invalidate();
    }

}

