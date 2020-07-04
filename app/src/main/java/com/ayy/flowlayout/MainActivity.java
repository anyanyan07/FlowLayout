package com.ayy.flowlayout;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author anyanyan
 */
public class MainActivity extends AppCompatActivity {
    private FlowLayout flowLayout;
    private String[] txt = {"热门搜索", "哈哈", "大家好", "2020年", "万事如意，事事顺心"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flowLayout = findViewById(R.id.flow_layout);
        for (int i = 0; i < 20; i++) {
            TextView textView = new TextView(this);
            FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            if (i == 9) {
                layoutParams.height = 120;
            }
            textView.setLayoutParams(layoutParams);

            textView.setPadding(20, 10, 20, 10);
            textView.setBackgroundResource(R.drawable.item_bg);
            textView.setGravity(Gravity.CENTER_VERTICAL);

            textView.setText(txt[i % txt.length]);
            flowLayout.addView(textView);
        }
    }
}
