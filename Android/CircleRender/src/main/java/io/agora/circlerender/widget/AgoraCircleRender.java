package io.agora.circlerender.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.agora.circlerender.objects.Circle;
import io.agora.circlerender.programs.TextureShaderProgram;
import io.agora.rtc.mediaio.BaseVideoRenderer;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

public class AgoraCircleRender implements GLSurfaceView.Renderer {
    private static final String TAG = "AgoraCircleRender";

    private final Context mContext;

    private int mTextureId = -1;
    private SurfaceTexture mSurfaceTexture;
    private float[] transformMatrix = new float[16];
    private AgoraCircleRendererView mCircleRendererView;
    private boolean mIsPreviewStarted;

    private Circle mCircle;

    private TextureShaderProgram mTextureProgram;

    private BaseVideoRenderer mBaseVideoRenderer;

    public AgoraCircleRender(Context context) {
        this.mContext = context;
    }

    public void init(AgoraCircleRendererView glSurfaceView, BaseVideoRenderer baseVideoRenderer) {
        mCircleRendererView = glSurfaceView;
        mBaseVideoRenderer = baseVideoRenderer;
    }

    /**
     * 一般在onSurfaceCreated 做一些初始化的动作
     * Render的初始化主要包括：
     * 创建shader---> 加载shader代码---> 编译shader  ====》最终可以生成vertexShader和fragmentShader
     * 创建porgram---> 附着顶点着色器和片段着色器到program--->链接program---> 通知OpenGL ES使用此program  ===》最终将shader添加到program中
     *
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // 获取一个纹理句柄
        mTextureId = createTextureOESObject();

        mCircle = new Circle();

        mTextureProgram = new TextureShaderProgram(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(transformMatrix);
        }

        if (!mIsPreviewStarted) {
            mIsPreviewStarted = initSurfaceTexture();
            mIsPreviewStarted = true;
            return;
        }

        mTextureProgram.useProgram();
        mTextureProgram.setUniforms(transformMatrix, mTextureId);
        mCircle.bindData(mTextureProgram);
        mCircle.draw();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public boolean initSurfaceTexture() {
        if (mCircleRendererView == null) {
            Log.i(TAG, "mCircleRendererView is null!");
            return false;
        }
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                mCircleRendererView.requestRender();
            }
        });
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBaseVideoRenderer.setRenderSurface(mSurfaceTexture);
            }
        });
        return true;
    }

    public void destroy() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
        }
    }

    /**
     * 返回一个纹理句柄，拿到这个纹理句柄后，就可以对它进行操作
     *
     * @return
     */
    public static int createTextureOESObject() {
        int[] tex = new int[1];
        // 返回一个纹理句柄
        GLES20.glGenTextures(1, tex, 0);
        // 绑定句柄
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        // 纹理对象使用GL_TEXTURE_EXTERNAL_OES作为纹理目标，其是OpenGL ES扩展GL_OES_EGL_image_external定义的。
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return tex[0];
    }
}