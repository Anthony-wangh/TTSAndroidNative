package com.example.voicelib;

import static android.provider.Settings.System.getString;

import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

public class WakeupMgr {
    private static String TAG = "ivw";
    private static VoiceWakeuper mIvw;
    private final static int MAX = 3000;
    private final static int MIN = 0;
    private static int curThresh = 1450;
    private static String threshStr = "门限值：";
    private static String keep_alive = "1";
    private static String ivwNetMode = "0";


    public WakeupMgr()
    {
        Init();
    }
    private   void Init() {

        //非空判断，防止因空指针使程序崩溃
        mIvw = VoiceWakeuper.createWakeuper(UnityPlayer.currentActivity,onWakeInitListener);
        setRadioEnable(false);
        // 清空参数
        mIvw.setParameter(SpeechConstant.PARAMS, null);
        // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
        // 设置唤醒模式
        mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
        // 设置持续进行唤醒
        mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
        // 设置闭环优化网络模式
        mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
        // 设置唤醒资源路径
        mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
        // 设置唤醒录音保存路径，保存最近一分钟的音频
        mIvw.setParameter(SpeechConstant.IVW_AUDIO_PATH,
                UnityPlayer.currentActivity.getExternalFilesDir("msc").getAbsolutePath() + "/ivw.wav");
        mIvw.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
        //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
        // 启动唤醒
        /*	mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");*/

        mIvw.startListening(mWakeuperListener);
    }


    private  InitListener onWakeInitListener=new InitListener() {
        @Override
        public void onInit(int code)
        {
            if (code != ErrorCode.SUCCESS)
            {
                UnityMessageUlt.Error("唤醒模块启动失败，错误代码："+code);
            }
            else
            {
                UnityMessageUlt.Error("唤醒模块启动成功!!");
            }
        }
    };

    private  WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            Log.d(TAG, "onResult");
            if (!"1".equalsIgnoreCase(keep_alive)) {
                setRadioEnable(true);
            }
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 " + text);
                buffer.append("\n");
                buffer.append("【操作类型】" + object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】" + object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                UnityMessageUlt.Debug(buffer.toString());
                setRadioEnable(true);
            } catch (JSONException e) {
                UnityMessageUlt.Error("结果解析出错");
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SpeechError error) {
            UnityMessageUlt.Error(error.getPlainDescription(true));
            setRadioEnable(true);
        }

        @Override
        public void onBeginOfSpeech() {
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            switch (eventType) {
                // EVENT_RECORD_DATA 事件仅在 NOTIFY_RECORD_DATA 参数值为 真 时返回
                case SpeechEvent.EVENT_RECORD_DATA:
                    final byte[] audio = obj.getByteArray(SpeechEvent.KEY_EVENT_RECORD_DATA);
                    Log.i(TAG, "ivw audio length: " + audio.length);
                    break;
            }
        }

        @Override
        public void onVolumeChanged(int volume) {

        }
    };
    private  void setRadioEnable(boolean b)
    {
        UnityMessageUlt.SendToUnity(TTSGlobal.WakeCode,b?"1":"0");
    }

    private  String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(UnityPlayer.currentActivity,
                ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + TTSGlobal.AppId+ ".jet");
        Log.d(TAG, "resPath: " + resPath);
        return resPath;
    }

    public void Dispose()
    {
        if (mIvw==null)
            return;
        mIvw.destroy();
    }
}