package com.example.madexam3game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Shot(context: Context, var shx: Int, var shy: Int) {
    private var shot: Bitmap

    init {
        shot = BitmapFactory.decodeResource(context.resources, R.drawable.shoot)
    }

    fun getShot(): Bitmap {
        return shot
    }
}
