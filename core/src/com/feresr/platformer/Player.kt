package com.feresr.platformer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import java.nio.ByteOrder
import kotlin.math.abs

class Player(
        x: Float,
        y: Float,
        dy: Float = 0f,
        dx: Float = 0f,
        val door : (Tile) -> Unit,
        val hurt : () -> Unit,
        var inAir: Boolean = true
) : GameObject(x, y, dx, dy) {
    private var flipHorizontal: Boolean = false
    private var sprite: IntArray
    private var sprite2: IntArray
    private var current: IntArray

    init {
        val texture = Texture("hero.png")
        texture.textureData.prepare()
        val pixels = texture.textureData.consumePixmap().pixels
        sprite = IntArray(pixels.asIntBuffer().remaining())
        pixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(sprite)

        val texture2 = Texture("hero2.png")
        texture2.textureData.prepare()
        val pixels2 = texture2.textureData.consumePixmap().pixels
        sprite2 = IntArray(pixels2.asIntBuffer().remaining())

        current = sprite
        pixels2.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(sprite2)
    }

    fun update(collisions: Collisions, map: Map) {
        dx *= Main.FRICTION
        if (abs(dx) < Main.PLAYER_ACC / 2f) dx = 0f
        dy += Main.GRAVITY

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveRight()
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) moveLeft()
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) jump(Main.MAX_ACC)

        inAir = true
        if (dy > 0) {
            collisions.check(this, Collisions.Direction.DOWN) {
                when (it.type) {
                    'X', '-' -> {
                        y = (((y + Main.MAX_ACC) / (Main.TILE_SIZE)).toInt()) * Main.TILE_SIZE.toFloat()
                        inAir = false
                        dy = 0f
                    }
                    'O' -> map.replaceTile(it.x, it.y, ' ')
                    'H' -> hurt()
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
                    'O' -> map.replaceTile(it.x, it.y, ' ')
                }

            }
        }

        collisions.check(this, Collisions.Direction.RIGHT) {
            when (it.type) {
                'X' -> {
                    x = (((x + Main.TILE_SIZE / 2) / (Main.TILE_SIZE)).toInt()) * Main.TILE_SIZE.toFloat()
                    dx = minOf(0f, dx)
                }
                'D' -> door(it)
                'O' -> map.replaceTile(it.x, it.y, ' ')
            }
        }
        collisions.check(this, Collisions.Direction.LEFT) {
            when (it.type) {
                'X' -> {
                    x = ((x + Main.TILE_SIZE / 2) / (Main.TILE_SIZE)).toInt() * Main.TILE_SIZE.toFloat()
                    dx = maxOf(0f, dx)
                }
                'D' -> door(it)
                'O' -> map.replaceTile(it.x, it.y, ' ')
            }
        }

        dx = dx.coerceIn(-Main.MAX_ACC, Main.MAX_ACC)
        dy = dy.coerceIn(-Main.MAX_ACC, Main.MAX_ACC)

        x += dx //* elapsed
        y += dy //* elapsed
    }

    fun draw(layer: CameraLayer) {
        layer.drawSprite(current, Main.TILE_SIZE, x, y, flipHorizontal)
        current = sprite
    }

    private fun jump(amount: Float) {
        if (!inAir) {
            dy -= amount
            inAir = true
        }
    }

    private fun moveLeft() {
        current = if (abs(dx) > .3) {
            if ((System.currentTimeMillis() / 80) % 2 == 0L) sprite else sprite2
        } else {
            sprite
        }
        flipHorizontal = true
        dx -= Main.PLAYER_ACC
    }

    private fun moveRight() {
        current = if (abs(dx) > .3) {
            if ((System.currentTimeMillis() / 80) % 2 == 0L) sprite else sprite2
        } else {
            sprite
        }
        flipHorizontal = false
        dx += Main.PLAYER_ACC
    }

}