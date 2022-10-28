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

import io.agora.circlerender.util.ShaderHelper;
import io.agora.circlerender.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

abstract class ShaderProgram {
    // Uniform constants
    protected static final String TEXTURE_MATRIX_UNIFORM = "uTextureMatrix";
    protected static final String TEXTURE_SAMPLER_UNIFORM = "uTextureSampler";

    // Attribute constants
    protected static final String POSITION_ATTRIBUTE = "aPosition";
    protected static final String A_COLOR = "a_Color";
    protected static final String TEXTURE_COORD_ATTRIBUTE = "aTextureCoordinate";

    // Shader program
    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId,
                            int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(
                        context, vertexShaderResourceId),
                TextResourceReader.readTextFileFromResource(
                        context, fragmentShaderResourceId));
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }
}
