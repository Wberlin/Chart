package com.wubolin.widget.chart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wubolin.widget.chart.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by Administrator on 2017/12/5.
 */

public class BrokenLineView extends View {
    private static String TAG=BrokenLineView.class.getSimpleName();
    //需要绘制的点的个数
    private int pointCount=8;
    //纵坐标上 的等分数
    private int partCount=40;



    //左右的间距
    private float leftOrRightSpacing=40;
    //字体大小
    private float textSize=30;
    //最大点半径
    private float maxRadius=12;
    //最小点半径
    private float minRadius=8;
    //线宽度
    private float lineWidth=6;

    //每个点之间的宽度间隔
    private int spacingW;
    //每个点之间的高度间隔
    private int spacingH;



    //绘制图像的画笔
    private Paint brokenLinePaint;
    //绘制文字的画笔
    private Paint textPaint;
    //绘制虚线的画笔
    private Paint dashedPaint;
    //用于绘制虚线的路径
    private Path dashedPath;






    //效果
    private PathEffect effects;


    //最大的值标记线条颜色
    private int maxIndex;






    private int orangeColor;
    private int grayColor;

    //控件固定的宽度和高度
    private static int mWidth=720;
    private static int mHeight=400;


    private List<BrokenLineModel> brokenLineModels;


    public BrokenLineView(Context context) {
        this(context, null);
    }

    public BrokenLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }



    public BrokenLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }
    private void setup(AttributeSet attrs) {
        leftOrRightSpacing=getResources().getDimension(R.dimen.leftOrRightSpacing);
        textSize=getResources().getDimension(R.dimen.text_size);
        minRadius=getResources().getDimension(R.dimen.circle_radius);
        maxRadius=getResources().getDimension(R.dimen.circle_radius_max);
        lineWidth=getResources().getDimension(R.dimen.line_width);


        orangeColor=getResources().getColor(R.color.orange);
        grayColor=getResources().getColor(R.color.text_color_01);
        brokenLinePaint=new Paint();
        brokenLinePaint.setStrokeWidth(2);
        brokenLinePaint.setAntiAlias(true);
        textPaint=new Paint();
        textPaint.setStrokeWidth(2);
        textPaint.setAntiAlias(true);


        dashedPaint=new Paint();
        dashedPaint.setAntiAlias(true);
        dashedPaint.setStyle(Paint.Style.STROKE);
        dashedPaint.setStrokeWidth(5);
        dashedPaint.setColor(grayColor);

        dashedPath=new Path();
        //画一段5的实线，画一段5的空白，以此类推
        effects = new DashPathEffect(new float[]{5,5,5,5},0);



        brokenLineModels=new ArrayList<>();
        for(int i=0;i<15;i++){
            BrokenLineModel model=new BrokenLineModel();
            model.setDownDate((i+1)+"");
            float up=new Random().nextInt(20);
            model.setUpDate(up);
            brokenLineModels.add(model);
        }
        pointCount=brokenLineModels.size();
        float max=0;
        for(int i=0;i<brokenLineModels.size();i++){
            if(brokenLineModels.get(i).getUpDate()>max){
                max=brokenLineModels.get(i).getUpDate();
                maxIndex=i;
            }
        }
    }


    //设置数据
    public void setData(List<BrokenLineModel> modelList){


        brokenLineModels=modelList;

        float max=0;
        for(int i=0;i<brokenLineModels.size();i++){
            if(brokenLineModels.get(i).getUpDate()>max){
                max=brokenLineModels.get(i).getUpDate();
                maxIndex=i;
            }
        }
        invalidate();
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







    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w=getWidth();
        int h=getHeight();
        spacingW=(w-(int)leftOrRightSpacing*2)/(pointCount-1);
        spacingH=(int)(h*0.5/partCount);
        brokenLinePaint.setColor(orangeColor);
        textPaint.setColor(grayColor);

        brokenLinePaint.setAlpha(150);
        //绘制首条线
        canvas.drawLine(0,h/2,leftOrRightSpacing,h/2-spacingH*(int)(brokenLineModels.get(0).getUpDate()*1.0/50*partCount),brokenLinePaint);
        brokenLinePaint.setAlpha(255);
        //绘制虚线
        dashedPath.reset();
        dashedPath.moveTo(leftOrRightSpacing+(pointCount-1)*spacingW, h/2-spacingH*(int)(brokenLineModels.get(pointCount-1).getUpDate()*1.0/50*partCount));
        dashedPath.lineTo(w,h/2-spacingH*(int)(brokenLineModels.get(pointCount-1).getUpDate()*1.0/50*partCount));



        dashedPaint.setPathEffect(effects);
        canvas.drawPath(dashedPath,dashedPaint);

        for(int i=0;i<pointCount;i++){
            int indexH=spacingH*(int)(brokenLineModels.get(i).getUpDate()*1.0/50*partCount);

            if(i==maxIndex){
                canvas.drawCircle(leftOrRightSpacing+i*spacingW,h/2-indexH,maxRadius,brokenLinePaint);
            }else
                canvas.drawCircle(leftOrRightSpacing+i*spacingW,h/2-indexH,minRadius,brokenLinePaint);


        }

        brokenLinePaint.setStrokeWidth(lineWidth);

        for(int i=0;i<pointCount-1;i++){
            int indexH=spacingH*(int)(brokenLineModels.get(i).getUpDate()*1.0/50*partCount);
            int indexHH=spacingH*(int)(brokenLineModels.get(i+1).getUpDate()*1.0/50*partCount);

            canvas.drawLine(leftOrRightSpacing+i*spacingW,h/2-indexH,leftOrRightSpacing+(i+1)*spacingW,h/2-indexHH,brokenLinePaint);
        }





        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);








        //绘制底部文字
        for(int i=0;i<pointCount;i++){
            canvas.drawText(brokenLineModels.get(i).getDownDate(), leftOrRightSpacing+i*spacingW, h/2+60 , textPaint);
        }
        textPaint.setColor(grayColor);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        brokenLinePaint.setTextSize(textSize);
        brokenLinePaint.setTextAlign(Paint.Align.CENTER);




        //绘制顶部文字
        for(int i=0;i<pointCount;i++){
            int indexH=spacingH*(int)(brokenLineModels.get(i).getUpDate()*1.0/50*partCount);
            if(i==maxIndex){
                canvas.drawText((int)brokenLineModels.get(i).getUpDate()+"",leftOrRightSpacing+i*spacingW,h/2-indexH-30,brokenLinePaint);
            }else
                canvas.drawText((int)brokenLineModels.get(i).getUpDate()+"",leftOrRightSpacing+i*spacingW,h/2-indexH-30,textPaint);
        }


    }




}
