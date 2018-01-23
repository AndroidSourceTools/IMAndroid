package com.lcp.imandroid.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.lcp.imandroid.model.User;

/**
 * Created by linchenpeng on 2018/1/23.
 */

public class PreferenceUtil {
    private static final String KEY = "account_";
    private static final String NICKNAME_KEY = "nickname";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String USER_ID_KEY = "user_id";
    private static final String AUTO_LOGIN = "auto_login";
    //网络id
    private static final String PROJECT_ID = "project_id";
    private static final String COLLEAGUE = "colleagues";
    private static SharedPreferences spf;

    private static void getSpf(Context context) {
        if (spf == null) {
            spf = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        }
    }

    /**
     * 保存用户id
     *
     * @param user_id 用户id
     * @param context
     */
    public static void setUserId(String user_id, Context context) {
        getSpf(context);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(USER_ID_KEY, user_id);
        editor.apply();//editor.commit();
    }

    /**
     * 获取用户id
     *
     * @param context
     * @return
     */
    public static String getUserId(Context context) {
        getSpf(context);
        return spf.getString(USER_ID_KEY, "");
    }

    /**
     * 保存用户名
     *
     * @param userName 用户名
     * @param context
     */
    public static void setUserName(String userName, Context context) {
        getSpf(context);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(USERNAME_KEY, userName);
        editor.apply();//editor.commit();
    }

    /**
     * 获取用户名
     *
     * @param context
     * @return
     */
    public static String getUserName(Context context) {
        getSpf(context);
        return spf.getString(USERNAME_KEY, "");
    }

    /**
     * 保存昵称
     *
     * @param userName 用户名
     * @param context
     */
    public static void setNickname(String userName, Context context) {
        getSpf(context);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(NICKNAME_KEY, userName);
        editor.apply();//editor.commit();
    }

    /**
     * 获取用户名
     *
     * @param context
     * @return
     */
    public static String getNickname(Context context) {
        getSpf(context);
        return spf.getString(NICKNAME_KEY, "");
    }


    /**
     * 保存密码：加密过后的，用于自动登录
     *
     * @param password
     * @param context
     */
    public static void setPassword(String password, Context context) {
        getSpf(context);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(PASSWORD_KEY, password);
        editor.apply();//editor.commit();
    }

    /**
     * 获取加密过后的密码，用于自动登录
     *
     * @param context
     * @return
     */
    public static String getPassword(Context context) {
        getSpf(context);
        return spf.getString(PASSWORD_KEY, "");
    }

    /**
     * 保存accesstoken
     *
     * @param accessToken
     * @param context
     */
    public static void setAccessToken(String accessToken, Context context) {
        getSpf(context);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(ACCESS_TOKEN_KEY, accessToken);
        editor.apply();//editor.commit();
    }

    /**
     * 获取CurrentAccessToken
     *
     * @param context
     * @return
     */
    public static String getAccessToken(Context context) {
        getSpf(context);
        return spf.getString(ACCESS_TOKEN_KEY, "");
    }

    /**
     * 设置自动登录，用于自动登录
     *
     * @param autoLogin
     * @param context
     */
    public static void setAutoLogin(boolean autoLogin, Context context) {
        getSpf(context);
        SharedPreferences.Editor editor = spf.edit();
        editor.putBoolean(AUTO_LOGIN, autoLogin);
        editor.apply();//editor.commit();
    }

    /**
     * 获取登录状态，为了测试方便，此时设置为true，以后应该设置为false
     *
     * @param context
     * @return
     */
    public static boolean getAutoLogin(Context context) {
        getSpf(context);
        return spf.getBoolean(AUTO_LOGIN, true);
    }

    public static void saveUser(User user,Context context) {
        setUserName(user.getUsername(),context);
        setPassword(user.getPassword(),context);
    }
}
