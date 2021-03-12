package com.kcbs.webforum.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ScrollView;

public class ZhangPhilScrollView extends ScrollView {
    // 这个值控制可以把ScrollView包裹的控件拉出偏离顶部或底部的距离。
    private static final int MAX_OVERSCROLL_Y = 200;
    private int mOriginalHeight; //最初ImageView的高度
    private Context mContext;
    private int newMaxOverScrollY;

    public ZhangPhilScrollView(Context context) {
        super(context);

        init(context);
    }

    public ZhangPhilScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    /*
     * public ZhangPhilListView(Context context, AttributeSet attrs, int
     * defStyle) { super(context, attrs, defStyle); this.mContext = context;
     * init(); }
     */

    @SuppressLint("NewApi")
    private void init(Context context) {

        this.mContext = context;

        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float density = metrics.density;
        newMaxOverScrollY = (int) (density * MAX_OVERSCROLL_Y);

        //false:隐藏ScrollView的滚动条。
        this.setVerticalScrollBarEnabled(false);

        //不管装载的控件填充的数据是否满屏，都允许橡皮筋一样的弹性回弹。
        this.setOverScrollMode(ScrollView.OVER_SCROLL_ALWAYS);
    }

    // 最关键的地方。
    //支持到SDK8需要增加@SuppressLint("NewApi")。
    @SuppressLint("NewApi")
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        mOriginalHeight = ZhangPhilScrollView.this.getMeasuredHeight();//获取ImageView的初始高度
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX, newMaxOverScrollY,
                isTouchEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                // 执行回弹动画, 方式一: 属性动画\值动画
                //获取ImageView在松手时的高度
                int currHeight = ZhangPhilScrollView.this.getHeight();
                // 从当前高度mHeaderIv.getHeight(), 执行动画到原始高度mOriginalHeight
                ValueAnimator animator = ValueAnimator.ofInt(currHeight, mOriginalHeight);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        ZhangPhilScrollView.this.getLayoutParams().height = value;
                        //此方法必须调用,调用后会重新调用onMeasure和onLayout方法进行测量和定位
                        ZhangPhilScrollView.this.requestLayout();
                    }
                });
                animator.setDuration(500);
                animator.setInterpolator(new OvershootInterpolator());
                animator.start();

                //方式二,通过自定义动画
                /*ResetAnimation animation = new ResetAnimation(mHeaderIv, mHeaderIv.getHeight(), mOriginalHeight);
                startAnimation(animation);*/
                break;
        }
        return super.onTouchEvent(ev);
    }
}
