package `in`.oncash.oncash.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import `in`.oncash.oncash.databinding.ActivityLeaderBoardBinding

class LeaderBoard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val binding = ActivityLeaderBoardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButtonLb.setOnClickListener {
            startActivity(Intent(this,Home::class.java))
        }



    }
}

