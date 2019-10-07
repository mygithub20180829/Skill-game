package com.liuwang.jinengsai;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.com.newland.nle_sdk.requestEntity.SignIn;
import cn.com.newland.nle_sdk.responseEntity.User;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etUserName;
    private EditText etPwd;
    private Context mContext;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext=this;
        etUserName=findViewById(R.id.name);
        etPwd=findViewById(R.id.password);
        login=findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });
    }

    private void SignIn(){
        final String userName = etUserName.getText().toString();
        final String pwd = etPwd.getText().toString();
        if (userName.equals("") || pwd.equals("")){
            Toast.makeText(mContext,"账号或密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        final NetWorkBusiness netWorkBusiness=new NetWorkBusiness("","http://api.nlecloud.com:80/");
        netWorkBusiness.signIn(new SignIn(userName, pwd), new NCallBack<BaseResponseEntity<User>>(mContext) {
            @Override
            protected void onResponse(BaseResponseEntity<User> response) {

            }

            @Override
            public void onResponse(Call<BaseResponseEntity<User>> call, Response<BaseResponseEntity<User>> response) {
                BaseResponseEntity<User> baseResponseEntity = response.body();	//获得响应体
                if (baseResponseEntity != null) {
                    if (baseResponseEntity.getStatus() == 0) {
                        //需要传输秘钥
                        String accessToken = baseResponseEntity.getResultObj().getAccessToken();        //json数据返回
                        Intent intent = new Intent(mContext, TempActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("accessToken", accessToken);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(mContext, baseResponseEntity.getMsg(), Toast.LENGTH_SHORT).show();  //返回为空...
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponseEntity<User>> call, Throwable t) {
                Toast.makeText(mContext,"登录失败 " + t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
