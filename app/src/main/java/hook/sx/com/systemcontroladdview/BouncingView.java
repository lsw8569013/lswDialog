package hook.sx.com.systemcontroladdview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

/**
 * Created by  lsw8569013 on 2017/1/5.
 *
 * 自定义控件
 */
public class BouncingView extends View {
    private Paint mPaint;
    //变化的过程当中当前弧度的高度mArcHeight
    private int mArcHeight;//当前的弧高
    private int mMaxArcHright;//狐高最大高度
    private States mstate = States.NONE;//状态
    private Path path = new Path();//需要绘制的路径
    private AnimationListener animationListener;//回调接口
    int showARC = 1;
    int showLinear = 2;
    int faceBook = 3;
    private int showBounce = 4;
    private int type;

    public void setType(int i) {
        type = i;
    }


    public enum States {
        NONE,
        STATUS_SMOOTH_UP,
        STATUS_DOWN,
    }
    public BouncingView(Context context) {
        super(context);
        init();
    }

    public BouncingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BouncingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化颜色和弧度的最大值
     */
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(android.R.color.white));
        mMaxArcHright = getResources().getDimensionPixelSize(R.dimen.arc_max_height);
    }

    /**
     * 这个就不用说了吧   主要说下drawBG方法吧
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBG(canvas);
    }

    /**
     * 先看下这个方法
     * @param canvas
     * 看完是不是想问是这比就修改了一次么？看到这里确实是修改了一次，但是我多调用几次不久好了么
     * 所以我再show方法里面对控件的动画进行了监听啊   在AnimatorUpdateListener方法里面不断的调用了系统更新的方法啊
     * 所以系统就会不断的调用onDraw方法。。。。。
     */
    private void drawBG(Canvas canvas) {
        int currentPointY = 0;
        path.reset();
        //计算--不断的变化的高度
        switch (mstate){
            case NONE:
                currentPointY = 0;
                break;
            case STATUS_SMOOTH_UP:
                //currentPointY值--- 跟mArcHeighr的变化率差不多一样
                currentPointY = (int)(getHeight()*(1-(float)mArcHeight/mMaxArcHright)+mMaxArcHright);
            break;
            case STATUS_DOWN:
                currentPointY = mMaxArcHright;
                break;
        }

        path.moveTo(0,currentPointY);
        if(type == showARC){
            path.quadTo(getWidth()/2,currentPointY-mArcHeight,getWidth(),currentPointY);
        }else {
            path.lineTo(getWidth(),currentPointY);
        }
        path.lineTo(getWidth(),getHeight());
        path.lineTo(0,getHeight());
        path.close();
        canvas.drawPath(path,mPaint);
    }

    /**
     * 不过多解释了
     */
    public void show() {
        mstate = States.STATUS_SMOOTH_UP;
        //这个就要是让RecyclerView进行显示数据的
        if (animationListener!=null){
            animationListener.onSrart();
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //显示数据
                    animationListener.onContentShow();
                }
            },400);
        }
        if(type == faceBook){
            SpringSystem mSpringSystem =   SpringSystem.create();
//         SpringConfig springConfig = SpringConfig.fromOrigamiTensionAndFriction(40, 3);
            SpringConfig springConfig = SpringConfig.fromBouncinessAndSpeed(18, 15);

            Spring spring = mSpringSystem
                    .createSpring()
                    .setSpringConfig(springConfig)
                    .addListener(new SimpleSpringListener() {
                        @Override
                        public void onSpringUpdate(Spring spring) {
                            float value = (float) spring.getCurrentValue();
                            mArcHeight = (int) (value * mMaxArcHright);
                            invalidate();
                        }
                    });

            spring.setEndValue(1);
        }else {
            ValueAnimator value = ValueAnimator.ofInt(0, mMaxArcHright);
            value.setDuration(500);
            value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mArcHeight = (int) animation.getAnimatedValue();
                    if (mArcHeight == mMaxArcHright) {
                        //弹一下
                        bounce();
                    }
                    invalidate();
                }
            });
            if(type == showARC ){
                value.setInterpolator(new AccelerateDecelerateInterpolator());//插值器(先快后慢)

            }else if(type == showLinear){
                value.setInterpolator(new DumpingInterpolator());//插值器(自己实现的弹簧差值器)
            }else if(type == showBounce){
                value.setInterpolator(new BounceInterpolator());//插值器(自由落体)

            }
            value.start();
        }

    }

    /**
     * 回弹动画
     */
    public void bounce(){
        mstate = States.STATUS_DOWN;
            ValueAnimator value = ValueAnimator.ofInt( mMaxArcHright,0);
            value.setDuration(500);
            value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mArcHeight = (int) animation.getAnimatedValue();//弧度不断减小 再不断更新 就会出现弹回去的效果了
                    invalidate();
                }
            });
            value.setInterpolator(new AccelerateDecelerateInterpolator());
            value.start();

    }

    public void setCallback(AnimationListener animationListener){
        this.animationListener = animationListener;
    }
    public interface  AnimationListener{
        void  onSrart();
        void onEnd();
        void onContentShow();
    }

    /**
     *
     */
    class DumpingInterpolator implements Interpolator {


        @Override
        public float getInterpolation(float input) {
            return (float) (1-Math.exp( -3 * input) * Math.cos(10 * input) );
        }
    }
}
