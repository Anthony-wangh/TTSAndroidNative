package com.example.voicelib;

import com.unity3d.player.UnityPlayer;

public class UnityMessageUlt
{
    public static void  Debug(String msg){

        SendToUnity(TTSGlobal.DebugCode,msg);
    }

    public static void  Error(String msg){

        SendToUnity(TTSGlobal.ErrorCode,msg);
    }
    public static void  SendToUnity(int code, String msg)
    {
        String s=code+"##"+msg;
        UnityPlayer.UnitySendMessage("TTSManager","OnMessage",s);
    }
}
