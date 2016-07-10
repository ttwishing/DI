package com.ttwishing.di;

import android.os.Bundle;
import android.widget.TextView;

import com.ttwishing.di.base.App;
import com.ttwishing.di.base.DIActivity;
import com.ttwishing.di.library.annotations.InjectView;

public class MainActivity extends DIActivity {

    /**
     * {@link com.ttwishing.di.base.App} 初始化时, this.diMaster.setSupportReflect(true); 更改为this.diMaster.setSupportReflect(false);
     */
//    @InjectView(R.id.text_1)
//    private TextView textView1;
//    @InjectView(value = R.id.text_2, resName = "text_2", resClass = TextView.class)
//    private TextView textView2;

    @InjectView
    protected TextView textView1;
    @InjectView
    protected TextView textView2;
    @InjectView
    protected TextView textView3;
    @InjectView
    protected TextView textView4;
    @InjectView
    protected TextView textView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1.setText("text1");
        textView2.setText("text2");
        textView3.setText("text3");

        /**
         * 确定MyManager为单例模式
         */
        MyManager myManager1 = App.getInstance(MyManager.class);
        textView4.setText("单例: " + myManager1.getKey());
        MyManager myManager2 = App.getInstance(MyManager.class);
        textView5.setText("单例: " + myManager2.getKey());

    }
}
