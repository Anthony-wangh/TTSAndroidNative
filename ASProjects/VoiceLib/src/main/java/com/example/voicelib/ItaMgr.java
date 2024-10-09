package com.example.voicelib;


import android.os.Bundle;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.unity3d.player.UnityPlayer;


public class ItaMgr
{
    private  SpeechRecognizer mIat;
    private  String mEngineType = "local";
    private  String mResultText;
    public ItaMgr()
    {
        Init();
    }
    private   void  Init(){
        mIat = SpeechRecognizer.createRecognizer(UnityPlayer.currentActivity, onInitListener);
        setParam();
    }
    public  void setParam()
    {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        String lag = "mandarin";
        // 设置引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        //mIat.setParameter(MscKeys.REQUEST_AUDIO_URL,"true");

        //	this.mTranslateEnable = mSharedPreferences.getBoolean( this.getString(R.string.pref_key_translate), false );
        if (mEngineType.equals(SpeechConstant.TYPE_LOCAL)) {
            // 设置本地识别资源
            mIat.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        }
        // 在线听写支持多种小语种，若想了解请下载在线听写能力，参看其speechDemo
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);

            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "10000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "3000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                UnityPlayer.currentActivity.getExternalFilesDir("msc").getAbsolutePath() + "/iat.wav");
    }
    private  InitListener onInitListener=new InitListener() {
        @Override
        public void onInit(int code)
        {
            if (code != ErrorCode.SUCCESS)
            {
                UnityMessageUlt.Error("语音识别模块启动失败，错误代码："+code);
            }
            else
            {
                UnityMessageUlt.Error("语音识别模块启动成功!!");
            }
        }
    };
    private  String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(UnityPlayer.currentActivity, ResourceUtil.RESOURCE_TYPE.assets, "iat/common.jet"));
        tempBuffer.append(";");
        tempBuffer.append(ResourceUtil.generateResourcePath(UnityPlayer.currentActivity, ResourceUtil.RESOURCE_TYPE.assets, "iat/sms_16k.jet"));
        //识别8k资源-使用8k的时候请解开注释
        return tempBuffer.toString();
    }


    public  void  Start(){
        mResultText="";
        int ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            UnityMessageUlt.Debug("开始监听失败："+ret);
        }
    }

    private  RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            UnityMessageUlt.Debug("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            UnityMessageUlt.Debug(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            UnityMessageUlt.Debug("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {

            String text = JsonParser.parseIatResult(results.getResultString());
            mResultText+=text;
            if (isLast) {
                //TODO 最后的结果
                UnityMessageUlt.SendToUnity(TTSGlobal.SpeechCompleteCode,mResultText);
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
//            UnityMessageUlt.Debug("当前正在说话，音量大小：" + volume);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_AUDIO_URL);
            }
        }
    };

    public void Dispose(){
        if (mIat==null){return;}
        mIat.destroy();
    }
}
