package com.feresr.platformer

class CameraLayer(private val camera: Camera, width: Int, height: Int) : GraphicLayer(width, height) {

    fun drawSprite(sprite: IntArray, w: Int, x: Float, y: Float, flipHorizontal: Boolean = false) {
        super.drawSprite(sprite, w,
                x.toInt() - camera.offsetX.toInt(),
                y.toInt() - camera.offsetY.toInt(),
                flipHorizontal
        )
    }

    override fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, r: Int, g: Int, b: Int) {
        super.drawLine(x1 - camera.offsetX,
                y1 - camera.offsetY,
                x2 - camera.offsetX,
                y2 - camera.offsetY, r, g, b)
    }
}