package com.example.madexam3game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.Random

class EnemySpaceship(private val context: Context) {
    private var enemySpaceship: Bitmap
    internal var ex: Int = 0
    internal var ey: Int = 0
    internal var enemyVelocity: Int = 0
    private val random: Random = Random()

    init {
        enemySpaceship = BitmapFactory.decodeResource(context.resources, R.drawable.alien)
        resetEnemySpaceship()
    }

    fun getEnemySpaceship(): Bitmap {
        return enemySpaceship
    }

    fun getEnemySpaceshipWidth(): Int {
        return enemySpaceship.width
    }

    fun getEnemySpaceshipHeight(): Int {
        return enemySpaceship.height
    }

    private fun resetEnemySpaceship() {
        ex = 200 + random.nextInt(400)
        ey = 0
        enemyVelocity = 14 + random.nextInt(10)
    }
}
