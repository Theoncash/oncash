package `in`.oncash.oncash.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import `in`.oncash.oncash.databinding.ActivityReferalBinding

class ReferalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding= ActivityReferalBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



    }
}