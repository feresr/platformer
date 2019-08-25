package com.feresr.platformer

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
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

    private val assetManager = AssetManager()

    private lateinit var player: Player

    private var currentLevel: Level = Level1()

    private lateinit var collisions: Collisions
    private val camera: Camera by lazy { Camera(currentLevel.map.width, currentLevel.map.height, 0f, 0f) }
    private val debugLayer = CameraLayer(camera, SCREEN_WIDTH, SCREEN_HEIGHT)
    private val foreground = CameraLayer(camera, SCREEN_WIDTH, SCREEN_HEIGHT)
    private val background = GraphicLayer(SCREEN_WIDTH, SCREEN_HEIGHT)

    private val font = Font("pico8small")
    private val fontL = Font("pico8")

    override fun create() {
        font.init(assetManager)
        fontL.init(assetManager)
        player = Player(10f, 10f, door = {
            player.x = 10f
            player.y = 10f
            val nextLevel = if (currentLevel is Level2) Level1() else Level2()
            nextLevel.let {
                currentLevel = it
                currentLevel.init(assetManager)
            }
        })
        collisions = Collisions({ x, y -> currentLevel.map.getTile(x, y) }, debugLayer)
        currentLevel.init(assetManager)
        foreground.init()
        background.init()
        if (DEBUG) debugLayer.init()
    }

    //test delete
    var gameover = false

    override fun render() {
        if (assetManager.update()) {
            Gdx.gl.glClearColor(.5f, .87f, 1f, 0f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            if (gameover) return

            t2 = System.currentTimeMillis()
            elapsed = t2 - t1
            t1 = t2

            player.update(collisions, currentLevel.map)

            currentLevel.enemies.forEach {
                it.update(collisions, currentLevel.map)
                if (collisions.check(player, it, Collisions.Direction.DOWN)) {
                    player.y = it.y - TILE_SIZE
                    player.dy = -1.5f
                    it.dead = true
                }
            }

            camera.follow(player)
            currentLevel.enemies.removeAll { it.dead }
            currentLevel.enemies.forEach {
                it.draw(foreground)
                if (collisions.check(it, player, Collisions.Direction.DOWN) ||
                        collisions.check(it, player, Collisions.Direction.LEFT) ||
                        collisions.check(it, player, Collisions.Direction.RIGHT)) {
                    player.hurt()
                    gameover = true
                }
            }

            currentLevel.map.draw(background, camera)
            player.draw(foreground)

            foreground.drawText(
                    font,
                    "HELLO WORLD :)",
                    player.x.toInt() + TILE_SIZE / 2,
                    player.y.toInt() - TILE_SIZE - 4,
                    0x70AF0F,
                    1
            )
            background.render()
            foreground.render()
            if (DEBUG) {
                background.drawText(fontL, "FPS: ${Gdx.graphics.framesPerSecond}", 30, 0, 1)
                debugLayer.render()
            }
        } else {
            //TODO show loading assets progress manager.getProgress()
        }
    }

    override fun dispose() {
        foreground.dispose()
        background.dispose()
        if (DEBUG) debugLayer.dispose()
        assetManager.dispose()
        super.dispose()
    }
}
