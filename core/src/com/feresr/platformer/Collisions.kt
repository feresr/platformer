package com.feresr.platformer

class Collisions(private val getTile: (x: Int, y: Int) -> Tile, private val graphicLayer: GraphicLayer) {
    private val tiles = mutableSetOf<Tile>()

    enum class Direction { UP, RIGHT, DOWN, LEFT }

    fun check(g1: GameObject,
              g2: GameObject,
              direction: Direction): Boolean {

        val x = g1.x
        val y = g1.y
        val w = Main.TILE_SIZE
        val h = Main.TILE_SIZE
        val dy = g1.dy
        val dx = g1.dx

        var x1 = 0f
        var y1 = 0f
        var x2 = 0f
        var y2 = 0f

        when (direction) {
            Direction.UP -> {
                x1 = x + 2
                y1 = y
                x2 = x + w - 2
                y2 = y + dy
            }
            Direction.DOWN -> {
                x1 = x + 2
                y1 = y + h
                x2 = x + w - 2
                y2 = y + h + dy
            }
            Direction.LEFT -> {
                x1 = x + dx
                y1 = y + 2
                x2 = x
                y2 = y + h - 2
            }
            Direction.RIGHT -> {
                x1 = x + w - 1 + dx
                y1 = y + 2
                x2 = x + w - 1
                y2 = y + h - 2
            }
        }

        val squareX1 = g2.x + g2.dx
        val squareY1 = g2.y + g2.dy

        val squareX2 = squareX1 + Main.TILE_SIZE
        val squareY2 = squareY1 + Main.TILE_SIZE

        if (Main.DEBUG) {
            graphicLayer.drawLine(
                    squareX1,
                    squareY1,
                    squareX2,
                    squareY2, 255, 0, 0)
        }

        return pointInSquare(x1, y1, squareX1, squareY1, squareX2, squareY2) ||
                pointInSquare(x1, y2, squareX1, squareY1, squareX2, squareY2) ||
                pointInSquare(x2, y1, squareX1, squareY1, squareX2, squareY2) ||
                pointInSquare(x2, y2, squareX1, squareY1, squareX2, squareY2)


    }

    private fun pointInSquare(px: Float, py: Float, x1: Float, y1: Float, x2: Float, y2: Float): Boolean {
        return px >= x1 && py >= y1 && px <= x2 && py <= y2
    }

    fun check(player: GameObject,
              direction: Direction,
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
            Direction.UP -> {
                x1 = x + 2
                y1 = y
                x2 = x + w - 2
                y2 = y + dy
            }
            Direction.DOWN -> {
                x1 = x + 2
                y1 = y + h
                x2 = x + w - 2
                y2 = y + h + dy
            }
            Direction.LEFT -> {
                x1 = x + dx
                y1 = y + 2
                x2 = x
                y2 = y + h - 2
            }
            Direction.RIGHT -> {
                x1 = x + w - 1 + dx
                y1 = y + 2
                x2 = x + w - 1
                y2 = y + h - 2
            }
        }

        if (Main.DEBUG) {
            graphicLayer.drawLine(
                    x1,
                    y1,
                    x2,
                    y2, 255, 255, 0)
        }

        //Transform to tile space
        val x1t = (x1 / Main.TILE_SIZE).toInt()
        val y1t = (y1 / Main.TILE_SIZE).toInt()
        val x2t = (x2 / Main.TILE_SIZE).toInt()
        val y2t = (y2 / Main.TILE_SIZE).toInt()

        with(tiles) {
            clear()
            add(getTile(x1t, y1t))
            add(getTile(x1t, y2t))
            add(getTile(x2t, y1t))
            add(getTile(x2t, y2t))
            forEach { action(it) }
        }
    }
}