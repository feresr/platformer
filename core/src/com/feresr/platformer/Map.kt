package com.feresr.platformer

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Pixmap
import com.feresr.platformer.Map.Tiles.*
import java.nio.ByteOrder


class Map(private val tiles: IntArray, val width: Int, val height: Int) {

    enum class Tiles(val type: Int) {
        Solid(1), Jump(2), Coin(3), Door(4), Spikes(5);
        companion object {
            fun getByType(t: Int): Tiles? = values().find { it.type == t }
        }
    }

    private val sprites: MutableMap<Tiles, IntArray> = mutableMapOf()

    fun init(assetManager: AssetManager) {
        values().forEach { assetManager.load("${it.name}.png", Pixmap::class.java) }
        assetManager.finishLoading()

        values().forEach {
            val tilePixels = assetManager.get("${it.name}.png", Pixmap::class.java).pixels
            val array = IntArray(Main.TILE_SIZE * Main.TILE_SIZE)
            tilePixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(array)
            sprites[it] = array
        }
    }

    fun draw(graphicLayer: GraphicLayer, camera: Camera) {
        fun drawTile(t: Tiles, x: Int, y: Int) {
            graphicLayer.drawSprite(
                    sprites[t]!!,
                    Main.TILE_SIZE,
                    x * Main.TILE_SIZE - (camera.offsetX % Main.TILE_SIZE).toInt(),
                    y * Main.TILE_SIZE - (camera.offsetY % Main.TILE_SIZE).toInt()
            )
        }

        for (y in 0..Main.SCREEN_HEIGHT / Main.TILE_SIZE) {
            for (x in 0..Main.SCREEN_WIDTH / Main.TILE_SIZE) {
                val tile = ((y + (camera.offsetY / Main.TILE_SIZE).toInt()) * width + x + (camera.offsetX / Main.TILE_SIZE).toInt())
                if (tile >= tiles.size) continue
                when (val t = Tiles.getByType(tiles[tile])) {
                    Solid, Jump, Door, Spikes -> drawTile(t, x, y)
                    Coin -> if ((System.currentTimeMillis() / 400) % 2 == 0L) drawTile(t, x, y)
                }
            }
        }
    }

    fun replaceTile(tile: Tile) {
        tiles[tile.y * (width) + tile.x] = tile.type
    }

    fun getTile(x: Int, y: Int): Tile {
        if (x < 0 || x >= width || y < 0 || y >= height) return Tile(x, y, 0)
        return Tile(x, y, tiles[y * (width) + x])
    }
}