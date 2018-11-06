package com.shanghaict.letinvr.androidservice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView mTx;
    private String str;

    private static final String HTTP = "http://fjbmvapp.hvs.fj.chinamobile.com:9006/appRegister/queryAuthorizationRel";
    private static final String JSON = "{\"appId\":\"10011\",\"timestamp\":\"20181011105239\",\"token\":\"fKpCodpB8iUuIScyP985rgVIV3gT8LmPDqoe058MoWM=\",\"VRId\":\"9C-B6-D0-10-CA-9B\",\"reqType\":\"2\",\"name\":null}";
    private static final String POST_NULL_URL = "http://shop.letinvr.com/api/app_cat";//post 无参接口
    private static final String APPS = "http://shop.letinvr.com/api/apps";
    private static final String APP_DATA = "http://shop.letinvr.com/api/app_detail";

    String apkjson = "{\"id\":\"3\"}";
    //接收广播
    private static final String ACTION = "com.lz.demo.processcommunication.MYBROADCASTS";
    //发送广播
    private static final String BROADCAST_ACTION = "com.lz.demo.processcommunication.MAX";
    int i = 0;

    //要启动的包名
    private String PACK_NAME = "com.shanghaict.letinvr.unityofandroidjar";
    //要启动的包类名
    private String CLASS_NAME = "com.shanghaict.letinvr.unityofandroidjar.MainActivity";

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();
            switch (action) {
                case ACTION:
                    i++;
                    Toast.makeText(context, intent.getStringExtra("data"), Toast.LENGTH_SHORT).show();
                    mTx.setText(intent.getStringExtra("data"));
                    Intent intent1 = new Intent(BROADCAST_ACTION);
                    intent1.putExtra("res", postRequest(POST_NULL_URL,null,null));
                    sendBroadcast(intent1);
                    Log.e(TAG, "onReceive: -------------------------------------");
                    break;
            }


        }
    };


    /**
     * Android-jar-A发送Action
     * com.letinvr.processcommunication.SERVICE_ACTION_ONE  //发送Letin-Service
     * com.letinvr.processcommunication.ANDROID_ONE_ACTION  //发送Android-jar-B
     *
     * Android-jar-A接收Action
     * com.letinvr.processcommunication.RECEIVE_SERVICE_ACTION_ONE  //接收Letin-Service
     * com.letinvr.processcommunication.RECEIVE_ANDROID_TWO_ACTION  //接收Android-jar-B
     *
     * *********************************************************************************
     *
     * Letin-Service 接收Action
     * com.letinvr.processcommunication.SERVICE_ACTION_ONE //接收Android-jar-A
     * com.letinvr.processcommunication.SERVICE_ACTION_TWO  //接收Android-jar-B
     *
     * Letin-Service 发送Action
     * com.letinvr.processcommunication.RECEIVE_SERVICE_ACTION_ONE  //发送Android-jar-A
     * com.letinvr.processcommunication.RECEIVE_SERVICE_ACTION_TWO  //发送Android-jar-B
     *
     * **********************************************************************************
     *
     * Android-jar-B发送Action
     * com.letinvr.processcommunication.SERVICE_ACTION_TWO  //发送Letin-Service
     * com.letinvr.processcommunication.RECEIVE_ANDROID_TWO_ACTION  //发送Android-jar-B
     *
     * Adnroid-jar-B接收
     * com.letinvr.processcommunication.ANDROID_ONE_ACTION  //接收Android-jar-A
     * com.letinvr.processcommunication.RECEIVE_SERVICE_ACTION_TWO  //接收Letin-Service
     *
     *
     */



    private boolean  bool =true;

    Handler handler =new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 66:
                    Log.e(TAG, "handleMessage: ");

                    //收到 what 将进度条赋值
                    /**
                     * 进度条赋值等操作
                     */
                    break;
            }
        }
    };


    Timer mTimer =new Timer();

    TimerTask timerTask =new TimerTask() {
        @Override
        public void run() {

            Log.e(TAG, "run: ");
           //进度每秒增加 1
            handler.sendEmptyMessage(66);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (bool){
            /**每秒执行一次 无延迟*/
            mTimer.schedule(timerTask,0,1000);
            bool=false;
        }


        mTx = findViewById(R.id.mTx);

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            String date = extras.getString("messager");
            mTx.setText(date);
        }else {
            mTx.setText("没接收到内容");
        }


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(receiver, intentFilter);

        mTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                //前提：知道要跳转应用的包名、类名
                ComponentName componentName = new ComponentName(PACK_NAME, CLASS_NAME);
                Bundle bundle = new Bundle();//bundle数据
                bundle.putString("oap", "Service传递的消息");//
                intent.putExtras(bundle);
                intent.setAction("MedicalRecordDetail");
                intent.setComponent(componentName);
                startActivity(intent);
            }
        });




    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     *
     * @param urls      Url地址
     * @param parameter_Json    参数
     */
    public String postRequest(final String urls, final String parameter_Json,final String headStr) {

        try {
            Log.d("HVC", "serverAddr: " + urls + " postData: " + parameter_Json + " cookieStr: " + headStr);
            URL url = new URL(urls);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");


            if (headStr!=null){
                connection.setRequestProperty("Content-Type", headStr);


            }else {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            }

            //            if(cookieStr != null) {
//                connection.setRequestProperty("Cookie", cookieStr);
//            }

            if(parameter_Json != null) {
                connection.setRequestProperty("Content-Length", parameter_Json.length() + "");
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(parameter_Json.getBytes());
            }

            int responseCode = connection.getResponseCode();
            Log.d("HVC", " responseCode " + responseCode);
            if(responseCode == 200) {
                InputStream is = connection.getInputStream();
                String out = IOUtlis.readFully(is);
                Log.d("HVC", "out response is " + out);
                return out;
            }

            Log.e(TAG, "postRequest: "+urls);
            return null;
        } catch (MalformedURLException var9) {
            var9.printStackTrace();
        } catch (ProtocolException var10) {
            var10.printStackTrace();
        } catch (IOException var11) {
            var11.printStackTrace();
        } catch (Exception var12) {
            var12.printStackTrace();
        }

        return null;
    }



}
