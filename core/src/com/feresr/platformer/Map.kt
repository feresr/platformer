package com.feresr.platformer

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Pixmap
import java.nio.ByteOrder


class Map(
        private val tiles: CharArray,
        val width: Int, val height: Int) {
    private val block = IntArray(Main.TILE_SIZE * Main.TILE_SIZE) { (0xFF000000).toInt() }
    private val jumpThrough = IntArray(Main.TILE_SIZE * Main.TILE_SIZE)
    private val coin = IntArray(Main.TILE_SIZE * Main.TILE_SIZE) { (0x22FFFFFF).toInt() }
    private val door = IntArray(Main.TILE_SIZE * Main.TILE_SIZE) { (0x22FFFFFF).toInt() }
    private val spikes = IntArray(Main.TILE_SIZE * Main.TILE_SIZE) { (0x22FFFFFF).toInt() }

    fun init(assetManager: AssetManager) {
        assetManager.load("tile.png", Pixmap::class.java)
        assetManager.load("jump.png", Pixmap::class.java)
        assetManager.load("coin.png", Pixmap::class.java)
        assetManager.load("door.png", Pixmap::class.java)
        assetManager.load("spikes.png", Pixmap::class.java)
        assetManager.finishLoading()

        val tilePixels = assetManager.get("tile.png", Pixmap::class.java).pixels
        tilePixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(block)

        val jumpPixels = assetManager.get("jump.png", Pixmap::class.java).pixels
        jumpPixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(jumpThrough)

        val coinPixels = assetManager.get("coin.png", Pixmap::class.java).pixels
        coinPixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(coin)

        val doorPixels = assetManager.get("door.png", Pixmap::class.java).pixels
        doorPixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(door)

        val spikesPixels = assetManager.get("spikes.png", Pixmap::class.java).pixels
        spikesPixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(spikes)
    }

    fun draw(graphicLayer: GraphicLayer, camera: Camera) {
        for (y in 0..Main.SCREEN_HEIGHT / Main.TILE_SIZE) {
            for (x in 0..Main.SCREEN_WIDTH / Main.TILE_SIZE) {
                val tile = ((y + (camera.offsetY / Main.TILE_SIZE).toInt()) * width + x + (camera.offsetX / Main.TILE_SIZE).toInt())
                if (tile >= tiles.size) continue
                if (tiles[tile] == 'X' || tiles[tile] == 'H') {
                    graphicLayer.drawSprite(
                            block,
                            Main.TILE_SIZE,
                            x * Main.TILE_SIZE - (camera.offsetX % Main.TILE_SIZE).toInt(),
                            y * Main.TILE_SIZE - (camera.offsetY % Main.TILE_SIZE).toInt()
                    )
                }
                if (tiles[tile] == 'O') {
                    if ((System.currentTimeMillis() / 400) % 2 == 0L) {
                        graphicLayer.drawSprite(
                                coin,
                                Main.TILE_SIZE,
                                x * Main.TILE_SIZE - (camera.offsetX % Main.TILE_SIZE).toInt(),
                                y * Main.TILE_SIZE - (camera.offsetY % Main.TILE_SIZE).toInt()
                        )
                    }
                }
                if (tiles[tile] == '-') {
                    graphicLayer.drawSprite(
                            jumpThrough,
                            Main.TILE_SIZE,
                            x * Main.TILE_SIZE - (camera.offsetX % Main.TILE_SIZE).toInt(),
                            y * Main.TILE_SIZE - (camera.offsetY % Main.TILE_SIZE).toInt()
                    )
                }
                if (tiles[tile] == 'D') {
                    graphicLayer.drawSprite(
                            door,
                            Main.TILE_SIZE,
                            x * Main.TILE_SIZE - (camera.offsetX % Main.TILE_SIZE).toInt(),
                            y * Main.TILE_SIZE - (camera.offsetY % Main.TILE_SIZE).toInt()
                    )
                }
                if (tiles[tile] == 'M') {
                    graphicLayer.drawSprite(
                            spikes,
                            Main.TILE_SIZE,
                            x * Main.TILE_SIZE - (camera.offsetX % Main.TILE_SIZE).toInt(),
                            y * Main.TILE_SIZE - (camera.offsetY % Main.TILE_SIZE).toInt()
                    )
                }
            }
        }
    }

    fun replaceTile(x: Int, y: Int, tile: Char) {
        tiles[y * (width) + x] = tile
    }

    fun getTile(x: Int, y: Int): Tile {
        if (x < 0 || x >= width || y < 0 || y >= height) return Tile(x, y, ' ')
        return Tile(x, y, tiles[y * (width) + x])
    }
}