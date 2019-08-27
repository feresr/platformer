package com.feresr.platformer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Pixmap
import java.nio.ByteOrder
import kotlin.math.abs

class Player(
        x: Float,
        y: Float,
        dy: Float = 0f,
        dx: Float = 0f,
        val door: (Tile) -> Unit,
        val hurt: () -> Unit,
        var inAir: Boolean = true
) : GameObject(x, y, dx, dy) {
    private var flipHorizontal: Boolean = false
    private lateinit var sprite: IntArray
    private lateinit var sprite2: IntArray
    private lateinit var current: IntArray

    private lateinit var sound: Sound

    fun init(assetManager: AssetManager) {
        assetManager.load("hero.png", Pixmap::class.java)
        assetManager.load("hero2.png", Pixmap::class.java)
        assetManager.finishLoading()

        val pixels = assetManager.get("hero.png", Pixmap::class.java).pixels
        sprite = IntArray(pixels.asIntBuffer().remaining())
        pixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(sprite)

        val pixels2 = assetManager.get("hero2.png", Pixmap::class.java).pixels
        sprite2 = IntArray(pixels.asIntBuffer().remaining())
        pixels2.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(sprite2)
        current = sprite
        sound = Gdx.audio.newSound(Gdx.files.internal("jump.wav"))
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
                when (Map.Tiles.getByType(it.type)) {
                    Map.Tiles.Solid,
                    Map.Tiles.Jump -> {
                        y = (((y + Main.MAX_ACC) / (Main.TILE_SIZE)).toInt()) * Main.TILE_SIZE.toFloat()
                        inAir = false
                        dy = 0f
                    }
                    Map.Tiles.Coin -> map.replaceTile(Tile(it.x, it.y, 0))
                    Map.Tiles.Spikes -> hurt()
                    else -> {}
                }
            }
        } else if (dy < 0) {
            collisions.check(this, Collisions.Direction.UP) {
                when (Map.Tiles.getByType(it.type)) {
                    Map.Tiles.Solid -> {
                        y = (((y + Main.MAX_ACC) / (Main.TILE_SIZE)).toInt()) * Main.TILE_SIZE.toFloat()
                        dy = 0f
                        map.replaceTile(Tile(it.x, it.y, 0))
                        sound.stop()
                    }
                    Map.Tiles.Coin -> map.replaceTile(Tile(it.x, it.y, 0))
                    else -> {}
                }

            }
        }

        collisions.check(this, Collisions.Direction.RIGHT) {
            when (Map.Tiles.getByType(it.type)) {
                Map.Tiles.Solid -> {
                    x = (((x + Main.TILE_SIZE / 2) / (Main.TILE_SIZE)).toInt()) * Main.TILE_SIZE.toFloat()
                    dx = minOf(0f, dx)
                }
                Map.Tiles.Door -> door(it)
                Map.Tiles.Coin -> map.replaceTile(Tile(it.x, it.y, 0))
                else -> {}
            }
        }
        collisions.check(this, Collisions.Direction.LEFT) {
            when (Map.Tiles.getByType(it.type)) {
                Map.Tiles.Solid ->{
                    x = ((x + Main.TILE_SIZE / 2) / (Main.TILE_SIZE)).toInt() * Main.TILE_SIZE.toFloat()
                    dx = maxOf(0f, dx)
                }
                Map.Tiles.Door -> door(it)
                Map.Tiles.Coin -> map.replaceTile(Tile(it.x, it.y, 0))
                else -> {}
            }
        }

        dx = dx.coerceIn(-Main.MAX_ACC, Main.MAX_ACC)
        dy = dy.coerceIn(-Main.MAX_ACC, Main.MAX_ACC)

        x += dx //* elapsed
        y += dy //* elapsed

        x = x.coerceIn(0f, map.width.toFloat() * Main.TILE_SIZE - Main.TILE_SIZE)
        if (y >= map.height.toFloat() * Main.TILE_SIZE) hurt()
    }

    fun draw(layer: CameraLayer) {
        layer.drawSprite(current, Main.TILE_SIZE, x, y, flipHorizontal)
        current = sprite
    }

    private fun jump(amount: Float) {
        if (!inAir) {
            dy -= amount
            inAir = true
            sound.play(.2f)
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