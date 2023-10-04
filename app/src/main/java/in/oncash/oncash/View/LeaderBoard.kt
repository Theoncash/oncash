package `in`.oncash.oncash.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.oncash.oncash.Component.Offer_RecylerViewAdapter
import `in`.oncash.oncash.Component.leaderBoard_RecylerViewAdapter
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.R
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.ViewModel.home_viewModel

import `in`.oncash.oncash.databinding.ActivityLeaderBoardBinding
import kotlinx.coroutines.launch

class LeaderBoard() : AppCompatActivity() {
    private val homeViewmodel: home_viewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        val binding = ActivityLeaderBoardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButtonLb.setOnClickListener {
            finish() // Finish the activity to return to the Fragment
        }

            val leaderBoard: RecyclerView = findViewById(R.id.leaderBoard_recyclerView)
            val adapter = leaderBoard_RecylerViewAdapter(  )
            leaderBoard.adapter = adapter
            leaderBoard.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


            lifecycleScope.launch {
                UserInfo_Airtable_Repo().getLeaderBoard().observe(this@LeaderBoard){
                    adapter.updateList(it)
                }

            }

        }



    }


