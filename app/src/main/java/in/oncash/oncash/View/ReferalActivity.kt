package `in`.oncash.oncash.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import `in`.oncash.oncash.Component.leaderBoard_RecylerViewAdapter
import `in`.oncash.oncash.DataType.userData
import `in`.oncash.oncash.Fragment.InviteFragment
import `in`.oncash.oncash.Fragment.StatusFragment
import `in`.oncash.oncash.R
import `in`.oncash.oncash.Repository.UserInfo_Airtable_Repo
import `in`.oncash.oncash.ViewModel.home_viewModel
import `in`.oncash.oncash.ViewModel.referral_viewModel
import `in`.oncash.oncash.databinding.ActivityReferalBinding
import kotlinx.coroutines.launch

class ReferalActivity : AppCompatActivity() {

    val referralViewmodel :referral_viewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding= ActivityReferalBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var userData : userData = userData(0)
        userData.userNumber = intent.getLongExtra("number" , 0)
//        Toast.makeText(this , userData.userNumber.toString() , Toast.LENGTH_LONG).show()
        referralViewmodel.userData.postValue(userData)
        class MainPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
            override fun getCount(): Int = 2

            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> InviteFragment()
                    else -> StatusFragment()
                }
            }

            override fun getPageTitle(position: Int): CharSequence {
                return when (position) {
                    0 -> "Invite"
                    else -> "Status"
                }
            }
        }

        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val adapter = MainPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)




        binding.backButtonInfo.setOnClickListener {
            finish() // Finish the activity to return to the Fragment
        }
    }
}