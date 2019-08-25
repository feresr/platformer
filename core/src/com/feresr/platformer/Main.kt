package com.feresr.platformer

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20


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

    private var t1 = System.currentTimeMillis()
    private var t2 = System.currentTimeMillis()
    private var elapsed = 0L

    private lateinit var player: Player
    private val enemies: MutableList<Enemy> = mutableListOf()
    private val map: Map = Map(charArrayOf(
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', 'X', ' ', ' ', ' ', ' ', '-', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', 'X', ' ', ' ', ' ', '-', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', 'X', 'X', 'X', 'X',
            ' ', ' ', ' ', 'X', ' ', 'X', '-', 'X', 'X', 'X', 'X', 'X', '-', '-', '-', '-', 'X', 'X', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', '-', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', '-', 'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', '-', 'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', ' ', 'O', 'O', 'O', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', 'X', ' ', ' ', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' ', ' ', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            'X', ' ', ' ', ' ', 'X', ' ', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'X', ' ', ' ', 'X', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
            'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ', 'X'
    ), 28, 20)
    private lateinit var collisions: Collisions
    private val camera: Camera = Camera(map.width, map.height, 0f, 0f)
    private val debugLayer = CameraLayer(camera, SCREEN_WIDTH, SCREEN_HEIGHT)
    private val foreground = CameraLayer(camera, SCREEN_WIDTH, SCREEN_HEIGHT)
    private val background = GraphicLayer(SCREEN_WIDTH, SCREEN_HEIGHT)

    private val font = Font("pico8")

    override fun create() {
        font.init()
        player = Player(10f, 10f)
        enemies.add(Enemy(TILE_SIZE * 4f, TILE_SIZE * 4f))
        enemies.add(Enemy(TILE_SIZE * 10f, TILE_SIZE * 4f))
        enemies.add(Enemy(TILE_SIZE * 4f, TILE_SIZE * 8f))
        enemies.add(Enemy(TILE_SIZE * 6f, TILE_SIZE * 12f))
        enemies.add(Enemy(TILE_SIZE * 7f, TILE_SIZE * 12f))
        enemies.add(Enemy(TILE_SIZE * 14f, TILE_SIZE * 12f))
        enemies.add(Enemy(TILE_SIZE * 9f, TILE_SIZE * 12f))
        enemies.add(Enemy(TILE_SIZE * 4f, TILE_SIZE * 12f))
        enemies.add(Enemy(TILE_SIZE * 2f, TILE_SIZE * 15f))

        collisions = Collisions(map, debugLayer)
        map.init()
        foreground.init()
        background.init()
        if (DEBUG) debugLayer.init()
    }

    //test delete
    var gameover = false

    override fun render() {

        Gdx.gl.glClearColor(.5f, .87f, 1f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (gameover) return

        t2 = System.currentTimeMillis()
        elapsed = t2 - t1
        t1 = t2

        player.update(collisions, map)

        enemies.forEach {
            it.update(collisions, map)
            if (collisions.check(player, it, Collisions.Direction.DOWN)) {
                player.y = it.y - TILE_SIZE
                player.dy = -1.5f
                it.dead = true
            }
        }

        camera.follow(player)
        enemies.removeAll { it.dead }
        enemies.forEach {
            it.draw(foreground)
            if (collisions.check(it, player, Collisions.Direction.DOWN) ||
                    collisions.check(it, player, Collisions.Direction.LEFT) ||
                    collisions.check(it, player, Collisions.Direction.RIGHT)) {
                player.hurt()
                gameover = true
            }
        }

        map.draw(background, camera)
        player.draw(foreground)

        foreground.drawText(font, "\"good\"", player.x.toInt(), player.y.toInt() - TILE_SIZE - 4)
        background.render()
        foreground.render()
        if (DEBUG) {
            debugLayer.render()
            println(Gdx.graphics.framesPerSecond)
        }
    }

    override fun dispose() {
        foreground.dispose()
        background.dispose()
        super.dispose()
    }
}
