package com.example.madexam3game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Explosion(context: Context, internal val eX: Int, internal val eY: Int) {
    private val explosion: Array<Bitmap?> = arrayOfNulls(8)
    private val explosionSize: Int = 300 // Define the desired size for the explosion bitmaps
    internal var explosionFrame: Int = 0 // Add explosionFrame variable and initialize it to 0

    init {
        explosion[0] = getScaledBitmap(context, R.drawable.blast1, explosionSize)
        explosion[1] = getScaledBitmap(context, R.drawable.blast2, explosionSize)
        explosion[2] = getScaledBitmap(context, R.drawable.blast3, explosionSize)
        explosion[3] = getScaledBitmap(context, R.drawable.blast4, explosionSize)
        explosion[4] = getScaledBitmap(context, R.drawable.blast5, explosionSize)
        explosion[5] = getScaledBitmap(context, R.drawable.blast6, explosionSize)
        explosion[6] = getScaledBitmap(context, R.drawable.blast7, explosionSize)
        explosion[7] = getScaledBitmap(context, R.drawable.blast8, explosionSize)
    }

    private fun getScaledBitmap(context: Context, resourceId: Int, size: Int): Bitmap {
        val originalBitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        return Bitmap.createScaledBitmap(originalBitmap, size, size, false)
    }

    fun getExplosion(): Bitmap? {
        return explosion.getOrNull(explosionFrame) // Return the bitmap at the current explosionFrame index
    }
}
