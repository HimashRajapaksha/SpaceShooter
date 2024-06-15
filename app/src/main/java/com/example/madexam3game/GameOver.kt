package com.example.madexam3game

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {
    private lateinit var tvPoints: TextView
    private lateinit var tvHighScore: TextView // New TextView for displaying high score

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)

        // Retrieve points from intent
        val points = intent.extras?.getInt("Points") ?: 0

        // Retrieve and display high score
        val highScore = getHighScore()
        tvHighScore = findViewById(R.id.tvHighScore)
        tvHighScore.text = "High Score: $highScore"

        // Display points
        tvPoints = findViewById(R.id.tvPoints)
        tvPoints.text = points.toString()
    }

    // Retrieve the high score
    private fun getHighScore(): Int {
        val prefs = applicationContext.getSharedPreferences("HighScore", MODE_PRIVATE)
        return prefs.getInt("HighScore", 0) // 0 is the default value if the high score is not found
    }

    fun restart(v: View) {
        val intent = Intent(this, StartUp::class.java)
        startActivity(intent)
        finish()
    }

    fun exit(v: View) {
        finish()
    }
}