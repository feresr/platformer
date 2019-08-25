package com.feresr.platformer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA
import com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.BufferUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.pow
import kotlin.math.sqrt

open class GraphicLayer(private val width: Int, private val height: Int) {

    private val shader: ShaderProgram by lazy { ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER) }
    fun init() {
        if (!shader.isCompiled) throw IllegalStateException(shader.log)

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        val tb = BufferUtils.newIntBuffer(1)
        Gdx.gl.glGenTextures(1, tb)
        Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, tb[0])

        Gdx.gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, width, height, 0,
                GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, null)

        val atexcoor = shader.getAttributeLocation("a_texcoord")
        Gdx.gl.glVertexAttribPointer(
                atexcoor,
                2, GL30.GL_FLOAT, false, 0, createVerticesBuffer(TEX_VERTICES))
        Gdx.gl.glEnableVertexAttribArray(atexcoor)

        val aposition = shader.getAttributeLocation("a_position")
        Gdx.gl.glVertexAttribPointer(
                aposition,
                2, GL30.GL_FLOAT, false, 0, createVerticesBuffer(POS_VERTICES))
        Gdx.gl.glEnableVertexAttribArray(aposition)

        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST)
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST)
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE)
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE)

        shader.begin()
        print(Gdx.gl.glGetError())
    }

    private val buffer = BufferUtils.newIntBuffer(width * height)
    private val clear = IntArray(width * height)

    private fun createVerticesBuffer(vertices: FloatArray): FloatBuffer {
        if (vertices.size != 8) {
            throw RuntimeException("Number of vertices should be four.")
        }
        val buffer = ByteBuffer.allocateDirect(vertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        buffer.put(vertices).position(0)
        return buffer
    }

    private fun drawPixel(x: Int, y: Int, r: Int, g: Int, b: Int, a: Int) {
        try {
            if (x < 0 || y < 0 || x >= width || y >= width) return
            buffer.put(y * width + x, r or (g shl 8) or (b shl 16) or (a shl 24))
        } catch (e: IndexOutOfBoundsException) {
            throw IndexOutOfBoundsException("Trying to draw to invalid position $x , $y")
        }
    }

    open fun drawSprite(sprite: IntArray, w: Int, x: Int, y: Int, flipHorizontal: Boolean = false) {

        val h = sprite.size / w

        for ((i, lineY) in (y until y + h).withIndex()) {
            for ((j, lineX) in (x until x + w).withIndex()) {

                if (lineX >= width) continue
                if (lineY >= height) continue

                val inx = if (!flipHorizontal) j else (w - 1 - j)
                drawPixel(lineX, lineY,
                        sprite[i * w + inx] and 0xff,
                        (sprite[i * w + inx] ushr 8) and 0xff,
                        (sprite[i * w + inx] ushr 16) and 0xff,
                        (sprite[i * w + inx] ushr 24) and 0xff
                )
            }
        }
    }

    open fun drawLine(x1: Float, y1: Float,
                      x2: Float, y2: Float,
                      r: Int, g: Int, b: Int) {

        val directionX = x2 - x1
        val directionY = y2 - y1
        val length = sqrt(directionX.pow(2) + directionY.pow(2))

        val unitX = directionX / length
        val unitY = directionY / length

        var currentX: Float = x1
        var currentY: Float = y1

        while (sqrt((x2 - currentX).pow(2) + (y2 - currentY).pow(2)) >= .9f) {
            drawPixel(currentX.toInt(), currentY.toInt(), r, g, b, 0xff)
            currentX += unitX / 2
            currentY += unitY / 2
        }
    }

    fun render() {
        buffer.rewind()
        Gdx.gl.glTexSubImage2D(GL20.GL_TEXTURE_2D, 0, 0, 0, width, height,
                GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer)
        Gdx.gl.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, 4)
        buffer.put(clear)
    }

    fun dispose() {
        shader.dispose()
    }

    open fun drawText(font: Font, string: String, x: Int, y: Int, color : Int, characterSpacing: Int = 2) {
        val characters = string.map { font.getCharacter(it) }
        var nextCharacterX = x - characters.map { it.width + characterSpacing }.sum() / 2
        characters.forEach {

            for (i in 0 until it.pixels.size) {
                it.pixels[i] = (it.pixels[i] and 0xFF000000.toInt()) or (color and 0x00FFFFFF)
            }
            drawSprite(it.pixels, it.width, nextCharacterX, y)
            nextCharacterX += it.width + characterSpacing
        }
    }

    companion object {
        private const val VERTEX_SHADER =
                "attribute vec4 a_position;\n" +
                        "attribute vec2 a_texcoord;\n" +
                        "varying vec2 v_texcoord;\n" +
                        "void main() {\n" +
                        "  gl_Position = a_position;\n" +
                        "  v_texcoord = a_texcoord;\n" +
                        "}\n"

        private const val FRAGMENT_SHADER =
                //"precision mediump float;\n" +
                "uniform sampler2D tex_sampler;\n" +
                        "varying vec2 v_texcoord;\n" +
                        "void main() {\n" +
                        "  gl_FragColor = texture2D(tex_sampler, v_texcoord);\n" +
                        "}\n"

        private val TEX_VERTICES = floatArrayOf(0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f)
        private val POS_VERTICES = floatArrayOf(-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f)
    }

}