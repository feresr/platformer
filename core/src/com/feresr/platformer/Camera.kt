package com.feresr.platformer

data class Camera(val mapWidth: Int, val mapHeight: Int, var x: Float, var y: Float) {
    val offsetX
        get() = x - (Main.SCREEN_WIDTH) / 2
    val offsetY
        get() = y - (Main.SCREEN_HEIGHT) / 2

    fun follow(player: Player) {
        x = (player.x + Main.TILE_SIZE / 2).coerceIn(Main.SCREEN_WIDTH / 2f, mapWidth * Main.TILE_SIZE - Main.SCREEN_WIDTH / 2f) //maxOf(player.x, )
        y = (player.y + Main.TILE_SIZE / 2).coerceIn(Main.SCREEN_HEIGHT / 2f, mapHeight * Main.TILE_SIZE - Main.SCREEN_HEIGHT / 2f) //maxOf(player.x, )
    }
}