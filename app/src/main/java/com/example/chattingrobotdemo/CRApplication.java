package com.example.chattingrobotdemo;

import android.app.Application;

import com.iflytek.cloud.SpeechUtility;

/**
 * Created by djs on 2017/12/31.
 */

public class CRApplication extends Application {
    @Override
    public void onCreate() {
        SpeechUtility.createUtility(this,"appid=" + getString(R.string.app_id));
        super.onCreate();
    }
}
