package io.agora.circlerender.activity;

import android.os.Bundle;
import android.util.Log;

import io.agora.circlerender.R;
import io.agora.circlerender.widget.AgoraCircleRendererView;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

import static io.agora.rtc.mediaio.MediaIO.BufferType.BYTE_ARRAY;
import static io.agora.rtc.mediaio.MediaIO.PixelFormat.I420;

public class CallActivity extends BaseActivity {
    private static final String TAG = CallActivity.class.getSimpleName();

    private RtcEngine mRtcEngine;

    private AgoraCircleRendererView mAgoraCircleRendererView;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRtcEngine.setRemoteVideoRenderer(uid, mAgoraCircleRendererView);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        mAgoraCircleRendererView = findViewById(R.id.remote_view);
        mAgoraCircleRendererView.setBufferType(BYTE_ARRAY);
        mAgoraCircleRendererView.setPixelFormat(I420);
        mAgoraCircleRendererView.init();

        initAgoraEngineAndJoinChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveChannel();
        RtcEngine.destroy();
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();
        setupVideoProfile();
        joinChannel();
    }

    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(this, getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
    }

    private void joinChannel() {
        mRtcEngine.joinChannel(null, "demoChannel1", "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }
}
