package com.liuwang.jinengsai;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TestActivity extends Activity {



        private LinearLayout mainLinerLayout;
        private RelativeLayout relativeLayout;
        private String[] name={"序号","考号","姓名","出生年月","语文","数学","英语"};

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_test);
            mainLinerLayout = (LinearLayout) this.findViewById(R.id.MyTable);
            initData();
        }

        //绑定数据
        private void initData() {
            //初始化标题
            relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.testtable, null);
            MyTableTextView title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_1);
            title.setText(name[0]);
            title.setTextColor(Color.BLUE);

            title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_2);
            title.setText(name[1]);
            title.setTextColor(Color.BLUE);
            title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_3);
            title.setText(name[2]);
            title.setTextColor(Color.BLUE);
            title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_4);
            title.setText(name[3]);
            title.setTextColor(Color.BLUE);
            title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_5);
            title.setText(name[4]);
            title.setTextColor(Color.BLUE);
            title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_6);
            title.setText(name[5]);
            title.setTextColor(Color.BLUE);
            title = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_7);
            title.setText(name[6]);
            title.setTextColor(Color.BLUE);
            mainLinerLayout.addView(relativeLayout);

            //初始化内容
            int number = 1;
            for (int i=0;i<10;i++) {
                relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.testtable, null);
                MyTableTextView txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_1);
                txt.setText(String.valueOf(number));

                txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_2);
                txt.setText("320321**********35");
                txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_3);
                txt.setText("张三");
                txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_4);
                txt.setText("1992/04/21");
                txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_5);
                txt.setText("150");
                txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_6);
                txt.setText("200");
                txt = (MyTableTextView) relativeLayout.findViewById(R.id.list_1_7);
                txt.setText("120");
                mainLinerLayout.addView(relativeLayout);
                number++;
            }
        }

}
