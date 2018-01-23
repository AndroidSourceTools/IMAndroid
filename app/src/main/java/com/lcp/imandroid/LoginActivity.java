package com.lcp.imandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lcp.imandroid.model.LoginResult;
import com.lcp.imandroid.util.Constant;
import com.lcp.imandroid.util.PreferenceUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.name)
    EditText editName;
    @BindView(R.id.password)
    EditText editPass;
    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
    }
    private void init(){
        editName.setText(PreferenceUtil.getUserName(this));
        editPass.setText(PreferenceUtil.getPassword(this));
    }

    @OnClick({R.id.btn_register, R.id.btn_sign_in})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                signin(true);
                break;
            case R.id.btn_sign_in:
                signin(false);
                break;
        }
    }

    private void signin(boolean register) {
        String name = editName.getText().toString().trim();
        String pass = editPass.getText().toString().trim();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pass)) {
            getCall(register, name, pass).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, register ? "注册失败" : "登录失败", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String body=response.body().string();
                    Log.i(TAG,body);
                    LoginResult result = new LoginResult();
                    try {
                        result = new Gson().fromJson(body, LoginResult.class);
                    } catch (Exception ignored) {
                        Log.i(TAG, ignored.getMessage());
                    } finally {
                        final LoginResult finalResult = result;
                        runOnUiThread(() -> {
                            String msg = finalResult.getMessage();
                            if (TextUtils.isEmpty(msg))
                                msg = register ? "注册失败" : "登录失败";
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            if (finalResult.getCode() == 1) {
                                IMApplication.setMe(finalResult.getUser());
                                PreferenceUtil.saveUser(finalResult.getUser(),LoginActivity.this);
                                new Handler().postDelayed(() -> {

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }, 2000);
                            }

                        });

                    }
                }
            });
        } else {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    private Call getCall(boolean register, String username, String password) {
        return getClient().newCall(getRequest(register, username, password));
    }

    private Request getRequest(boolean register, String username, String password) {

        return new Request.Builder()
                .url(register ? Constant.URL_REGISTER : Constant.URL_LOGIN)
                .post(new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build())
                .build();

    }

    private OkHttpClient getClient() {
        return new OkHttpClient.Builder().build();
    }
}
