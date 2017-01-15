package hook.sx.com.systemcontroladdview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsw8569013 on 2017/1/14.
 *
 *  #link blog.csdn.com/lsw8569013
 */
public class MainActivity extends Activity {

    private BouncingMenuUtil boucingMenu;
    private View show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show =  findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boucingMenu != null && boucingMenu.ishow()) {
                    boucingMenu.dismiss();
                } else {
                    List<String> ll = new ArrayList<>();
                    for (int i = 0; i < 50; i++) {
                        ll.add("item" + i);
                    }
                    MyRecyclerAdapter adapter = new MyRecyclerAdapter(ll);
                    boucingMenu = BouncingMenuUtil.makeMenu(findViewById(R.id.rl), R.layout.layout_rv_sweet, adapter, 1).show();
                }
            }
        });

        findViewById(R.id.show2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLinearAnimator();
            }
        });
        
        findViewById(R.id.show3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFaceBookRebond();
            }
        });
        findViewById(R.id.show4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showView(4);
            }
        });
    }

    private void showFaceBookRebond() {
        showView(3);
    }

    public  void showLinearAnimator(){
        showView(2);
    }

    private void showView(int type) {
        List<String> ll = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            ll.add("item" + i);
        }
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(ll);
        boucingMenu = BouncingMenuUtil.makeMenu(findViewById(R.id.rl), R.layout.layout_rv_sweet, adapter,type).show();
    }

}
