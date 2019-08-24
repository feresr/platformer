package com.feresr.platformer

class Collisions(private val map : Map) {
    private val tiles = mutableSetOf<Tile>()

    fun check(player: Player,
              direction: Main.Direction,
              action: (Tile) -> Unit) {
        val x = player.x
        val y = player.y
        val w = Main.TILE_SIZE
        val h = Main.TILE_SIZE
        val dy = player.dy
        val dx = player.dx

        var x1 = 0f
        var y1 = 0f
        var x2 = 0f
        var y2 = 0f

        when (direction) {
            Main.Direction.UP -> {
                x1 = x + 2
                y1 = y
                x2 = x + w - 2
                y2 = y + dy
            }
            Main.Direction.DOWN -> {
                x1 = x + 2
                y1 = y + h
                x2 = x + w - 2
                y2 = y + h + dy
            }
            Main.Direction.LEFT -> {
                x1 = x + dx
                y1 = y + 2
                x2 = x
                y2 = y + h - 2
            }
            Main.Direction.RIGHT -> {
                x1 = x + w - 1 + dx
                y1 = y + 2
                x2 = x + w - 1
                y2 = y + h - 2
            }
        }

//        if (Main.DEBUG) {
//            foreground.drawLine(
//                    x1 - camera.offsetX,
//                    y1 - camera.offsetY,
//                    x2 - camera.offsetX,
//                    y2 - camera.offsetY, 255, 255, 0)
//        }

        //Transform to tile space
        val x1t = (x1 / Main.TILE_SIZE).toInt()
        val y1t = (y1 / Main.TILE_SIZE).toInt()
        val x2t = (x2 / Main.TILE_SIZE).toInt()
        val y2t = (y2 / Main.TILE_SIZE).toInt()

        with(tiles) {
            clear()
            add(Tile(x1t, y1t, map.getTile(x1t, y1t)))
            add(Tile(x1t, y2t, map.getTile(x1t, y2t)))
            add(Tile(x2t, y1t, map.getTile(x2t, y1t)))
            add(Tile(x2t, y2t, map.getTile(x2t, y2t)))
            forEach { action(it) }
        }
    }
}