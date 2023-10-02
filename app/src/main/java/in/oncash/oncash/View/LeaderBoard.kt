package `in`.oncash.oncash.View

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.cardview.widget.CardView
import com.google.android.material.internal.ContextUtils.getActivity
import com.skydoves.transformationlayout.TransformationLayout
import `in`.oncash.oncash.Fragment.profile_fragment
import `in`.oncash.oncash.R
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

