package com.lcp.imandroid;

import android.app.Application;

import com.lcp.imandroid.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchenpeng on 2018/1/22.
 */

public class IMApplication extends Application {
    private static List<Message> messages;
    private static User me;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public synchronized static void addMessage(Message message){
        if(messages==null)
            messages=new ArrayList<>();
        messages.add(message);
    }
    public synchronized static List<Message> getMessages(){
        if(messages==null)
            messages=new ArrayList<>();
        return messages;
    }

    public static void setMe(User me) {
        IMApplication.me = me;
    }

    public static User getMe() {
        return me;
    }
}
