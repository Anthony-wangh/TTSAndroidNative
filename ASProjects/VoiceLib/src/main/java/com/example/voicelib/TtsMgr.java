package com.example.voicelib;

import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.unity3d.player.UnityPlayer;
//语音合成管理器
public class TtsMgr
{
    private  String TAG = "[TTS]";
    private  SpeechSynthesizer mTts;

    public  String voicerLocal = "xiaoyan";
    private  String path;

    public TtsMgr(){
        Init();
    }
    public  void Init()
    {
        try
        {
            Debug("初始化参数！！");
            SpeechUtility.createUtility(UnityPlayer.currentActivity, SpeechConstant.APPID + "=ed78ec06");
            // 初始化合成对象
            mTts = SpeechSynthesizer.createSynthesizer(UnityPlayer.currentActivity, mTtsInitListener);
        }
        catch (Exception e)
        {
            Debug("创建合成对象实例失败！"+e);
        }
        setParam();
    }

    private  InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code)
        {
            if (code != ErrorCode.SUCCESS) {
                Debug("语音合成模块启动失败,错误码：" + code );

            } else {

                Debug("语音合成模块启动成功！！");
            }
        }
    };

    public  void  Start(String text){
        Debug("开始合成！！"+text);
//        int code = mTts.startSpeaking(text, mTtsListener);
        path =UnityPlayer.currentActivity.getExternalFilesDir("msc").getAbsolutePath() + "/tts.pcm";
		int code = mTts.synthesizeToUri(text, path, mTtsListener);
        if (code != ErrorCode.SUCCESS)
        {
            OnError("播放失败！"+code);
        }
    }



    private  void  OnError(String msg)
    {
        UnityMessageUlt.SendToUnity(TTSGlobal.ErrorCode, msg);
    }

    private  void  Debug(String msg){
        Log.d(TAG, msg);
        UnityMessageUlt.SendToUnity(TTSGlobal.DebugCode, msg);
    }

    /**
     * 合成回调监听。
     */
    private  SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            //showTip("开始播放");
//            Log.d(TAG, "开始播放：" + System.currentTimeMillis());
        }

        @Override
        public void onSpeakPaused()
        {
//            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed()
        {
//            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info)
        {
            // 合成进度
//            mPercentForBuffering = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));

//            UnityMessageUlt.SendToUnity(TTSGlobal.ProcessCode,Integer.toString(percent));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
//            mPercentForPlaying = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null)
            {
                UnityMessageUlt.SendToUnity(TTSGlobal.SynthesisCompleteCode,path);
            } else {
                UnityMessageUlt.SendToUnity(TTSGlobal.ErrorCode,error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_AUDIO_URL);
                Log.d(TAG, "session id =" + sid);
            }

        }
    };

    private  void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_XTTS);
        //设置发音人资源路径
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicerLocal);
        //设置使用云端引擎
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置发音人
//        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //设置合成
//        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
//            //设置使用云端引擎
//            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//            //设置发音人
//            mTts.setParameter(SpeechConstant.VOICE_NAME, voicerCloud);
//        }
//        } else if (mEngineType.equals(SpeechConstant.TYPE_LOCAL)) {
//            //设置使用本地引擎
//            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
//            //设置发音人资源路径
//            mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
//            //设置发音人
//            mTts.setParameter(SpeechConstant.VOICE_NAME, voicerLocal);
//        } else {
//            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_XTTS);
//            //设置发音人资源路径
//            mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
//            //设置发音人
//            mTts.setParameter(SpeechConstant.VOICE_NAME, voicerXtts);
//        }
        //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY,"1");//支持实时音频流抛出，仅在synthesizeToUri条件下支持
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        //	mTts.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC+"");

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
                UnityPlayer.currentActivity.getExternalFilesDir("msc").getAbsolutePath() + "/tts.pcm");

    }


    //获取发音人资源路径
    private  String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        String type = "xtts";
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(UnityPlayer.currentActivity, ResourceUtil.RESOURCE_TYPE.assets, type + "/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(UnityPlayer.currentActivity, ResourceUtil.RESOURCE_TYPE.assets, type + "/" + voicerLocal + ".jet"));
        return tempBuffer.toString();
    }

    public void Dispose(){
        if (mTts==null) return;
        mTts.destroy();
    }
}
