package com.lcp.imandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.edit_message)
    EditText editMessage;

    MessageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        adapter = new MessageAdapter(IMApplication.getMessages());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        startService(new Intent(this, MsgService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.setNewData(IMApplication.getMessages());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMessage(Message message) {
        adapter.setNewData(IMApplication.getMessages());
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.btn_send})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String message = editMessage.getText().toString();

                if (!TextUtils.isEmpty(message) && MsgService.sendMessage(message)) {
                    Toast.makeText(this, "正在发送", Toast.LENGTH_SHORT).show();
                    editMessage.setText("");
                }else{
                    Toast.makeText(this, "正在重连", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
