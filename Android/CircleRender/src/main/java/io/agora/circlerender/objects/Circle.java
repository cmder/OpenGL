package io.agora.circlerender.objects;

import android.opengl.GLES20;

import io.agora.circlerender.data.VertexArray;
import io.agora.circlerender.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static io.agora.circlerender.widget.Constants.BYTES_PER_FLOAT;

public class Circle {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private float[] VERTEX_DATA;

    // 圆形分割的数量，分成 360 份，可由 360 个线段组成空心圆，也可以由 360 个三角形组成实心圆
    private static final int VERTEX_DATA_NUM = 360;
    // 360 个顶点的位置，因为有 x 和 y 坐标，所以 double 一下，再加上中心点 和 闭合的点
    // Order of coordinates: X, Y, S, T
    private float[] circleVertex = new float[(VERTEX_DATA_NUM * 2 + 4) * 2];
    // 分成 360 份，每一份的弧度
    private float radian = (float) (2 * Math.PI / VERTEX_DATA_NUM);
    // 绘制的半径
    private float radius = 1f;

    private final VertexArray vertexArray;

    public Circle() {
        VERTEX_DATA = createPositions();
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        GLES20.glDrawArrays(GL_TRIANGLE_FAN, 0, circleVertex.length / 4);
    }

    // 初始化圆形的纹理顶点数据
    private float[] createPositions() {
        // 中心点
        circleVertex[0] = 0f;
        circleVertex[1] = 0f;
        circleVertex[2] = 0.5f;
        circleVertex[3] = 0.5f;
        // 圆的 360 份的顶点数据
        for (int i = 0; i < VERTEX_DATA_NUM; i++) {
            circleVertex[4 * i + 0 + 4] = (float) (radius * Math.cos(radian * i));
            circleVertex[4 * i + 1 + 4] = (float) (radius * Math.sin(radian * i));
            circleVertex[4 * i + 2 + 4] = (float) ((radius / 2) * Math.cos(radian * i)) + 0.5f;
            circleVertex[4 * i + 3 + 4] = (float) ((radius / 2) * Math.sin(radian * i)) + 0.5f;
        }
        // 闭合点
        circleVertex[VERTEX_DATA_NUM * 4 + 4] = (float) (radius * Math.cos(radian));
        circleVertex[VERTEX_DATA_NUM * 4 + 5] = (float) (radius * Math.sin(radian));
        circleVertex[VERTEX_DATA_NUM * 4 + 6] = (float) ((radius / 2) * Math.cos(radian)) + 0.5f;
        circleVertex[VERTEX_DATA_NUM * 4 + 7] = (float) ((radius / 2) * Math.sin(radian)) + 0.5f;
        return circleVertex;
    }
}
