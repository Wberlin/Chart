package com.wubolin.widget.chart.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.wubolin.widget.chart.R;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Administrator on 2017/12/6.
 */

public class PieChartView extends View {
    private static String TAG=PieChartView.class.getSimpleName();
    private Paint piePaint;//画扇形圆
    private Paint textPaint;//画文字的画笔
    private Paint valuePaint;//画值的画笔
    private Paint bitmapPaint;//图片画笔

    //总的值
    private int sum;
    //当前角度
    private float currentAngle;

    //深红色
    private int redColor;
    //深绿色
    private int greenColor;
    //深蓝色
    private int blueColor;
    //文字颜色
    private int grayColor;



    private float mRadius=120;//圆的半径
    private int mMinRadius=8;//小圆半径
    private float mCenterRadius=60;//中心圆半径
    private float mDistanceCenter=0.65f;//离中心点的距离
    private float mNumberTextSize=14;//数字的显示大小
    private float mWordTextSize=14;//文字的显示大小



    private List<PieCharModel> pieCharModels;


    //偏移长度
    private int offset=20;


    //动画变化的值
    private float animValue;
    //一个从0~1变化的值
    private float animZeroToOne;


    //控件固定的宽度和高度
    private static int mWidth=720;
    private static int mHeight=400;


    //测试移动图片的路径
    private Path imgPath;


    public PieChartView(Context context) {
        this(context,null);
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setUp(context,attrs);
    }



