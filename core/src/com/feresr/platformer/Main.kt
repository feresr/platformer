package com.feresr.platformer

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.feresr.platformer.Main.Direction.*
import kotlin.math.abs


class Main : ApplicationAdapter() {

    companion object {
        const val DEBUG = false
        const val SCREEN_WIDTH = 128
        const val SCREEN_HEIGHT = 128
        const val PLAYER_ACC = .048f
        const val TILE_SIZE = 8
        const val FRICTION = .95f
        const val GRAVITY = .12f//.012f
        const val MAX_ACC = 2.5f
    }

    private val foreground = Graphics(SCREEN_WIDTH, SCREEN_HEIGHT)
    private val background = Graphics(SCREEN_WIDTH, SCREEN_HEIGHT)
    private var t1 = System.currentTimeMillis()
    private var t2 = System.currentTimeMillis()
    private var elapsed = 0L

    private lateinit var player: Player
    private lateinit var map: Map
    private lateinit var collisions: Collisions
    private lateinit var camera: Camera

    override fun create() {
        player = Player(200f, 10f)
        map = Map(charArrayOf(
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', 'X', ' ', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', 'X', 'X', 'X', 'X',
                ' ', ' ', ' ', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', '-', '-', '-', '-', 'X', 'X', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', 'X', 'X', ' ', ' ', ' ', 'X', ' ', ' ', 'X', ' ', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', 'X', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', ' ', 'O', 'O', 'O', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', 'X', ' ', ' ', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' ', ' ', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', 'O', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                'X', 'X', 'X', 'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X'
        ), 28, 20)

        camera = Camera(map.width, map.height, 0f, 0f)
        collisions = Collisions(map)
        foreground.init()
        background.init()
    }

    enum class Direction { UP, RIGHT, DOWN, LEFT }


    override fun render() {
        t2 = System.currentTimeMillis()
        elapsed = t2 - t1
        t1 = t2

        player.dx *= FRICTION
        if (abs(player.dx) < PLAYER_ACC / 2f) player.dx = 0f
        player.dy += GRAVITY

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.moveRight()
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) player.moveLeft()
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) player.jump(MAX_ACC)

        player.inAir = true
        if (player.dy > 0) {
            collisions.check(player, DOWN) {
                when (it.type) {
                    'X', '-' -> {
                        player.y = (((player.y + MAX_ACC) / (TILE_SIZE)).toInt()) * TILE_SIZE.toFloat()
                        player.inAir = false
                        player.dy = 0f
                    }
                    'O' -> map.replaceTile(it.x, it.y, ' ')
                }
            }
        } else if (player.dy < 0) {
            collisions.check(player, UP) {
                when (it.type) {
                    'X' -> {
                        player.y = (((player.y + MAX_ACC) / (TILE_SIZE)).toInt()) * TILE_SIZE.toFloat()
                        player.dy = 0f
                        map.replaceTile(it.x, it.y, ' ')
                    }
                    'O' -> map.replaceTile(it.x, it.y, ' ')
                }

            }
        }

        collisions.check(player, RIGHT) {
            when (it.type) {
                'X' -> {
                    player.x = (((player.x + TILE_SIZE / 2) / (TILE_SIZE)).toInt()) * TILE_SIZE.toFloat()
                    player.dx = minOf(0f, player.dx)
                }
                'O' -> map.replaceTile(it.x, it.y, ' ')
            }
        }
        collisions.check(player, LEFT) {
            when (it.type) {
                'X' -> {
                    player.x = ((player.x + TILE_SIZE / 2) / (TILE_SIZE)).toInt() * TILE_SIZE.toFloat()
                    player.dx = maxOf(0f, player.dx)
                }
                'O' -> map.replaceTile(it.x, it.y, ' ')
            }
        }

        player.dx = player.dx.coerceIn(-MAX_ACC, MAX_ACC)
        player.dy = player.dy.coerceIn(-MAX_ACC, MAX_ACC)

        player.x += player.dx //* elapsed
        player.y += player.dy //* elapsed
        camera.follow(player)
        map.draw(background, camera)
        player.draw(foreground, camera)

        println(Gdx.graphics.framesPerSecond)
        Gdx.gl.glClearColor(.5f, .87f, 1f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        background.render()
        foreground.render()

    }

    override fun dispose() {
        foreground.dispose()
        background.dispose()
        super.dispose()
    }
}
