package example.com.myprogressbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * Created by zcxd on 2018/4/16.
 * @author Erichthonius
 */

public class MyProgressBar extends ProgressBar {
    private static final int DEFAULT_VAULES=30;
    //进度条默认颜色(粉)
    private static final int DEFAULT_BACK_LINE_COLOR=0xffFF8080;
    private static final int DEFAULT_BACK_LINE_HEIGHT=DEFAULT_VAULES;
    //进度条默认颜色(蓝)
    private static final int DEFAULT_FORE_LINE_COLOR = 0xff95CAFF;
    private static final int DEFAULT_FORE_LINE_HEIGHT = DEFAULT_VAULES;
    //图片大小
    private static final int DEFAULT_PICTURE_WIDTH = 60;
    private static final int DEFAULT_PICTURE_HEIGHT = 80;
    //文字大小和位置
    private static final int DEFAULT_TEXT_SIZE = 16;
    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_TEXT_OFFSET = 10;
    /**
     * 变量
     */
    private int backHeight=dp2px(DEFAULT_BACK_LINE_HEIGHT);
    private int backColor=DEFAULT_BACK_LINE_COLOR;
    private int foreHeight=dp2px(DEFAULT_FORE_LINE_HEIGHT);
    private int foreColor=DEFAULT_FORE_LINE_COLOR;
    private int textSize=sp2px(DEFAULT_TEXT_SIZE);
    private int textColor=DEFAULT_TEXT_COLOR;
    private int textOffSet=dp2px(DEFAULT_TEXT_OFFSET);
    private int pritureWidth=dp2px(DEFAULT_PICTURE_WIDTH);
    private int pritureHeigth=dp2px(DEFAULT_PICTURE_HEIGHT);
    private int progressWidth;
    private Paint backPaint;
    private Paint forePaint;
    private Paint textPaint;
    private Paint picturePaint;
    private Bitmap mBitmap;
    private Bitmap newBitmap;
    public MyProgressBar(Context context) {
        super(context);
        initPaint();
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        obtainAttributes(attrs);
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        obtainAttributes(attrs);
    }
    private void obtainAttributes(AttributeSet attrs){
        TypedArray typedArray=getContext().obtainStyledAttributes(attrs,R.styleable.MyProgressBar);
        backHeight=(int)typedArray.getDimension(R.styleable.MyProgressBar_back_line_height,backHeight);
        backColor=typedArray.getColor(R.styleable.MyProgressBar_back_line_color,backColor);
        foreHeight=(int)typedArray.getDimension(R.styleable.MyProgressBar_fore_line_height,foreHeight);
        foreColor=typedArray.getColor(R.styleable.MyProgressBar_fore_line_color,foreColor);
        textColor=typedArray.getColor(R.styleable.MyProgressBar_text_color,textColor);
        textSize=(int)typedArray.getDimension(R.styleable.MyProgressBar_text_size,textSize);
        textOffSet=(int)typedArray.getDimension(R.styleable.MyProgressBar_text_offset,textOffSet);
        pritureHeigth=(int)typedArray.getDimension(R.styleable.MyProgressBar_picture_height,pritureHeigth);
        pritureWidth=(int)typedArray.getDimension(R.styleable.MyProgressBar_picture_width,pritureWidth);

        typedArray.recycle();
    }
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    private void initPaint(){
        backPaint=new Paint();
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setColor(backColor);
        backPaint.setStrokeWidth(backHeight);

        forePaint=new Paint();
        forePaint.setStyle(Paint.Style.FILL);
        forePaint.setColor(foreColor);
        forePaint.setStrokeWidth(foreHeight);

        textPaint=new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.BLACK);
        picturePaint=new Paint();

        mBitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.houzi);
        newBitmap=resizeBitmap(mBitmap,pritureWidth,pritureHeigth);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=getMeasuredWidth();
        int height=measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
        progressWidth=width-getPaddingLeft()-getPaddingRight();
    }
    private  int measureHeight(int heightMeasureSpec)
    {
        int result=0;
        int mode=MeasureSpec.getMode(heightMeasureSpec);
        int size=MeasureSpec.getSize(heightMeasureSpec);

        if(mode==MeasureSpec.EXACTLY)
        {
            result=size;

        }else
        {
            int textHeight=(int)(textPaint.descent()-textPaint.ascent())+pritureHeigth;
            result = getPaddingTop()+getPaddingBottom()+Math.max(Math.max(backHeight,foreHeight),Math.abs(textHeight));
            if(mode==MeasureSpec.AT_MOST)
            {
                result=Math.min(result,size);
            }
        }
        return result;
    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getPaddingLeft(),getHeight()-(textPaint.descent()-textPaint.ascent())/2);
        boolean noNeedUnRech=false;
        String text=getProgress()+"%";
        int textWidth=(int)textPaint.measureText(text);
        float radio =getProgress()*1.0f/getMax();

        float progressX=radio*progressWidth;
        if(progressX+textWidth>progressWidth)
        {
            progressX=progressWidth-textWidth;
            noNeedUnRech=true;
        }

        float endX=radio*progressWidth-textOffSet/2;
        if(endX>0)
        {
            canvas.drawLine(0,0,endX,0,backPaint);
        }

        int y=(int)(-(textPaint.descent()+textPaint.ascent()/2));
//        int y=(int)(-(DEFAULT_VAULES-textPaint.ascent()/2-textPaint.descent()/2));
        canvas.drawText(text,progressX,y,textPaint);

        if(!noNeedUnRech)
        {
            float start=progressX+textOffSet/2+textWidth;
            canvas.drawLine(start,0,progressWidth,0,forePaint);
        }
        canvas.restore();
        canvas.save();
        canvas.translate(-(pritureWidth-textWidth)/2,0);
        canvas.drawBitmap(newBitmap,progressX,0,picturePaint);
        canvas.restore();
    }


    private int dp2px(int dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpVal,getResources().getDisplayMetrics());
    }

    private int sp2px(int spVal)
    {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spVal,getResources().getDisplayMetrics());
    }
    public static Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true);
            return resizedBitmap;
        } else {
            return null;
        }
    }
}
