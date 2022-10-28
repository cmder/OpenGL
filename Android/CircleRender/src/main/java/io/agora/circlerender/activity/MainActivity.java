package io.agora.circlerender.activity;

import android.hardware.Camera;
import android.os.Bundle;

import io.agora.circlerender.R;
import io.agora.circlerender.util.MyCameraManager;
import io.agora.circlerender.widget.CircleRendererView;

public class MainActivity extends BaseActivity {
    private CircleRendererView mCircleRendererView;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private MyCameraManager mMyCameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mCircleRendererView = findViewById(R.id.circleRendererView);
        mMyCameraManager = new MyCameraManager(this);
        if (!mMyCameraManager.openCamera(mCameraId)) {
            return;
        }
        mCircleRendererView.init(mMyCameraManager, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCircleRendererView.destroy();
    }
}
