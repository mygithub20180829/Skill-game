package com.liuwang.jinengsai;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.com.newland.nle_sdk.responseEntity.SensorDataPageDTO;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import cn.com.newland.nle_sdk.util.Tools;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TempDataActivity extends BaseActivity {
    private Context mContext;
    private String AccessToken,mJsonData="";
    private NetWorkBusiness mNetWorkBusiness;
    private Button startRecord,stopRecord,firstPage,upPage,downPage,endPage;
    private static String TAG = "TempDataActivity",recordTime;
    private int value,number=0;

    private int[] idData=new int[30];
    private int[] valueData=new int[30];
    private String[] recordTimeData=new String[30];

    private LinearLayout mainLinerLayout;
    private RelativeLayout relativeLayout;
    private String[] name = {"设备ID", "温度(℃)", "时间"};

    private double highInt,lowInt;
    private List<Integer> count=new ArrayList<>();
    private List<String> countTime=new ArrayList<>();
    private int nowpage=1;
    private TextView allCount,maxTempture,minTempture;


    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_data);


        mContext=this;
        Bundle bundle = getIntent().getExtras();                                //得到传过来的bundle  
        AccessToken = bundle.getString("AccessToken");
        highInt=bundle.getInt("highInt");
        lowInt=bundle.getInt("lowInt");
        mNetWorkBusiness=new NetWorkBusiness(AccessToken,"http://api.nlecloud.com:80/");

        dbHelper = new MyDatabaseHelper(this, "16204204.db", null, 1);
        dbHelper.getWritableDatabase();

        mainLinerLayout=this.findViewById(R.id.MyTable);
        relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
        initView();
    }

    private void initView() {
        startRecord=findViewById(R.id.record_start);
        stopRecord=findViewById(R.id.record_stop);
        firstPage=findViewById(R.id.first_page);
        upPage=findViewById(R.id.up_page);
        downPage=findViewById(R.id.down_page);
        endPage=findViewById(R.id.end_page);
        allCount=findViewById(R.id.all_count);
        maxTempture=findViewById(R.id.history_maxtempture);
        minTempture=findViewById(R.id.history_mintempture);

        upPage.setEnabled(false);
        nowpage = 1;
        firstPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"当前已是首页",Toast.LENGTH_SHORT).show();
                downPage.setEnabled(true);
                upPage.setEnabled(false);
                nowpage=1;
                initData(1);
//                Toast.makeText(mContext,""+nowpage,Toast.LENGTH_SHORT).show();
            }
        });
        upPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downPage.setEnabled(true);
                nowpage --;
                initData(nowpage);
                if(nowpage <= 1){
                    nowpage = 1;
                    initData(nowpage);
                    upPage.setEnabled(false);
                }
//                Toast.makeText(mContext,""+nowpage,Toast.LENGTH_SHORT).show();

            }
        });
        downPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upPage.setEnabled(true);
                nowpage++;
                initData(nowpage);
                if(nowpage >= 3){
                    nowpage = 3;
                    initData(nowpage);
                    downPage.setEnabled(false);
                }
//                Toast.makeText(mContext,""+nowpage,Toast.LENGTH_SHORT).show();
            }
        });
        endPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nowpage = 3;
                downPage.setEnabled(false);
                upPage.setEnabled(true);
                initData(3);
