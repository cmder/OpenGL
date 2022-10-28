package io.agora.circlerender.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.nio.ByteBuffer;

import io.agora.rtc.gl.EglBase;
import io.agora.rtc.gl.RendererCommon;
import io.agora.rtc.mediaio.BaseVideoRenderer;
import io.agora.rtc.mediaio.IVideoSink;
import io.agora.rtc.mediaio.MediaIO;
import io.agora.rtc.utils.ThreadUtils;

public class AgoraCircleRendererView extends GLSurfaceView implements IVideoSink {
    private static final String TAG = "AgoraCircleRendererView";

    private Context mContext;

    private AgoraCircleRender agoraCircleRender;

    private BaseVideoRenderer mBaseVideoRenderer;
    private EglBase.Context mEglContext;
    private int[] mConfigAttributes;
    private RendererCommon.GlDrawer mDrawer;

    public AgoraCircleRendererView(Context context) {
        super(context);
        mContext = context;
        mBaseVideoRenderer = new BaseVideoRenderer(TAG);
    }

    public AgoraCircleRendererView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mBaseVideoRenderer = new BaseVideoRenderer(TAG);
    }

    public void init() {
        setEGLContextClientVersion(2);

        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);

        agoraCircleRender = new AgoraCircleRender(mContext);
        agoraCircleRender.init(this, mBaseVideoRenderer);
        setRenderer(agoraCircleRender);
    }

    public void destroy() {
        if (agoraCircleRender != null) {
            agoraCircleRender.destroy();
        }
    }

    public void init(EglBase.Context sharedContext) {
        this.mEglContext = sharedContext;
    }

    public void init(EglBase.Context sharedContext, int[] configAttributes, RendererCommon.GlDrawer drawer) {
        this.mEglContext = sharedContext;
        this.mConfigAttributes = configAttributes;
        this.mDrawer = drawer;
    }

    public long getEGLContextHandle() {
        return this.mBaseVideoRenderer.getEGLContextHandle();
    }

    public void setBufferType(MediaIO.BufferType bufferType) {
        this.mBaseVideoRenderer.setBufferType(bufferType);
    }

    public void setPixelFormat(MediaIO.PixelFormat pixelFormat) {
        this.mBaseVideoRenderer.setPixelFormat(pixelFormat);
    }

    public void setMirror(boolean mirror) {
        this.mBaseVideoRenderer.getEglRender().setMirror(mirror);
    }

    public boolean onInitialize() {
        if (this.mConfigAttributes != null && this.mDrawer != null) {
            this.mBaseVideoRenderer.init(this.mEglContext, this.mConfigAttributes, this.mDrawer);
        } else {
            this.mBaseVideoRenderer.init(this.mEglContext);
        }

        return true;
    }

    public boolean onStart() {
        return this.mBaseVideoRenderer.start();
    }

    public void onStop() {
        this.mBaseVideoRenderer.stop();
    }

    public void onDispose() {
        this.mBaseVideoRenderer.release();
    }

    public void consumeTextureFrame(int texId, int format, int width, int height, int rotation, long ts, float[] matrix) {
        this.mBaseVideoRenderer.consume(texId, format, width, height, rotation, ts, matrix);
    }

    public void consumeByteBufferFrame(ByteBuffer buffer, int format, int width, int height, int rotation, long ts) {
        this.mBaseVideoRenderer.consume(buffer, format, width, height, rotation, ts);
    }

    public void consumeByteArrayFrame(byte[] data, int format, int width, int height, int rotation, long ts) {
        this.mBaseVideoRenderer.consume(data, format, width, height, rotation, ts);
    }

    public int getBufferType() {
        int type = this.mBaseVideoRenderer.getBufferType();
        if (type == -1) {
            throw new IllegalArgumentException("Buffer type is not set");
        } else {
            return type;
        }
    }

    public int getPixelFormat() {
        int format = this.mBaseVideoRenderer.getPixelFormat();
        if (format == -1) {
            throw new IllegalArgumentException("Pixel format is not set");
        } else {
            return format;
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ThreadUtils.checkIsOnMainThread();
        this.mBaseVideoRenderer.getEglRender().setLayoutAspectRatio((float) (right - left) / (float) (bottom - top));
    }
}
