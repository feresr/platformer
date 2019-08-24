package com.feresr.platformer

import com.badlogic.gdx.graphics.Texture
import java.nio.ByteOrder
import kotlin.math.abs

class Player(
        var x: Float,
        var y: Float,
        var dy: Float = 0f,
        var dx: Float = 0f,
        var inAir: Boolean = true

) {
    private var flipHorizontal: Boolean = false
    private var sprite: IntArray
    private var sprite2: IntArray
    private var current: IntArray

    init {
        val texture = Texture("hero.png")
        val texture2 = Texture("hero2.png")
        texture.textureData.prepare()
        texture2.textureData.prepare()
        val pixels = texture.textureData.consumePixmap().pixels
        val pixels2 = texture2.textureData.consumePixmap().pixels
        sprite = IntArray(pixels.asIntBuffer().remaining())
        sprite2 = IntArray(pixels2.asIntBuffer().remaining())

        current = sprite
        pixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(sprite)
        pixels2.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(sprite2)
    }

    fun draw(graphics: Graphics, camera: Camera) {
        graphics.drawSprite(current, Main.TILE_SIZE,
                (x - camera.offsetX).toInt(),
                (y - camera.offsetY).toInt(),
                flipHorizontal
        )
        current = sprite
    }

    fun jump(amount: Float) {
        if (!inAir) {
            dy -= amount
            inAir = true
        }
    }
    fun moveLeft() {
        current = if (abs(dx) > .3) {
            if ((System.currentTimeMillis() / 80) % 2 == 0L) sprite else sprite2
        } else {
            sprite
        }
        flipHorizontal = true
        dx -= Main.PLAYER_ACC
    }

    fun moveRight() {
        current = if (abs(dx) > .3) {
            if ((System.currentTimeMillis() / 80) % 2 == 0L) sprite else sprite2
        } else {
            sprite
        }
        flipHorizontal = false
        dx += Main.PLAYER_ACC
    }
}