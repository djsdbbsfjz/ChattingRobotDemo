package com.example.chattingrobotdemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

public class SynthesizerActivity extends AppCompatActivity {

    //语音合成对象
    private SpeechSynthesizer mTts;
    //引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 默认发音人
    private String voicer = "xiaoyan";

    private EditText mEditText;
    private Button mSynBtn;
    private Button mCancelBtn;
    private Button mPauseBtn;
    private Button mResumeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synthesizer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initLayout();
        //初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(SynthesizerActivity.this,mTtsInitListener);
    }

    private void initLayout(){
        mSynBtn = (Button) findViewById(R.id.syn_btn);
        mCancelBtn = (Button) findViewById(R.id.cancel_btn);
        mPauseBtn = (Button) findViewById(R.id.pause_btn);
        mResumeBtn = (Button) findViewById(R.id.resume_btn);
        mEditText = (EditText) findViewById(R.id.syn_text);

        mSynBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( null == mTts ){
                    // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
                    Toast.makeText(SynthesizerActivity.this,"创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化",Toast.LENGTH_SHORT).show();
                    return;
                }
                // 开始合成
                // 收到onCompleted 回调时，合成结束、生成合成音频
                // 合成的音频格式：只支持pcm格式
                String text = mEditText.getText().toString();
                //设置参数
                setParam();
                int code = mTts.startSpeaking(text,mTtsListener);
                if (code != ErrorCode.SUCCESS){
                    Toast.makeText(SynthesizerActivity.this,"语音合成失败,错误码: " + code,Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTts.stopSpeaking();
            }
        });

        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTts.pauseSpeaking();
            }
        });

        mResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTts.resumeSpeaking();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(SynthesizerActivity.this,"初始化失败,错误码："+code,Toast.LENGTH_SHORT).show();
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    /**
     * 合成回调监听
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            Toast.makeText(SynthesizerActivity.this,"开始播放",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {
            if (speechError == null){
                Toast.makeText(SynthesizerActivity.this,"播放完成",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(SynthesizerActivity.this,speechError.getPlainDescription(true),Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    /**
     * 参数设置
     */
    private void setParam(){
        //清空参数
        mTts.setParameter(SpeechConstant.PARAMS,null);
        //设置引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE,SpeechConstant.TYPE_CLOUD);
        //设置合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME,voicer);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");


        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mTts){
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }
}
