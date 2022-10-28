package io.agora.circlerender.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import io.agora.circlerender.util.MyCameraManager;

public class CircleRendererView extends GLSurfaceView {
    private static final String TAG = "CircleRendererView";

    private Context mContext;

    private CircleRenderer mRenderer;

    public CircleRendererView(Context context) {
        super(context);
        mContext = context;
    }

    public CircleRendererView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void init(MyCameraManager camera, boolean isPreviewStarted) {
        setEGLContextClientVersion(2);

        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);

        mRenderer = new CircleRenderer(mContext);
        mRenderer.init(this, camera, isPreviewStarted);
        setRenderer(mRenderer);
    }

    public void destroy() {
        if (mRenderer != null) {
            mRenderer.destroy();
        }
    }
}
