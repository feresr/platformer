package com.feresr.platformer

import com.badlogic.gdx.graphics.Texture
import java.nio.ByteOrder


class Map(private val tiles: CharArray, val width: Int, val height: Int) {
    private val block = IntArray(Main.TILE_SIZE * Main.TILE_SIZE)
    private val jumpThrough = IntArray(Main.TILE_SIZE * Main.TILE_SIZE)
    private val coin = IntArray(Main.TILE_SIZE * Main.TILE_SIZE) { (0x22FFFFFF).toInt() }

    fun init() {
        val tile = Texture("tile.png").textureData
        tile.prepare()
        val tilePixels = tile.consumePixmap().pixels
        tilePixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(block)

        val jump = Texture("jump.png").textureData
        jump.prepare()
        val jumpPixels = jump.consumePixmap().pixels
        jumpPixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(jumpThrough)
    }

    fun draw(graphicLayer: GraphicLayer, camera: Camera) {
        for (y in 0..Main.SCREEN_HEIGHT / Main.TILE_SIZE) {
            for (x in 0..Main.SCREEN_WIDTH / Main.TILE_SIZE) {
                val tile = ((y + (camera.offsetY / Main.TILE_SIZE).toInt()) * width + x + (camera.offsetX / Main.TILE_SIZE).toInt())
                if (tile >= tiles.size) continue
                if (tiles[tile] == 'X') {
                    graphicLayer.drawSprite(
                            block,
                            Main.TILE_SIZE,
                            x * Main.TILE_SIZE - (camera.offsetX % Main.TILE_SIZE).toInt(),
                            y * Main.TILE_SIZE - (camera.offsetY % Main.TILE_SIZE).toInt()
                    )
                }
                if (tiles[tile] == 'O') {
                    graphicLayer.drawSprite(
                            if ((System.currentTimeMillis() / 200) % 2 == 0L) coin else block,
                            Main.TILE_SIZE,
                            x * Main.TILE_SIZE - (camera.offsetX % Main.TILE_SIZE).toInt(),
                            y * Main.TILE_SIZE - (camera.offsetY % Main.TILE_SIZE).toInt()
                    )
                }
                if (tiles[tile] == '-') {
                    graphicLayer.drawSprite(
                            jumpThrough,
                            Main.TILE_SIZE,
                            x * Main.TILE_SIZE - (camera.offsetX % Main.TILE_SIZE).toInt(),
                            y * Main.TILE_SIZE - (camera.offsetY % Main.TILE_SIZE).toInt()
                    )
                }
            }
        }
    }

    fun replaceTile(x : Int, y : Int, tile : Char) {
        tiles[y * (width) + x] = tile
    }

    fun getTile(x: Int, y: Int): Char {
        return tiles[y * (width) + x]
    }
}