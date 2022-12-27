package com.example.oncash.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.util.Util
import com.example.oncash.Component.Diffutil
import com.example.oncash.Component.Offer_RecylerViewAdapter
import com.example.oncash.R
import com.example.oncash.ViewModel.home_viewModel
import com.example.oncash.databinding.ActivityHomeBinding

class Home : AppCompatActivity() {
    lateinit var  binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = Offer_RecylerViewAdapter()
        binding.offerRecylerview.adapter = adapter
        binding.offerRecylerview.layoutManager = LinearLayoutManager(this , LinearLayoutManager.VERTICAL , false)

        val viewModel = ViewModelProvider(this).get(home_viewModel::class.java)
        viewModel.getOfferList().observe(this , Observer { OfferList ->
            adapter.updateList(OfferList)
        })

    }
}