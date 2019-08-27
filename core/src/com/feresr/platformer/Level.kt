package com.feresr.platformer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.utils.JsonReader
import com.feresr.platformer.Main.Companion.SCREEN_WIDTH


class GameOver : Level(
        -1,
        Map(IntArray(SCREEN_WIDTH * Main.SCREEN_HEIGHT), SCREEN_WIDTH, Main.SCREEN_HEIGHT),
        mutableListOf()
)

open class Level(
        val id: Int,
        val map: Map,
        val enemies: MutableList<Enemy>
) {
    fun init(assetManager: AssetManager) {
        map.init(assetManager)
        enemies.forEach { it.init(assetManager) }
    }

    companion object {
        private const val LAYERS = "layers"
        private const val DATA = "data"
        private const val WIDTH = "width"
        private const val HEIGHT = "height"
        private const val OBJECTS = "objects"

        fun build(level: Int): Level {
            val root = JsonReader().parse(Gdx.files.internal("data/maps/map$level.json"))

            val tileLayer = root.get(LAYERS)[0]
            val enemyLayer = root.get(LAYERS)[1]

            val map = Map(
                    tileLayer[DATA].asIntArray(),
                    tileLayer[WIDTH].asInt(),
                    tileLayer[HEIGHT].asInt()
            )

            val enemies = enemyLayer[OBJECTS].map {
                Enemy(it.get("x").asFloat(), it.get("y").asFloat())
            }.toMutableList()

            return Level(level, map, enemies)
        }
    }
}

class MainMenu : Level(-1,
        Map(intArrayOf(
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
                0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1
        ), 26, 16),
        mutableListOf(
                Enemy(Main.TILE_SIZE * 4f, Main.TILE_SIZE * 4f),
                Enemy(Main.TILE_SIZE * 6f, Main.TILE_SIZE * 12f),
                Enemy(Main.TILE_SIZE * 2f, Main.TILE_SIZE * 10f)
        )
)