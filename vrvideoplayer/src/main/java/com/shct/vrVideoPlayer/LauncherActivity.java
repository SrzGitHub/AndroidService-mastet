package com.shct.vrVideoPlayer;

import android.os.Bundle;
import android.util.Log;

import com.dpvr.sdk.DpvrActivity;
import com.unity3d.player.UnityPlayer;

public class LauncherActivity extends DpvrActivity {

    private static final String TAG = "LauncherActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //     UnityPlayer.UnitySendMessage("AndroidTool","OnRecivedAndroidMsg",date);

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            Log.e(TAG, "extras: 不为空");
            String date = extras.getString("messager");
            Log.e(TAG, "date不为空 ");
            if (!date.isEmpty()){
                //将接收到的json 传给Unity回调方法
                UnityPlayer.UnitySendMessage("AndroidTool","OnRecivedAndroidMsg",date);

            }
        }else {
            Log.e(TAG, "extras为空 告诉Unity端 没接收到参数");
            UnityPlayer.UnitySendMessage("AndroidTool","OnRecivedAndroidMsg","未接收到参数");
        }

    }
}