//                Toast.makeText(mContext,""+nowpage,Toast.LENGTH_SHORT).show();
            }
        });

        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTemp();
                initData(1);
            }
        });
        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void getTemp(){
        mNetWorkBusiness.getSensorData("50509", "nl_temperature", "3", "1", "", "",
                "DESC", "20", "", new NCallBack<BaseResponseEntity<SensorDataPageDTO>>(mContext) {
                    @Override
                    protected void onResponse(BaseResponseEntity<SensorDataPageDTO> response) {

                    }

                    @Override
                    public void onResponse(Call<BaseResponseEntity<SensorDataPageDTO>> call, Response<BaseResponseEntity<SensorDataPageDTO>> response) {
                        final Gson gson = new Gson();
                        BaseResponseEntity baseResponseEntity = response.body();
                        Log.d("TAG", "queryDatas, gson.toJson(baseResponseEntity):" + gson.toJson(baseResponseEntity));
                        if (baseResponseEntity != null) {
                            mJsonData=gson.toJson(baseResponseEntity);
                        } else {
                            Toast.makeText(mContext,"请求出错 : 请求参数不合法或者服务出错",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    //绑定数据
    private void initData(int nowPage) {
        //初始化标题
        relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
        TableTextView title = relativeLayout.findViewById(R.id.list_1_1);
        title.setText(name[0]);
        title.setTextColor(Color.BLUE);

        title = relativeLayout.findViewById(R.id.list_1_2);
        title.setText(name[1]);
        title.setTextColor(Color.BLUE);
        title = relativeLayout.findViewById(R.id.list_1_3);
        title.setText(name[2]);
        title.setTextColor(Color.BLUE);
        mainLinerLayout.addView(relativeLayout);

        //将数据加载到数据库里
        addData();
        //将数据以图表形式显示
        showData(nowPage);


//        JSONObject jsonObject = null;
//        try {
//            jsonObject = new JSONObject(mJsonData);
//            JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
//            JSONArray dataPoints = (JSONArray) resultObj.getJSONArray("DataPoints");
//            Log.d(TAG, "dataPoints size:" + dataPoints.length());
//            Log.d(TAG, "dataPoints:" + dataPoints);
//
//            JSONArray pointDTO = (JSONArray) dataPoints.getJSONObject(0).getJSONArray("PointDTO");
//            Log.d(TAG, "pointDTO size:" + pointDTO.length());
//            Log.d(TAG, "pointDTO:" + pointDTO);
//            for (int i = 0; i < pointDTO.length(); i++) {
//                JSONObject subObject = pointDTO.getJSONObject(i);
//                value = subObject.getInt("Value");
//                recordTime = (String) subObject.get("RecordTime");
//                relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
//                count.add(value);
//                countTime.add(recordTime);
//            }
//            //实现list集合逆序排列
//            Collections.reverse(count);
//            Collections.reverse(countTime);
//            //将数据加载到数据库里
//            addData();
//            //将数据以图表形式显示
//            showData();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void addData(){
        //添加数据
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values =new ContentValues();
        for (int i=0;i</*count.size()*/30;i++){
//            values.put("Id",50509);
//            values.put("tempture",count.get(i));
//            values.put("gettime",countTime.get(i));
            values.put("Id",50509+(i++));
            values.put("tempture",23);
            values.put("gettime","2017-03-06");
            db.insert("usertable",null,values);
            values.clear();     //第i条
        }
    }

    private void showData(int nowPage){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor cursor = db.query("usertable", new String[]{"Id,tempture,gettime"},
//                "tempture > "+highInt+" and tempture<"+lowInt, null, null, null, "Id desc");//, "1,2"

        Cursor cursor = db.query("usertable", new String[]{"Id,tempture,gettime"},
                "tempture == 23", null, null, null, "Id desc");//, "1,2"


//        db.query("table","string[] columns","string selection","stirng[] selectoinArgs","groupby","having","orderby");
        while (cursor.moveToNext()) {
            int Id = cursor.getInt(0); //获取第一列的值,第一列的索引从0开始
            int tempture = cursor.getInt(1);//获取第二列的值
            String gettime = cursor.getString(2);//获取第三列的值

            if (number<30){
                idData[number]=Id;
                valueData[number]=tempture;
                recordTimeData[number]=gettime;
                number++;
            }
        }
        cursor.close();
        db.close();

        //显示历史最高值和历史最低值
        int max,min;
        max=min=valueData[0];
        for (int i=0;i<30;i++){
            if (valueData[i]>=max){
                max=valueData[i];
            }
            if (valueData[i]<=min){
                min=valueData[i];
            }
        }
        maxTempture.setText(""+max);
        minTempture.setText(""+min);

        allCount.setText("30");
//        TableTextView txt;
        if (nowPage==1){
            mainLinerLayout.removeAllViews();
            //初始化标题
            relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
            TableTextView title = relativeLayout.findViewById(R.id.list_1_1);
            title.setText(name[0]);
            title.setTextColor(Color.BLUE);

            title = relativeLayout.findViewById(R.id.list_1_2);
            title.setText(name[1]);
            title.setTextColor(Color.BLUE);
            title = relativeLayout.findViewById(R.id.list_1_3);
            title.setText(name[2]);
            title.setTextColor(Color.BLUE);
            mainLinerLayout.addView(relativeLayout);
            for (int j=0;j<10;j++){
                relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
                TableTextView txt = relativeLayout.findViewById(R.id.list_1_1);
                txt.setText(String.valueOf(idData[j]));

                txt = relativeLayout.findViewById(R.id.list_1_2);
                txt.setText(String.valueOf(valueData[j]));

                txt = relativeLayout.findViewById(R.id.list_1_3);
                txt.setText(recordTimeData[j]);
                mainLinerLayout.addView(relativeLayout);
            }
        }else if (nowPage==2){
            mainLinerLayout.removeViews(0,11);
//            mainLinerLayout.removeView(relativeLayout);
            for (int j=10;j<20;j++){
                relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
                TableTextView txt = relativeLayout.findViewById(R.id.list_1_1);
                txt.setText(String.valueOf(idData[j]));

                txt = relativeLayout.findViewById(R.id.list_1_2);
                txt.setText(String.valueOf(valueData[j]));

                txt = relativeLayout.findViewById(R.id.list_1_3);
                txt.setText(recordTimeData[j]);

                mainLinerLayout.addView(relativeLayout);
            }
        }else if (nowPage==3){
            mainLinerLayout.removeViews(0,11);
//            mainLinerLayout.removeView(relativeLayout);
            for (int j=20;j<30;j++){
                relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
                TableTextView txt = relativeLayout.findViewById(R.id.list_1_1);
                txt.setText(String.valueOf(idData[j]));

                txt = relativeLayout.findViewById(R.id.list_1_2);
                txt.setText(String.valueOf(valueData[j]));

                txt = relativeLayout.findViewById(R.id.list_1_3);
                txt.setText(recordTimeData[j]);
                mainLinerLayout.addView(relativeLayout);
            }
        }

    }

}

