package com.example.voicelib;

import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.unity3d.player.UnityPlayer;

public class PermissionUlt {

    private static void RequestPermission(String permission)
    {
        // 检查是否已经授予了所需的权限
        if (ContextCompat.checkSelfPermission(UnityPlayer.currentActivity, permission)
                == PackageManager.PERMISSION_GRANTED) {
            // 权限已经被授予，可以执行相关操作
        } else {
            // 如果没有授予，则请求权限
            ActivityCompat.requestPermissions(UnityPlayer.currentActivity, new String[]{permission},1);
        }
    }


    public static void  RequestMyPermissions()
    {
        String[] permissions=new String[]{
                "android.permission.WRITE_EXTERNAL_STORAGE"
                , "android.permission.READ_EXTERNAL_STORAGE"
                , "android.permission.INTERNET"
                , "android.permission.RECORD_AUDIO"
                , "android.permission.MANAGE_EXTERNAL_STORAGE"};

        for(int i=0;i<permissions.length;i++)
        {
            RequestPermission(permissions[i]);
        }
    }
}
