package com.feresr.platformer

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.feresr.platformer.Main.Direction.*
import java.nio.ByteOrder
import kotlin.math.abs


class Main : ApplicationAdapter() {

    companion object {
        const val DEBUG = true
        const val SCREEN_WIDTH = 128
        const val SCREEN_HEIGHT = 128
        const val PLAYER_ACC = .044f
        const val TILE_SIZE = 8
        const val FRICTION = .96f
        const val GRAVITY = .12f//.012f
        const val MAX_ACC = 2.5f
    }

    private val graphics = Graphics(SCREEN_WIDTH, SCREEN_HEIGHT)
    private var t1 = System.currentTimeMillis()
    private var t2 = System.currentTimeMillis()
    private var elapsed = 0L

    class Player(
            val sprite: IntArray,
            var x: Float,
            var y: Float,
            var dy: Float = 0f,
            var dx: Float = 0f,
            var inAir: Boolean = true,
            var flipHorizontal: Boolean = false
    )

    private lateinit var player: Player //= Player(IntArray(TILE_SIZE * TILE_SIZE) { (0xFF3555FF).toInt() }, 10f, 10f)

    data class Camera(val mapWidth: Int, val mapHeight: Int, var x: Float, var y: Float) {
        val offsetX
            get() = x - (SCREEN_WIDTH) / 2
        val offsetY
            get() = y - (SCREEN_HEIGHT) / 2

        fun follow(player: Player) {
            x = player.x.coerceIn(SCREEN_WIDTH / 2f, mapWidth * TILE_SIZE - SCREEN_WIDTH / 2f) //maxOf(player.x, )
            y = player.y.coerceIn(SCREEN_HEIGHT / 2f, mapHeight * TILE_SIZE - SCREEN_HEIGHT / 2f) //maxOf(player.x, )
        }
    }

    class Map(val tiles: CharArray, val width: Int, val height: Int)

    private val map = Map(charArrayOf(
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', 'X', ' ', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', 'X', 'X', 'X', 'X', 'X',
            ' ', ' ', ' ', ' ', ' ', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', 'X', ' ', ' ', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' ', ' ', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            'X', 'X', 'X', 'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X'
    ), 28, 20)
    private val camera: Camera = Camera(map.width, map.height, 0f, 0f)

    private lateinit var texture: Texture
    private val block = IntArray(TILE_SIZE * TILE_SIZE) { (0xaaFF00FF).toInt() }
    override fun create() {

        texture = Texture("hero.png")
        texture.textureData.prepare()
        val pixels = texture.textureData.consumePixmap().pixels

        val array = IntArray(pixels.asIntBuffer().remaining())
        pixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(array)

        player = Player(array, 10f, 10f)

        val tile = Texture("tile.png").textureData
        tile.prepare()
        val tilePixels = tile.consumePixmap().pixels
        tilePixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(block)

        graphics.init()
    }

    enum class Direction { UP, RIGHT, DOWN, LEFT }

    private fun checkCollision(player: Player,
                               direction: Direction
                               //,action: (Char) -> Unit
    ): Boolean {
        val x = player.x
        val y = player.y
        val w = TILE_SIZE
        val h = TILE_SIZE

        var x1 = 0f
        var y1 = 0f
        var x2 = 0f
        var y2 = 0f

        when (direction) {
            UP -> {
                x1 = x + 2
                y1 = y
                x2 = x + w - 2
                y2 = y + player.dy
            }
            DOWN -> {
                x1 = x + 2
                y1 = y + h
                x2 = x + w - 2
                y2 = y + h + player.dy
            }
            LEFT -> {
                x1 = x + player.dx
                y1 = y + 2
                x2 = x
                y2 = y + h - 2
            }
            RIGHT -> {
                x1 = x + w - 1 + player.dx
                y1 = y + 2
                x2 = x + w - 1
                y2 = y + h - 2
            }

        }

        if (DEBUG) {
            graphics.drawLine(
                    x1 - camera.offsetX,
                    y1 - camera.offsetY,
                    x2 - camera.offsetX,
                    y2 - camera.offsetY, 255, 255, 0)
        }

        val tiles = mutableSetOf<Char>()
        tiles.add(mget(x1, y1))
        tiles.add(mget(x1, y2))
        tiles.add(mget(x2, y1))
        tiles.add(mget(x2, y2))

        //tiles.forEach { action(it) }

        return tiles.any { it != ' ' }
    }

    override fun render() {
        t2 = System.currentTimeMillis()
        elapsed = t2 - t1
        t1 = t2

        player.dx *= FRICTION
        if (abs(player.dx) < PLAYER_ACC / 2f) player.dx = 0f
        player.dy += GRAVITY

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.dx += PLAYER_ACC
            player.flipHorizontal = false
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.flipHorizontal = true
            player.dx -= PLAYER_ACC
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (!player.inAir) {
                player.dy -= MAX_ACC
                player.inAir = true
            }
        }

        player.inAir = true
        if (player.dy > 0) {
            if (checkCollision(player, DOWN)) {
                player.y = (((player.y + MAX_ACC) / (TILE_SIZE)).toInt()) * TILE_SIZE.toFloat()
                player.inAir = false
                player.dy = 0f
            }
        } else if (player.dy < 0) {
            if (checkCollision(player, UP)) {
                player.y = (((player.y + MAX_ACC) / (TILE_SIZE)).toInt()) * TILE_SIZE.toFloat()
                player.dy = 0f
            }
        }

        if (player.dx > 0) {
            if (checkCollision(player, RIGHT)) {
                player.x = (((player.x + MAX_ACC) / (TILE_SIZE)).toInt()) * TILE_SIZE.toFloat()
                player.dx = 0f
            }
        } else if (player.dx < 0) {
            if (checkCollision(player, LEFT)) {
                player.x = (((player.x + MAX_ACC) / (TILE_SIZE)).toInt()) * TILE_SIZE.toFloat()
                player.dx = 0f
            }
        }

        player.dx = player.dx.coerceIn(-MAX_ACC, MAX_ACC)
        player.dy = player.dy.coerceIn(-MAX_ACC, MAX_ACC)

        player.x += player.dx //* elapsed
        player.y += player.dy //* elapsed
        camera.follow(player)
        drawMap()
        graphics.drawSprite(player.sprite, TILE_SIZE,
                (player.x - camera.offsetX).toInt(),
                (player.y - camera.offsetY).toInt(),
                player.flipHorizontal
        )


        println(Gdx.graphics.framesPerSecond)
        graphics.render()
    }

    private fun drawMap() {
        for (y in 0..SCREEN_HEIGHT / TILE_SIZE) {
            for (x in 0..SCREEN_WIDTH / TILE_SIZE) {
                val tile = ((y + (camera.offsetY / TILE_SIZE).toInt()) * map.width + x + (camera.offsetX / TILE_SIZE).toInt())
                if (tile >= map.tiles.size) continue
                if (map.tiles[tile] == 'X') {
                    graphics.drawSprite(
                            block,
                            TILE_SIZE,
                            x * TILE_SIZE - (camera.offsetX % TILE_SIZE).toInt(),
                            y * TILE_SIZE - (camera.offsetY % TILE_SIZE).toInt()
                    )
                }
            }
        }
    }

    override fun dispose() {
        graphics.dispose()
        super.dispose()
    }

    private fun mget(x: Float, y: Float): Char {
        return map.tiles[getTileForPosition(x, y)]
    }

    private fun getTileForPosition(x: Float, y: Float): Int {
        return ((y / TILE_SIZE).toInt()) * (map.width) + (x / TILE_SIZE).toInt()
    }
}
