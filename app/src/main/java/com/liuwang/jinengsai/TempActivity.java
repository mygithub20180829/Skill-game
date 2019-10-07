package com.liuwang.jinengsai;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import cn.com.newland.nle_sdk.responseEntity.SensorInfo;
import cn.com.newland.nle_sdk.responseEntity.User;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class TempActivity extends Activity {

    private GifImageView mGifImageView;
    private GifDrawable gifDrawable;
    private Context mContext;
    private Switch aSwitch;
    private String AccessToken;
    private NetWorkBusiness mNetWorkBusiness;
    private int tem=0,highInt,lowInt;
    private TextView CurrentTemp,lowTemp,highTemp,CurrentSeekBarX,CurrentSeekBarY;
    private Button on,off,set_temp,dataResearch;
    private String Hightemp="",Lowtemp="";
    private SeekBar SeekBarX,SeekBarY;
    private boolean flag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        mContext=this;
        Bundle bundle = getIntent().getExtras();                                //得到传过来的bundle  
        AccessToken = bundle.getString("accessToken");
        mNetWorkBusiness=new NetWorkBusiness(AccessToken,"http://api.nlecloud.com:80/");

        initView();

        //风扇
        try{
            mGifImageView=findViewById(R.id.giv1);
            gifDrawable=new GifDrawable(getResources(),R.drawable.fan);
            mGifImageView.setImageDrawable(gifDrawable);//这里是实际决定资源的地方，优先级高于xml文件的资源定义
            gifDrawable.stop();
        }catch (IOException e){
            e.printStackTrace();
        }

        Thread thread=new Thread(new MyThread());
        thread.start();
    }

    private void initView(){
        aSwitch=findViewById(R.id.switch1);
        CurrentTemp=findViewById(R.id.temp);
        on=findViewById(R.id.open_fan);
        off=findViewById(R.id.close_fan);
        off.setEnabled(false);
        set_temp=findViewById(R.id.set_temp);
        lowTemp=findViewById(R.id.low_temp);
        highTemp=findViewById(R.id.high_temp);
        dataResearch=findViewById(R.id.data_research);
        CurrentSeekBarX=findViewById(R.id.CurrentSeekBarX);
        CurrentSeekBarY=findViewById(R.id.CurrentSeekBarY);
        SeekBarX = findViewById(R.id.seekBarX);    //水平拖动条
        SeekBarY = findViewById(R.id.seekBarY);

        SeekBarX.setVerticalScrollbarPosition(90);//垂直
        control("50509","nl_steeringdown","90");
        CurrentSeekBarX.setText("90");
        SeekBarX.setProgress(90);
        SeekBarY.setVerticalScrollbarPosition(90);//水平
        control("50509","nl_steeringup","90");
        CurrentSeekBarY.setText("90");
        SeekBarY.setProgress(90);

        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gifDrawable.start();
                Toast.makeText(mContext,"成功开启风扇",Toast.LENGTH_SHORT).show();
                sendTempDevice(1);
                off.setEnabled(true);
                aSwitch.setChecked(false);
            }
        });
        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gifDrawable.stop();
                Toast.makeText(mContext,"成功关闭风扇",Toast.LENGTH_SHORT).show();
                sendTempDevice(0);
                aSwitch.setChecked(false);
            }
        });
        set_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Hightemp=highTemp.getText().toString();
                try{
                    highInt=Integer.valueOf(Hightemp);//Float.parseFloat(Hightemp);
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
                Lowtemp=lowTemp.getText().toString();
                try{
                    lowInt=Integer.valueOf(Lowtemp);//Float.parseFloat(Lowtemp);
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
                Toast.makeText(mContext,"温度设置成功",Toast.LENGTH_SHORT).show();
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    set_temp.setEnabled(true);
                    autoControl();
                    on.setEnabled(false);
                    off.setEnabled(false);
                }else {
                    on.setEnabled(true);
                    off.setEnabled(true);
                    set_temp.setEnabled(false);
                    Toast.makeText(mContext,"当前模式:手动",Toast.LENGTH_SHORT).show();

                }
            }
        });
        dataResearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TempDataActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("highInt",highInt);
                bundle.putInt("lowInt",lowInt);
                bundle.putString("AccessToken", AccessToken);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        SeekBarX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //获取值..
                String a = Integer.toString(i);
                CurrentSeekBarX.setText(a);
                control("50509","nl_steeringup",a);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBarY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String a = Integer.toString(i);
                CurrentSeekBarY.setText(a);
                control("50509","nl_steeringdown",a);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void control(String id,String apiTag,Object value){
        //设备id,标识符,值.
        mNetWorkBusiness.control(id, apiTag, value, new NCallBack<BaseResponseEntity>(mContext) {
            @Override
            protected void onResponse(BaseResponseEntity response) {

            }

            @Override
            public void onResponse(@NonNull Call<BaseResponseEntity> call,@NonNull Response<BaseResponseEntity> response) {
                BaseResponseEntity<User> baseResponseEntity = response.body();  //获得返回体
                if (baseResponseEntity==null){
                    Toast.makeText(mContext,"请求内容为空",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BaseResponseEntity> call, Throwable t) {
                Toast.makeText(mContext,"请求出错 " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void autoControl(){
        if ((Lowtemp.equals(""))||(Hightemp.equals(""))){
            aSwitch.setChecked(false);
            Toast.makeText(mContext,"没有温度的上下限值，请前往设置",Toast.LENGTH_SHORT).show();
        }
        else {
            aSwitch.setChecked(true);
            Toast.makeText(mContext,"当前模式:自动",Toast.LENGTH_SHORT).show();
            if (tem < lowInt) {
                gifDrawable.stop();
                sendTempDevice(0);
                Toast.makeText(mContext, "当前温度低于设定的温度，已为您关闭风扇", Toast.LENGTH_SHORT).show();
            } else if (tem > highInt) {
                gifDrawable.start();
                sendTempDevice(1);
                Toast.makeText(mContext, "当前温度高于设定的温度，已为您打开风扇", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendTempDevice(Object data){
        mNetWorkBusiness.control("50509", "nl_fan", data, new NCallBack<BaseResponseEntity>(mContext) {
            @Override
            protected void onResponse(BaseResponseEntity response) {

            }

            @Override
            public void onResponse(Call<BaseResponseEntity> call, Response<BaseResponseEntity> response) {
                BaseResponseEntity<User> baseResponseEntity = response.body();  //获得返回体
                if (baseResponseEntity==null){
                    Toast.makeText(mContext,"请求内容为空",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponseEntity> call, Throwable t) {
                Toast.makeText(mContext,"请求出错 " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    class MyThread implements Runnable{

        @Override
        public void run() {
            Looper.prepare();
            while (true){
                //显示温度
                ShowTemp();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ShowTemp();
                    }
                },3000);
            }
        }

        private void ShowTemp(){
            mNetWorkBusiness.getSensor("50509", "nl_temperature", new NCallBack<BaseResponseEntity<SensorInfo>>(mContext) {
                @Override
                protected void onResponse(BaseResponseEntity<SensorInfo> response) {

                }

                @Override
                public void onResponse(Call<BaseResponseEntity<SensorInfo>> call, Response<BaseResponseEntity<SensorInfo>> response) {
                    BaseResponseEntity baseResponseEntity=response.body();
                    if (baseResponseEntity!=null){
                        //获取到了内容,使用json解析.
                        //JSON 是一种文本形式的数据交换格式，它比XML更轻量、比二进制容易阅读和编写，调式也更加方便;解析和生成的方式很多，Java中最常用的类库有：JSON-Java、Gson、Jackson、FastJson等
                        final Gson gson=new Gson();
                        JSONObject jsonObject=null;
                        String msg=gson.toJson(baseResponseEntity);
                        try {
                            jsonObject = new JSONObject(msg);   //解析数据.
                            JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
                            tem=resultObj.getInt("Value");
                            CurrentTemp.setText(""+tem);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(mContext,"失败",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}


