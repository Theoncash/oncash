package `in`.oncash.oncash.View

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import `in`.oncash.oncash.R

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val SPLASH_DELAY: Long = 3000 // 3 seconds

            Handler().postDelayed({
                startActivity(Intent(this, Home::class.java))
                finish()
            }, SPLASH_DELAY)
        }
    }
