package com.example.voicelib;

import android.util.Log;

import java.util.List;

import com.iflytek.sparkchain.core.LLM;
import com.iflytek.sparkchain.core.LLMCallbacks;
import com.iflytek.sparkchain.core.LLMConfig;
import com.iflytek.sparkchain.core.LLMError;
import com.iflytek.sparkchain.core.LLMEvent;
import com.iflytek.sparkchain.core.LLMResult;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.SparkChainConfig;
import com.unity3d.player.UnityPlayer;

import Permission.OnPermission;
import Permission.XXPermissions;

public class SparkMgr
{
    private  String TAG="SparkMgr";
    private  boolean sessionFinished=true;

    private   String text="";
    private  LLM llm;
    public  SparkMgr()
    {
        initSDK();
    }


    private  void initSDK() {
        // 初始化SDK，Appid等信息在清单中配置
        SparkChainConfig sparkChainConfig = SparkChainConfig.builder();
        sparkChainConfig.appID(VoiceManager.AppId)
                .apiKey(VoiceManager.AppKey)
                .apiSecret(VoiceManager.AppSecret)//应用申请的appid三元组
                .logLevel(0);

        int ret = SparkChain.getInst().init(UnityPlayer.currentActivity,sparkChainConfig);
        if(ret == 0){
            UnityMessageUlt.Debug("星火大模型SDK初始化成功：" + ret);
        }else{
            UnityMessageUlt.Error("星火大模型SDK初始化失败：其他错误:" + ret);
        }


        LLMConfig llmConfig = LLMConfig.builder();
        llmConfig.domain("generalv2")
                .url("");
        llm = new LLM(llmConfig);
        LLMCallbacks llmCallbacks = new LLMCallbacks() {
            @Override
            public void onLLMResult(LLMResult llmResult, Object usrContext) {
                Log.d(TAG,"onLLMResult\n");
                String content = llmResult.getContent();
                Log.e(TAG,"onLLMResult:" + content);
                int status = llmResult.getStatus();
                if(content != null) {
                    text+=content;
//                    chatMsg(content);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            chatText.append(content);
//                            toend();
//                        }
//                    });
                }
                if(usrContext != null) {
                    String context = (String)usrContext;
                    Log.d(TAG,"context:" + context);
                }
                if(status == 2){
                    int completionTokens = llmResult.getCompletionTokens();
                    int promptTokens = llmResult.getPromptTokens();//
                    int totalTokens = llmResult.getTotalTokens();
                    Log.e(TAG,"completionTokens:" + completionTokens + "promptTokens:" + promptTokens + "totalTokens:" + totalTokens);
                    sessionFinished = true;
                    chatMsg(text);
                }
            }

            @Override
            public void onLLMEvent(LLMEvent event, Object usrContext) {
                Log.d(TAG,"onLLMEvent\n");
                Log.w(TAG,"onLLMEvent:" + " " + event.getEventID() + " " + event.getEventMsg());
            }

            @Override
            public void onLLMError(LLMError error, Object usrContext) {
                Log.d(TAG,"onLLMError\n");
                Log.e(TAG,"errCode:" + error.getErrCode() + "errDesc:" + error.getErrMsg());

                UnityMessageUlt.Error("错误:" + " err:" + error.getErrCode() + " errDesc:" + error.getErrMsg() + "\n");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        chatText.append("错误:" + " err:" + error.getErrCode() + " errDesc:" + error.getErrMsg() + "\n");
//                    }
//                });
                if(usrContext != null) {
                    String context = (String)usrContext;
                    Log.d(TAG,"context:" + context);
                }
                sessionFinished = true;
            }
        };
        llm.registerLLMCallbacks(llmCallbacks);
    }


    private  void chatMsg(String msg)
    {
        UnityMessageUlt.SendToUnity(TTSGlobal.ChatMsgCode,msg);
    }

    public  void startChat(String userInput)
    {
        if (!sessionFinished)
        {
            UnityMessageUlt.Error("Busying! Please Wait");
            return;
        }

        text="";

        int ret = llm.arun(userInput,"myContext");
        if(ret != 0){
            Log.e(TAG,"SparkChain failed:\n" + ret);
            return;
        }

        sessionFinished = false;
    }

}