    public PieChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(context,attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);





    }


    private void setUp(Context context,AttributeSet attrs) {

        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.PieChartView);






        redColor=getResources().getColor(R.color.deep_red);
        blueColor=getResources().getColor(R.color.deep_blue);
        greenColor=getResources().getColor(R.color.deep_green);
        grayColor=getResources().getColor(R.color.text_color_01);

        mRadius=getResources().getDimension(R.dimen.pie_circle_radius);
        mCenterRadius=getResources().getDimension(R.dimen.center_circle_radius);

        try{
            mRadius=ta.getDimension(R.styleable.PieChartView_out_radius,mRadius);
            mCenterRadius=ta.getDimension(R.styleable.PieChartView_in_radius,mCenterRadius);
            mDistanceCenter=ta.getFloat(R.styleable.PieChartView_distance_to_center,mDistanceCenter);
            mNumberTextSize=ta.getDimension(R.styleable.PieChartView_number_text_size,mNumberTextSize);
            mWordTextSize=ta.getDimension(R.styleable.PieChartView_word_text_size,mWordTextSize);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ta.recycle();
        }





        piePaint=new Paint();
        piePaint.setAntiAlias(true);
        piePaint.setColor(Color.RED);
        piePaint.setStyle(Paint.Style.FILL);


        textPaint=new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(grayColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(mWordTextSize);

        valuePaint=new Paint();
        valuePaint.setAntiAlias(true);
        valuePaint.setColor(grayColor);
        valuePaint.setStyle(Paint.Style.FILL);
        valuePaint.setTextSize(mNumberTextSize);

        bitmapPaint=new Paint();
        bitmapPaint.setAntiAlias(true);



        pieCharModels=new ArrayList<>();
        for(int i=0;i<3;i++){
            PieCharModel pieCharModel=new PieCharModel();

            switch (i) {
                case 0:
                    pieCharModel.setValue(119999);
                    pieCharModel.setColor(redColor);
                    pieCharModel.setTitle("第一项");
                    pieCharModel.setBitmapId(R.mipmap.door_close);
                    break;
                case 1:
                    pieCharModel.setValue(2000000);
                    pieCharModel.setColor(blueColor);
                    pieCharModel.setTitle("第二项");
                    pieCharModel.setBitmapId(R.mipmap.door);
                    break;
                case 2:
                    pieCharModel.setValue(300000);
                    pieCharModel.setColor(greenColor);
                    pieCharModel.setTitle("第三项");
                    pieCharModel.setBitmapId(R.mipmap.door);
                    break;
            }
            pieCharModels.add(pieCharModel);
        }

        sum=getSum(pieCharModels);



        initAnumator();



    }

    private void initAnumator() {
        ValueAnimator anim=ValueAnimator.ofFloat(0,mCenterRadius);
        anim.setDuration(1000);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animValue=(float)animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();

        ValueAnimator animZero=ValueAnimator.ofFloat(0,1);
        animZero.setDuration(1000);
        animZero.setInterpolator(new AccelerateDecelerateInterpolator());
        animZero.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animZeroToOne=(float)animation.getAnimatedValue();
                invalidate();
            }
        });
        animZero.start();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize=MeasureSpec.getSize(heightMeasureSpec);
        Log.e(TAG,"widthSpecMode:"+widthSpecMode);
        Log.e(TAG,"widthSpecSize:"+widthSpecSize);
        Log.e(TAG,"heightSpecMode:"+heightSpecMode);
        Log.e(TAG,"heightSpecSize:"+heightSpecSize);

        //使用wrap_content 为不确定测量，指定固定高度和固定宽度


        if(widthSpecMode==MeasureSpec.UNSPECIFIED&&heightMeasureSpec==MeasureSpec.UNSPECIFIED){
            setMeasuredDimension((int)mWidth,(int)mHeight);
        }else if(widthSpecMode==MeasureSpec.UNSPECIFIED){
            setMeasuredDimension((int)mWidth,heightSpecSize);
        }else if(heightSpecMode==MeasureSpec.UNSPECIFIED){
            setMeasuredDimension(widthSpecSize,(int)mHeight);
        } else if(widthSpecMode==MeasureSpec.AT_MOST&&heightSpecMode==MeasureSpec.AT_MOST){//使用match_parent为最大父布局大小，也固定高度和固定宽度
            setMeasuredDimension((int)mWidth,(int)mHeight);
        }else if(widthSpecMode==MeasureSpec.AT_MOST){
            setMeasuredDimension((int)mWidth,heightSpecSize);
        }else if(heightSpecMode==MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize,(int)mHeight);
        }
    }
    public void setData(List<PieCharModel> pieCharModels){
        this.pieCharModels=pieCharModels;
        sum=getSum(pieCharModels);
    }

    private int getSum(List<PieCharModel> pieCharModels) {
        int sum=0;
        for(int i=0;i<pieCharModels.size();i++){
            PieCharModel pieCharModel=pieCharModels.get(i);
            sum+=pieCharModel.getValue();
        }
        return sum;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawLine(0,getHeight()/2,getPaddingLeft(),getHeight()/2,piePaint);
//        piePaint.setColor(Color.BLACK);
//        canvas.drawLine(getPaddingLeft(),getHeight()/2-10,getPaddingLeft()+getPaddingRight(),getHeight()/2-10,piePaint);
//        piePaint.setColor(Color.BLUE);
//
//        canvas.drawLine(getPaddingLeft(),getHeight()/2,getPaddingLeft()+getWidth(),getHeight()/2,piePaint);


        canvas.translate((getWidth()+getPaddingLeft()-getPaddingRight())/2,(getHeight()+getPaddingTop() - getPaddingBottom()) / 2);
        RectF oval=new RectF(-mRadius,-mRadius,mRadius,mRadius);
        //piePaint.setColor(Color.RED);




        for(int i=0;i<pieCharModels.size();i++){
            PieCharModel pieCharModel=pieCharModels.get(i);
            float num=pieCharModel.getValue();

            float needDrawAngle=num*1.0f/sum*360;
            piePaint.setColor(pieCharModel.getColor());
            piePaint.setStyle(Paint.Style.FILL);
            canvas.drawArc(oval,currentAngle,needDrawAngle,true,piePaint);
            //canvas.drawText(""+needDrawAngle,mRadius+i*10,mRadius+i*10,piePaint);
            drawCircleAndLineAndText(canvas,currentAngle+needDrawAngle/2,needDrawAngle,pieCharModel);

            currentAngle=currentAngle+needDrawAngle;



        }


        piePaint.setColor(Color.WHITE);
        piePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0,0,animValue,piePaint);

    }




    private void drawCircleAndLineAndText(Canvas canvas,float sumAngle,float needDrawAngle,PieCharModel pieCharModel){
        //piePaint.setColor(Color.BLACK);
        if(sumAngle>=0&&sumAngle<=90){//画布坐标系第一象限(数学坐标系第四象限)
            float x1=(float) ((mRadius+20)*Math.cos(Math.toRadians(sumAngle)));
            float y1=(float) ((mRadius+20)*Math.sin(Math.toRadians(sumAngle)));

            //canvas.drawText("x="+x1+",y="+y1,mRadius+20,mRadius+20,piePaint);
            canvas.drawCircle(x1,
                    y1,mMinRadius,piePaint);
            drawLine(canvas,x1,y1,needDrawAngle,pieCharModel);

            //canvas.drawLine(0,0,(float) ((mRadius+20)*Math.cos(Math.toRadians(sumAngle))),(float) ((mRadius+20)*Math.sin(Math.toRadians(sumAngle))),piePaint);

        }else if(sumAngle>90&&sumAngle<=180){ //画布坐标系第二象限(数学坐标系第三象限)
            float x2=(float) ((-mRadius-20)*Math.cos(Math.toRadians(180-sumAngle)));
            float y2=(float) ((mRadius+20)*Math.sin(Math.toRadians(180-sumAngle)));

            canvas.drawCircle(x2,y2
                    ,mMinRadius,piePaint);
            drawLine(canvas,x2,y2,needDrawAngle,pieCharModel);
            //canvas.drawLine(0,0,(float) ((-mRadius-20)*Math.cos(Math.toRadians(180-sumAngle))),(float) ((mRadius+20)*Math.sin(Math.toRadians(180-sumAngle))),piePaint);
        }else if(sumAngle>180&&sumAngle<=270){//画布坐标系第三象限(数学坐标系第二象限)
            float x3=(float) ((-mRadius-20)*Math.cos(Math.toRadians(sumAngle-180)));
            float y3=(float) ((-mRadius-20)*Math.sin(Math.toRadians(sumAngle-180)));

            canvas.drawCircle(x3 ,y3
                    ,mMinRadius,piePaint);
            drawLine(canvas,x3,y3,needDrawAngle,pieCharModel);
            //canvas.drawLine(0,0,(float) ((-mRadius-20)*Math.cos(Math.toRadians(sumAngle-180))),(float) ((-mRadius-20)*Math.sin(Math.toRadians(sumAngle-180))),piePaint);
        }else{//画布坐标系第四象限(数学坐标系第一象限)
            float x4=(float) ((mRadius+20)*Math.cos(Math.toRadians(360-sumAngle)));
            float y4=(float) ((-mRadius-20)*Math.sin(Math.toRadians(360-sumAngle)));

            canvas.drawCircle(x4,y4
                    ,mMinRadius,piePaint);

            //canvas.drawText("x="+x4+",y="+y4,mRadius+20,-mRadius-20,piePaint);

            drawLine(canvas,x4,y4,needDrawAngle,pieCharModel);
           // canvas.drawLine(0,0,(float) ((mRadius+20)*Math.cos(Math.toRadians(360-sumAngle))),(float) ((-mRadius-20)*Math.sin(Math.toRadians(360-sumAngle))),piePaint);
        }
    }

    private void drawLine(Canvas canvas,float x, float y,float needDrawAngle,PieCharModel pieCharModel) {

        String text=pieCharModel.getTitle();

        Path path=new Path();
        piePaint.setStyle(Paint.Style.STROKE);
        path.moveTo(x,y);

        Paint.FontMetricsInt metricsInt=textPaint.getFontMetricsInt();
        Paint.FontMetricsInt metricsInt_value=valuePaint.getFontMetricsInt();

        //字体的宽度
        float w=textPaint.measureText(text);

        //字体的高度
        float h=metricsInt.descent-metricsInt.ascent;

        String valueStr=pieCharModel.getValue()+"";
        //值的宽度
        float wV=valuePaint.measureText(valueStr);
        //值的高度
        float hV=metricsInt_value.descent-metricsInt_value.ascent;


        //canvas.drawLine(mRadius,y+40,mRadius+w,y+40,piePaint);

        Bitmap bigBitMap= BitmapFactory.decodeResource(getResources(),pieCharModel.getBitmapId());
        float px0=0,py0=0;
        float px2=mDistanceCenter*x,py2=mDistanceCenter*y;
        float px1=(px0+px2)/2,py1=(py0+py2)/2;

        float r=(float) Math.sqrt(px1*px1+py1*py1);


        if(x>=0&&y>=0){//第一象限
            path.lineTo(x+offset,y+offset);
            float endX=getWidth()/2-getPaddingRight();
            float endY=y+offset;
            path.lineTo(endX,endY);
            canvas.drawPath(path,piePaint);
            canvas.drawText(text,endX-w,endY+h,textPaint);
            canvas.drawText(valueStr,endX-wV,endY-5,valuePaint);


            px1=px1-r*(float) Math.sin(Math.toRadians(45));
            py1=py1+r*(float)Math.cos(Math.toRadians(45));

        }
        if(x<0&&y>=0){//第二象限
            path.lineTo(x-offset,y+offset);

            float endX=-getWidth()/2+getPaddingLeft();
            float endY=y+offset;

            path.lineTo(endX,endY);
            canvas.drawPath(path,piePaint);

            canvas.drawText(text,endX,endY+h,textPaint);

            canvas.drawText(valueStr,endX,endY-5,valuePaint);


            px1=px1-r*(float) Math.sin(Math.toRadians(45));
            py1=py1-r*(float)Math.cos(Math.toRadians(45));

        }

        if(x<0&&y<0){//第三象限
            path.lineTo(x-offset,y-offset);

            float endX=-getWidth()/2+getPaddingLeft();
            float endY=y-offset;

            path.lineTo(endX,endY);
            canvas.drawPath(path,piePaint);

            canvas.drawText(text,endX,endY+h,textPaint);
            canvas.drawText(valueStr,endX,endY-5,valuePaint);

            px1=px1+r*(float) Math.sin(Math.toRadians(45));
            py1=py1-r*(float)Math.cos(Math.toRadians(45));

        }


        if(x>=0&&y<0){//第四象限
            path.lineTo(x+offset,y-offset);

            float endX=getWidth()/2-getPaddingRight();
            float endY=y-offset;

            path.lineTo(endX,endY);
            canvas.drawPath(path,piePaint);

            canvas.drawText(text,endX-w,endY+h,textPaint);

            canvas.drawText(valueStr,endX-wV,endY-5,valuePaint);

            px1=px1+r*(float) Math.sin(Math.toRadians(45));
            py1=py1+r*(float)Math.cos(Math.toRadians(45));
        }
        if(bigBitMap!=null&&needDrawAngle>=18){


            float quX=(1-animZeroToOne)*(1-animZeroToOne)*px0+2*animZeroToOne*(1-animZeroToOne)*px1+animZeroToOne*animZeroToOne*px2;
            float quY=(1-animZeroToOne)*(1-animZeroToOne)*py0+2*animZeroToOne*(1-animZeroToOne)*py1+animZeroToOne*animZeroToOne*py2;


            //canvas.drawLine(quX,quY,px2,py2,textPaint);

            Rect srcRect=new Rect(0,0,bigBitMap.getWidth(),bigBitMap.getHeight());
            RectF desRectF=new RectF(quX-bigBitMap.getWidth()/2,quY-bigBitMap.getHeight()/2,
                    quX+bigBitMap.getWidth()/2,quY+bigBitMap.getHeight()/2);
            canvas.drawBitmap(bigBitMap,srcRect,desRectF,bitmapPaint);
        }

    }


}
