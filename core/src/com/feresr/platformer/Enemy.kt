package com.feresr.platformer

import com.badlogic.gdx.graphics.Texture
import java.nio.ByteOrder

class Enemy(
        x: Float,
        y: Float,
        dy: Float = 0f,
        var dead: Boolean = false
) : GameObject(x, y, dy, .5f) {

    private var sprite: IntArray

    init {
        val texture = Texture("hero2.png")
        texture.textureData.prepare()
        val pixels = texture.textureData.consumePixmap().pixels
        sprite = IntArray(pixels.asIntBuffer().remaining())
        pixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(sprite)
    }

    fun update(collisions: Collisions, map: Map) {
        dy += Main.GRAVITY
        if (dy > 0) {
            collisions.check(this, Collisions.Direction.DOWN) {
                when (it.type) {
                    'X', '-' -> {
                        y = (((y + Main.MAX_ACC) / (Main.TILE_SIZE)).toInt()) * Main.TILE_SIZE.toFloat()
                        dy = 0f
                    }
                }
            }
        } else if (dy < 0) {
            collisions.check(this, Collisions.Direction.UP) {
                when (it.type) {
                    'X' -> {
                        y = (((y + Main.MAX_ACC) / (Main.TILE_SIZE)).toInt()) * Main.TILE_SIZE.toFloat()
                        dy = 0f
                        map.replaceTile(it.x, it.y, ' ')
                    }
                }

            }
        }

        collisions.check(this, Collisions.Direction.RIGHT) {
            when (it.type) {
                'X' -> {
                    x = (((x + Main.TILE_SIZE / 2) / (Main.TILE_SIZE)).toInt()) * Main.TILE_SIZE.toFloat()
                    dx *= -1
                }
            }
        }
        collisions.check(this, Collisions.Direction.LEFT) {
            when (it.type) {
                'X' -> {
                    x = ((x + Main.TILE_SIZE / 2) / (Main.TILE_SIZE)).toInt() * Main.TILE_SIZE.toFloat()
                    dx *= -1
                }
            }
        }

        dx = dx.coerceIn(-Main.MAX_ACC, Main.MAX_ACC)
        dy = dy.coerceIn(-Main.MAX_ACC, Main.MAX_ACC)

        x += dx //* elapsed
        y += dy //* elapsed
    }

    fun draw(graphicLayer: CameraLayer) {
        graphicLayer.drawSprite(sprite, Main.TILE_SIZE, x, y)
    }
}