package com.letinvr.appmerket;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;


public class AppMarketActivity extends UnityPlayerActivity {


    private static final String TAG = "AppMarketActivity";
    //路径
    public static final String APK_SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MNUpdateAPK/update.apk";
    private InstallUtils.DownloadCallBack downloadCallBack;
    private String apkDownloadPath;
    private int progress;    //进度
    private Activity context;
    private String ACTION ="android.intent.action.DOWNLOAD_COMPLETE";

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e(TAG, "handleMessage: " + progress + "%");
                    unityMessage(progress + "%");
                    break;
            }
        }
    };



    BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ACTION)){
                unityMessage("安装完成");
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initCallBack();

        IntentFilter filter =new IntentFilter();
        filter.addAction(ACTION);
        registerReceiver(receiver,filter);

    }

    public void unityMessage(String ms) {
        UnityPlayer.UnitySendMessage("AppMarket", "MarketCallback", ms);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置监听,防止其他页面设置回调后当前页面回调失效
        if (InstallUtils.isDownloading()) {
            InstallUtils.setDownloadCallBack(downloadCallBack);
        }
    }

    public void marKet(final String Url) {

//申请SD卡权限
//        if (!PermissionUtils.isGrantSDCardReadPermission(this)) {
//            PermissionUtils.requestSDCardReadPermission(this, 100);
//        } else {
        InstallUtils.with(context)
                //必须-下载地址
                .setApkUrl(Url)
                //非必须-下载保存的文件的完整路径+name.apk
                .setApkPath(APK_SAVE_PATH)
                //非必须-下载回调
                .setCallBack(downloadCallBack)
                //开始下载
                .startDownload();
//        }


    }

    private void initCallBack() {
        downloadCallBack = new InstallUtils.DownloadCallBack() {

            @Override
            public void onStart() {
                Log.e(TAG, "onStart: 开始下载");
            }

            @Override
            public void onComplete(String path) {
                Log.e(TAG, "onComplete: " + path);
                apkDownloadPath = path;
                Log.e(TAG, "onComplete: 下载成功");
                unityMessage("下载完成");
                //先判断有没有安装权限
                InstallUtils.checkInstallPermission(AppMarketActivity.this, new InstallUtils.InstallPermissionCallBack() {
                    @Override
                    public void onGranted() {
                        unityMessage("正在安装...");
                        //去安装APK
                        installApk(apkDownloadPath);
                    }

                    @Override
                    public void onDenied() {
                        //弹出弹框提醒用户
                        AlertDialog alertDialog = new AlertDialog.Builder(AppMarketActivity.this)
                                .setTitle("温馨提示")
                                .setMessage("需开启未知来源安装权限，请设置允许安装")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //打开设置页面
                                        InstallUtils.openInstallPermissionSetting(AppMarketActivity.this, new InstallUtils.InstallPermissionCallBack() {
                                            @Override
                                            public void onGranted() {
                                                //去安装APK
                                                Log.e(TAG, "打开未知来源，安装");
                                                unityMessage("正在安装...");
                                                installApk(apkDownloadPath);

                                            }

                                            @Override
                                            public void onDenied() {
                                                //还是不允许咋搞？


                                                Toast.makeText(AppMarketActivity.this, "不允许安装咋搞？强制更新就退出应用程序吧！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                })
                                .create();
                        alertDialog.show();
                    }
                });
            }

            @Override
            public void onLoading(long total, long current) {
                //内部做了处理，onLoading 进度转回progress必须是+1，防止频率过快
                Log.e(TAG, "InstallUtils----onLoading:-----total:" + total + ",current:" + current);
                progress = (int) (current * 100 / total);
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onFail(Exception e) {

                Log.e(TAG, "onFail:" + e.getMessage());
            }

            @Override
            public void cancle() {
                Log.e(TAG, "cancle下载取消");
            }
        };
    }

    private void installApk(String path) {
        InstallUtils.installAPK(context, path, new InstallUtils.InstallCallBack() {
            @Override
            public void onSuccess() {
                //onSuccess：表示系统的安装界面被打开
                //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
                unityMessage("正在安装程序");

            }

            @Override
            public void onFail(Exception e) {
                unityMessage("安装失败" + e.getMessage());
                Log.e(TAG, "onFail: 安装失败" + e.getMessage());
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: \n"
                + "requestCode：" + requestCode + "\n"
                + "grantResults：" + grantResults + "\n");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "onActivityResult: \n" + "requestCode:" + requestCode + "\nresultCode:" + resultCode);
        if (resultCode == RESULT_OK) {
            installApk(apkDownloadPath);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver!=null)
        unregisterReceiver(receiver);
        
    }
}
