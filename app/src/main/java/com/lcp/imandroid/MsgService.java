package com.lcp.imandroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.lcp.imandroid.util.Constant;

import org.greenrobot.eventbus.EventBus;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;

public class MsgService extends Service {
    private final static String TAG = MsgService.class.getSimpleName();
    private static Socket socket;
    private static String toId = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        try {
            initSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    private static void initSocket() throws Exception {

        IO.Options options = new IO.Options();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .hostnameVerifier(CustomTrust.getHostnameVerifier())
                .sslSocketFactory(CustomTrust.getSSLSocketFactory(), CustomTrust.getManager())
                .build();
        IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
        IO.setDefaultOkHttpCallFactory(okHttpClient);
        options.callFactory = okHttpClient;
        options.webSocketFactory = okHttpClient;
        options.timeout = 10000;

        socket = IO.socket(Constant.URL_CHAT, options);
        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.i(TAG, "连接成功");
            socket.emit("login", "android端-1");
        })
                .on(Socket.EVENT_DISCONNECT, args -> {
                    Log.i(TAG, "断开连接");
                    socket.connect();
                })
                .on(Socket.EVENT_CONNECTING, args -> {
                    Log.i(TAG, "正在连接");
                })
                .on(Socket.EVENT_RECONNECT, args -> {
                    Log.i(TAG, "正在重连");
                })
                .on(Socket.EVENT_ERROR, args -> Log.i(TAG + " eve error", args[0].toString()))
                .on(Socket.EVENT_CONNECT_ERROR, args -> {
                    Log.i(TAG, "连接错误:" + args[0]);
                })
                .on("new user", args -> {
                    Message msg = new Gson().fromJson(args[0].toString(),Message.class);
                    toId=msg.getFromId();
                    Log.i(TAG,"新用户上线:"+toId);
                })
                .on("chat message", args -> {
                    Log.i(TAG, "消息:" + args[0]);
                    Message message = new Gson().fromJson(args[0].toString(), Message.class);
                    IMApplication.addMessage(message);
                    EventBus.getDefault().post(message);
                    Object ack = null;
                    if (args.length > 1 && (ack = args[args.length - 1]) instanceof Ack) {
                        ((Ack) ack).call("我收到了");
                    }
                });

        socket.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null)
            socket.disconnect();
    }

    public static boolean sendMessage(String message) {
        Log.i(TAG, "socket为空?" + (socket != null ? "false,socket已连接?" + socket.connected() : "true"));
        if (socket != null && socket.connected()) {
            Message msg = new Message();
            msg.setMessage(message);
            if (toId != null)
                msg.setToId(toId);

            socket.emit("chat message single", new Gson().toJson(msg), (Ack) args -> {
                if (args != null && args.length > 0)
                    Log.i(TAG, "服务端收到消息之后说:" + args[0].toString());
            });
            return true;
        } else {
            if (socket == null)
                try {
                    initSocket();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            else
                socket.connect();
        }
        return false;
    }
}
