/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package io.agora.circlerender.programs;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import io.agora.circlerender.R;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class TextureShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uTextureMatrixLocation;
    private final int uTextureSamplerLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader,
                R.raw.texture_fragment_shader);

        // Retrieve uniform locations for the shader program.
        uTextureMatrixLocation = glGetUniformLocation(program, TEXTURE_MATRIX_UNIFORM);
        uTextureSamplerLocation = glGetUniformLocation(program, TEXTURE_SAMPLER_UNIFORM);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, POSITION_ATTRIBUTE);
        aTextureCoordinatesLocation =
                glGetAttribLocation(program, TEXTURE_COORD_ATTRIBUTE);
    }

    public void setUniforms(float[] matrix, int textureId) {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uTextureMatrixLocation, 1, false, matrix, 0);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId);

        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureSamplerLocation, 0);

    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
