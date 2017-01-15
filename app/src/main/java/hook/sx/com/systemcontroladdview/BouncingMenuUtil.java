package hook.sx.com.systemcontroladdview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 *
 * Created by 宋鑫 on 2017/1/5.
 * modify by lsw8569013
 * 工具类
 */
public class BouncingMenuUtil {
    private final View lsw_dialog;
    private ViewGroup mPatentVG;//这里就是顶级容器FragmeLayout
    private View rootView;//我们自己的布局文件(layout_rv_sweet.xml)
    private BouncingView bouncingv;//自己的自定义控件
    private RecyclerView recyclerView;//显示数据的RecyclerView
    private MyRecyclerAdapter adapter;


    private BouncingMenuUtil(View v, int resId, MyRecyclerAdapter adapter, int i) {
        this.adapter = adapter;
        //1.找到系统里的FragmentLayout
        mPatentVG = findrootParent(v);
        //渲染布局
        rootView = LayoutInflater.from(v.getContext()).inflate(resId, null, false);
        bouncingv = (BouncingView) rootView.findViewById(R.id.sv);
        bouncingv.setCallback(new MyAnimationListener());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        bouncingv.setType(i);
        lsw_dialog = rootView.findViewById(R.id.lsw_dialog);

        lsw_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lsw_dialog.setBackgroundColor(0x00000000);
                dismiss();
            }
        });
    }

    /**
     * 调用这个方法返回一个BouncingMenuUtil对象
     * @param v    传进来的自己的布局文件的最大的父容器，然后以这个容器为锚点向上查找，找到上一层的id为content的FrahmeLayout
     * @param resId     自己布局文件的id
     * @param adapter   这个就不用说了 就是制造好添加好数据的adapter
     * @param i
     * @return
     */
    public static BouncingMenuUtil makeMenu(View v, int resId, MyRecyclerAdapter adapter, int i) {
        return new BouncingMenuUtil(v, resId, adapter,i);
    }

    /**
     * 这个方法就是查找id为content的Fragment
     * 首先判断是不是FragmentLayout如果是的话判断id   id正确就说明找到了 结束方法并返回
     * @param v
     * @return
     */
    private ViewGroup findrootParent(View v) {
        do {
            if (v instanceof FrameLayout) {
                if (v.getId() == android.R.id.content)
                    return (ViewGroup) v;
            }
            if (v != null) {
                ViewParent viewgp = v.getParent();
                v = viewgp instanceof View ? (View) viewgp : null;
            }
        } while (v != null);
        return null;
    }

    public BouncingMenuUtil show() {
        //2.王帧布局里面添加自定义控件
        if (rootView.getParent() != null) {
            //先将原来有的删除掉  再进行添加
            mPatentVG.removeView(rootView);
        }
        //将自己的布局传入
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
        mPatentVG.addView(rootView, lp);
        //3.开始动画
        bouncingv.show();
        return this;
    }

    public void dismiss() {
        //消失就只用了一个位移动画，将控件移出屏幕之后然后再将控件销毁，避免内存泄漏
        Log.d("sssss", "dismiss1111");
        ObjectAnimator an = ObjectAnimator.ofFloat(rootView, "translationY", 0, rootView.getHeight());
        an.setDuration(600);
        an.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mPatentVG.removeView(rootView);
                rootView = null;
            }
        });
        an.start();
    }

    public boolean ishow() {
        if (rootView == null) {
            return false;
        }
        return true;
    }

    /**
     * 回掉实现回调接口，主要是为了给recyclerView设置适配器
     */
    class MyAnimationListener implements BouncingView.AnimationListener {
        @Override
        public void onSrart() {
        }
        @Override
        public void onEnd() {
        }
        @Override
        public void onContentShow() {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
            recyclerView.scheduleLayoutAnimation();


            lsw_dialog.setBackgroundColor(0x66000000);
        }
    }

    private  void setBounceContentView() {

    }
}
