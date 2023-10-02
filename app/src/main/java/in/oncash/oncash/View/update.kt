package `in`.oncash.oncash.View

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import `in`.oncash.oncash.R
import `in`.oncash.oncash.ViewModel.home_viewModel

class update : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val homeViewmodel: home_viewModel by viewModels()
        val version = homeViewmodel.getVersionInfo().value
        val downloadButton = findViewById<Button>(R.id.download_button)
        downloadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(version!!.link))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            startActivity(intent)
        }

    }
}