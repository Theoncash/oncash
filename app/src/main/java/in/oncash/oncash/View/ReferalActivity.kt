package `in`.oncash.oncash.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import `in`.oncash.oncash.Fragment.InviteFragment
import `in`.oncash.oncash.Fragment.StatusFragment
import `in`.oncash.oncash.R
import `in`.oncash.oncash.databinding.ActivityReferalBinding

class ReferalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding= ActivityReferalBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
            startActivity(Intent(this,Home::class.java))
        }
    }
}