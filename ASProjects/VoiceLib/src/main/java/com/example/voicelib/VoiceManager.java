package com.example.voicelib;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.unity3d.player.UnityPlayer;

import kotlin.jvm.internal.PropertyReference0Impl;

public class VoiceManager
{
    public static String AppId="ed78ec06";
    public static String AppKey="07fc44cd716197edf1b788d5a9a05c0c";
    public static String AppSecret="ZGU1MDI4YTA0NWU3MjlhNmYwNDMzYTY4";


    private static WakeupMgr wakeupMgr;
    private static ItaMgr  itaMgr;
    private static TtsMgr ttsMgr;
    private static SparkMgr sparkMgr;
    public static void InitWithDebugAppid()
    {
        Init(null,null,null);
    }

    public static void  Init(String appid,String appKey,String appSecret)
    {
        if (appid!=null){AppId=appid;}
        if (appKey!=null){AppKey=appKey;}
        if (appSecret!=null){AppSecret=appSecret;}
        //初始化讯飞语音工具，关联Listence
        SpeechUtility.createUtility(UnityPlayer.currentActivity, SpeechConstant.APPID + "="+AppId);



        wakeupMgr=new WakeupMgr();
        itaMgr=new ItaMgr();
        ttsMgr=new TtsMgr();
        sparkMgr=new SparkMgr();

    }

    public static void RequestPermission()
    {
        //请求权限
        PermissionUlt.RequestMyPermissions();
    }

    public static WakeupMgr GetWakeMgr(){
        return wakeupMgr;
    }
    public static ItaMgr GetItaMgr(){
        return itaMgr;
    }
    public static TtsMgr GetTtsMgr(){
        return ttsMgr;
    }
    public static SparkMgr GetSparkMgr(){
        return sparkMgr;
    }


    public static void Dispose(){
        wakeupMgr.Dispose();
        itaMgr.Dispose();
        ttsMgr.Dispose();
    }
}
