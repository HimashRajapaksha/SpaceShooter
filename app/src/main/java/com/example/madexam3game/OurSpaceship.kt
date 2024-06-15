package com.example.madexam3game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.Random

class OurSpaceship(private val context: Context, private val screenWidth: Int, private val screenHeight: Int) {
    private var ourSpaceship: Bitmap
    internal var ox: Int = 0
    internal var oy: Int = 0
    var isAlive: Boolean = true
    private var ourVelocity: Int = 0
    private val random: Random = Random()

    init {
        ourSpaceship = BitmapFactory.decodeResource(context.resources, R.drawable.rocket)
        resetOurSpaceship()
    }

    fun getOurSpaceship(): Bitmap {
        return ourSpaceship
    }

    fun getOurSpaceshipWidth(): Int {
        return ourSpaceship.width
    }

    private fun resetOurSpaceship() {
        ox = random.nextInt(screenWidth)
        oy = screenHeight - ourSpaceship.height
        ourVelocity = 10 + random.nextInt(6)
    }
}