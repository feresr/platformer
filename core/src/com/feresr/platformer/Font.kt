package com.feresr.platformer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Pixmap
import org.w3c.dom.Element
import java.nio.ByteOrder
import javax.xml.parsers.DocumentBuilderFactory

class Font(private val fontName: String) {

    private val letters = mutableMapOf<Char, Character>()

    class Character(val pixels: IntArray, val width: Int, val height: Int)

    fun init(assetManager: AssetManager) {
        assetManager.load("$fontName.png", Pixmap::class.java)
        assetManager.finishLoading()

        val pixmap = assetManager.get("$fontName.png", Pixmap::class.java)
        val sprite = pixmap.pixels.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer()
        val spriteWidth = pixmap.width

        val font = Gdx.files.internal("data/$fontName.xml").file()

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(font)
        doc.documentElement.normalize()

        val chars = doc.getElementsByTagName(NODE_CHAR)
        val height = (doc.getElementsByTagName(NODE_FONT).item(0) as Element)
                .getAttribute(ATTRIBUTE_HEIGHT).toInt()

        for (i in 0 until chars.length) {
            val char = chars.item(i) as Element
            val rect = char.getAttribute(ATTRIBUTE_RECT).split(" ").map { it.toInt() }.toIntArray()
            val x = rect[0]
            val y = rect[1]
            val width = rect[2]

            val offsetY = char.getAttribute(ATTRIBUTE_OFFSET).split(" ")[1].toInt()
            val characterSprite = IntArray(width * height)

            var toIndex = 0
            for (k in y - offsetY until y + height - offsetY) {
                for (j in x until x + width) {
                    val fromIndex = k * spriteWidth + j
                    characterSprite[toIndex] = sprite[fromIndex]
                    toIndex++
                }
            }

            val code = char.getAttribute(ATTRIBUTE_CODE).toCharArray().first()
            letters[code] = Character(characterSprite, width, height)
        }
    }

    fun getCharacter(letter: Char): Character = this.letters[letter]!!

    companion object {
        const val ATTRIBUTE_OFFSET = "offset"
        const val ATTRIBUTE_CODE = "code"
        const val ATTRIBUTE_HEIGHT = "height"
        const val ATTRIBUTE_RECT = "rect"
        const val NODE_CHAR = "Char"
        const val NODE_FONT = "Font"
    }
}