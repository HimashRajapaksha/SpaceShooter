package com.example.madexam3game
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.content.Intent

import java.util.*

class SpaceShooter : View {
    private var context: Context
    private var background: Bitmap
    private var lifeImage: Bitmap
    private var handler: Handler?
    private val UPDATE_MILLIS: Long = 30
    private var points = 0
    private var life = 3
    private var scorePaint: Paint
    private val TEXT_SIZE = 80
    private var paused = false
    private lateinit var ourSpaceship: OurSpaceship
    private lateinit var enemySpaceship: EnemySpaceship
    private val random: Random = Random()
    private val enemyShots: ArrayList<Shot> = ArrayList()
    private val ourShots: ArrayList<Shot> = ArrayList()
    private var enemyExplosion = false
    private lateinit var explosion: Explosion
    private val explosions: ArrayList<Explosion> = ArrayList()
    private var enemyShotAction = false
    private val screenWidth: Int
    private val screenHeight: Int
    private val runnable = Runnable { invalidate() }

    // Additional properties for continuous enemy shooting
    private var lastEnemyShotTime: Long = 0
    private val enemyShotInterval: Long = 1000 // Adjust as needed (milliseconds)

    constructor(context: Context) : super(context) {
        this.context = context
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y
        ourSpaceship = OurSpaceship(context, screenWidth, screenHeight)
        enemySpaceship = EnemySpaceship(context)
        background = decodeSampledBitmapFromResource(resources, R.drawable.background, screenWidth, screenHeight)
        lifeImage = decodeSampledBitmapFromResource(resources, R.drawable.life, screenWidth / 50, screenHeight / 50)
        handler = Handler()
        scorePaint = Paint()
        scorePaint.color = Color.RED
        scorePaint.textSize = TEXT_SIZE.toFloat()
        scorePaint.textAlign = Paint.Align.LEFT
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.context = context
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y
        ourSpaceship = OurSpaceship(context, screenWidth, screenHeight)
        enemySpaceship = EnemySpaceship(context)
        background = decodeSampledBitmapFromResource(resources, R.drawable.background, screenWidth, screenHeight)
        lifeImage = decodeSampledBitmapFromResource(resources, R.drawable.life, screenWidth / 10, screenHeight / 10)
        handler = Handler()
        scorePaint = Paint()
        scorePaint.color = Color.RED
        scorePaint.textSize = TEXT_SIZE.toFloat()
        scorePaint.textAlign = Paint.Align.LEFT
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(background, 0f, 0f, null)
        canvas.drawText("Pt: $points", 0f, TEXT_SIZE.toFloat(), scorePaint)
        for (i in life downTo 1) {
            canvas.drawBitmap(lifeImage, (screenWidth - lifeImage.width * i).toFloat(), 0f, null)
        }
        if (life == 0) {
            paused = true
            handler = null
            updateHighScore(points) // Update the high score
            val intent = Intent(context, GameOver::class.java)
           intent.putExtra("Points", points)
           context.startActivity(intent)
            (context as Activity).finish()
        }
        enemySpaceship.ex += enemySpaceship.enemyVelocity
        if (enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() >= screenWidth) {
            enemySpaceship.enemyVelocity *= -1
        }
        if (enemySpaceship.ex <= 0) {
            enemySpaceship.enemyVelocity *= -1
        }

        // Continuous shooting logic for enemy spaceship
        shootEnemy()

        if (!enemyExplosion) {
            canvas.drawBitmap(enemySpaceship.getEnemySpaceship(), enemySpaceship.ex.toFloat(), enemySpaceship.ey.toFloat(), null)
        }

        if (ourSpaceship.isAlive) {
            if (ourSpaceship.ox > screenWidth - ourSpaceship.getOurSpaceshipWidth()) {
                ourSpaceship.ox = (screenWidth - ourSpaceship.getOurSpaceshipWidth()).toFloat().toInt()
            } else if (ourSpaceship.ox < 0) {
                ourSpaceship.ox = 0
            }
            canvas.drawBitmap(ourSpaceship.getOurSpaceship(), ourSpaceship.ox.toFloat(), ourSpaceship.oy.toFloat(), null)
        }
        // Loop over enemyShots in reverse order
        for (i in enemyShots.size - 1 downTo 0) {
            val shot = enemyShots[i]
            shot.shy += 15
            canvas.drawBitmap(shot.getShot(), shot.shx.toFloat(), shot.shy.toFloat(), null)
            if (shot.shx >= ourSpaceship.ox &&
                shot.shx <= ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth() &&
                shot.shy >= ourSpaceship.oy &&
                shot.shy <= screenHeight) {
                life--
                enemyShots.removeAt(i)
                explosion = Explosion(context, ourSpaceship.ox.toInt(), ourSpaceship.oy.toInt())
                explosions.add(explosion)
            } else if (shot.shy >= screenHeight) {
                enemyShots.removeAt(i)
            }
        }

// Loop over ourShots in reverse order
        for (i in ourShots.size - 1 downTo 0) {
            val shot = ourShots[i]
            shot.shy -= 15
            canvas.drawBitmap(shot.getShot(), shot.shx.toFloat(), shot.shy.toFloat(), null)
            if (shot.shx >= enemySpaceship.ex &&
                shot.shx <= enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() &&
                shot.shy <= enemySpaceship.getEnemySpaceshipHeight() &&
                shot.shy >= enemySpaceship.ey) {
                points++
                ourShots.removeAt(i)
                explosion = Explosion(context, enemySpaceship.ex.toInt(), enemySpaceship.ey.toInt())
                explosions.add(explosion)
            } else if (shot.shy <= 0) {
                ourShots.removeAt(i)
            }
        }

        val explosionsToRemove = ArrayList<Explosion>()
        for (i in 0 until explosions.size) {
            val explosion = explosions[i]
            canvas.drawBitmap(
                explosion.getExplosion() ?: continue,
                explosion.eX.toFloat(),
                explosion.eY.toFloat(),
                null
            )
            explosion.explosionFrame++
            if (explosion.explosionFrame > 7) { // Adjusted to match the array size
                explosionsToRemove.add(explosion)
            }
        }
        explosions.removeAll(explosionsToRemove)
        if (!paused) handler?.postDelayed(runnable, UPDATE_MILLIS)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x.toInt()
        if (event.action == MotionEvent.ACTION_UP) {
            if (ourShots.size < 3) {
                val ourShot = Shot(context, ourSpaceship.ox.toInt() + ourSpaceship.getOurSpaceshipWidth() / 2, ourSpaceship.oy.toInt())
                ourShots.add(ourShot)
            }
        }
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            ourSpaceship.ox = touchX.toFloat().toInt()
        }
        return true
    }

    private fun shootEnemy() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastEnemyShotTime

        // Shoot only if enough time has elapsed since the last shot
        if (elapsedTime >= enemyShotInterval) {
            lastEnemyShotTime = currentTime
            val enemyShot = Shot(context, enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() / 2, enemySpaceship.ey)
            enemyShots.add(enemyShot)
        }
    }
    // Save the high score
    private fun saveHighScore(score: Int) {
        val prefs = context.getSharedPreferences("HighScore", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt("HighScore", score)
        editor.apply()
    }

    // Retrieve the high score
    private fun getHighScore(): Int {
        val prefs = context.getSharedPreferences("HighScore", Context.MODE_PRIVATE)
        return prefs.getInt("HighScore", 0) // 0 is the default value if the high score is not found
    }

    // Update the high score
    private fun updateHighScore(newScore: Int) {
        val currentHighScore = getHighScore()
        if (newScore > currentHighScore) {
            saveHighScore(newScore)
        }
    }


    private fun decodeSampledBitmapFromResource(resources: Resources, resId: Int, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, resId, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(resources, resId, options)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
