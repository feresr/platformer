package com.feresr.platformer

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Pixmap
import java.nio.ByteOrder

class Enemy(
        x: Float,
        y: Float,
        dy: Float = 0f,
        var dead: Boolean = false
) : GameObject(x, y, dy, .5f) {

    private lateinit var sprite: IntArray

    fun init(assetManager: AssetManager) {
        assetManager.load("hero2.png", Pixmap::class.java)
        assetManager.finishLoading()

        val pixels = assetManager.get("hero2.png", Pixmap::class.java).pixels
        sprite = IntArray(pixels.asIntBuffer().remaining())
        pixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(sprite)
    }

    fun update(collisions: Collisions, map: Map) {
        dy += Main.GRAVITY
        if (dy > 0) {
            collisions.check(this, Collisions.Direction.DOWN) {
                when (Map.Tiles.getByType(it.type)) {
                    Map.Tiles.Solid, Map.Tiles.Jump -> {
                        y = (((y + Main.MAX_ACC) / (Main.TILE_SIZE)).toInt()) * Main.TILE_SIZE.toFloat()
                        dy = 0f
                    }
                    else -> {
                    }
                }
            }
        } else if (dy < 0) {
            collisions.check(this, Collisions.Direction.UP) {
                when (Map.Tiles.getByType(it.type)) {
                    Map.Tiles.Solid -> {
                        y = (((y + Main.MAX_ACC) / (Main.TILE_SIZE)).toInt()) * Main.TILE_SIZE.toFloat()
                        dy = 0f
                        map.replaceTile(Tile(it.x, it.y, 0))
                    }
                    else -> {
                    }
                }

            }
        }

        collisions.check(this, Collisions.Direction.RIGHT) {
            when (Map.Tiles.getByType(it.type)) {
                Map.Tiles.Solid -> {
                    x = (((x + Main.TILE_SIZE / 2) / (Main.TILE_SIZE)).toInt()) * Main.TILE_SIZE.toFloat()
                    dx *= -1
                }
                else -> {
                }
            }
        }
        collisions.check(this, Collisions.Direction.LEFT) {
            when (Map.Tiles.getByType(it.type)) {
                Map.Tiles.Solid -> {
                    x = ((x + Main.TILE_SIZE / 2) / (Main.TILE_SIZE)).toInt() * Main.TILE_SIZE.toFloat()
                    dx *= -1
                }
                else -> {
                }
            }
        }

        dx = dx.coerceIn(-Main.MAX_ACC, Main.MAX_ACC)
        dy = dy.coerceIn(-Main.MAX_ACC, Main.MAX_ACC)

        x += dx //* elapsed
        y += dy //* elapsed

        if (x + Main.TILE_SIZE < 0 || x > map.width.toFloat() * Main.TILE_SIZE) dead = true
        if (y >= map.height.toFloat() * Main.TILE_SIZE) dead = true
    }

    fun draw(graphicLayer: CameraLayer) {
        graphicLayer.drawSprite(sprite, Main.TILE_SIZE, x, y)
    }
}