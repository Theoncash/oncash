package com.example.oncash.View

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oncash.Component.Offer_RecylerViewAdapter
import com.example.oncash.Component.UserDataStoreUseCase
import com.example.oncash.Component.get_UserInfo_UseCase
import com.example.oncash.R
import com.example.oncash.Repository.UserInfo_Airtable_Repo
import com.example.oncash.ViewModel.home_viewModel
import com.example.oncash.databinding.ActivityHomeBinding
import com.google.android.gms.common.api.internal.ActivityLifecycleObserver
import com.google.android.gms.common.api.internal.LifecycleActivity
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class Home : AppCompatActivity() {
    lateinit var  binding : ActivityHomeBinding
    val homeViewmodel : home_viewModel by viewModels()
    private val dataStore by preferencesDataStore(
        name = "UserData"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this);

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
      //  lifecycleScope.launch{
      //      Toast.makeText(this@Home , UserDataStoreUseCase().retrieveUser(dataStore).toString() , Toast.LENGTH_LONG).show()
  //      }
        val adapter = Offer_RecylerViewAdapter()
  //      val walletImage:ImageView = findViewById(R.id.walletimageview)
   //   Glide.with(this).load("https://cdn-icons-png.flaticon.com/512/214/214362.png").into(binding.imageView)

        val offerRecylerview : RecyclerView = findViewById(R.id.offer_recylerview)
        offerRecylerview.adapter = adapter
        offerRecylerview.layoutManager = LinearLayoutManager(this , LinearLayoutManager.VERTICAL , false)
        homeViewmodel.getOfferList().observe(this , Observer { OfferList ->
            if (OfferList != null){
                adapter.updateList(OfferList)
            }
     })
        binding.walletTextView.setOnClickListener {
            startActivity(Intent(this , Wallet::class.java))
        }

    }

    override fun onResume(){
        super.onResume()

         homeViewmodel.getWallet().observe(this  , Observer {  wallet ->
             binding.walletTextView.text = wallet.toString()
             Log.i("wallet data" , wallet.toString())
         })

    }


}
