package com.example.loginapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager


class MainActivity : AppCompatActivity() {

    /**set Image*/
    private lateinit var sliderItemList:ArrayList<SliderItem>
    private lateinit var sliderAdapter:SliderAdapter
    private lateinit var sliderHandle:Handler
    private lateinit var sliderRun :Runnable
    private lateinit var viewPagerImgSlider: ViewPager2 // Declare ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore theme preference
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("switch_dark_mode", false)
        val nightMode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)

        setContentView(R.layout.activity_main)

        // Bind the ViewPager2 component
        viewPagerImgSlider = findViewById(R.id.viewPagerImgSlider)

        sliderItems()
        itemSliderView()
        val btnSignIn = findViewById<Button>(R.id.btnSignInMain)
        val btnRegister = findViewById<Button>(R.id.btnRegisterMain)

        btnSignIn.setTextColor(ContextCompat.getColor(this, R.color.white))
        btnSignIn.setBackgroundColor(ContextCompat.getColor(this, R.color.black))

        btnRegister.setTextColor(ContextCompat.getColor(this, R.color.white))
        btnRegister.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))

        btnSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun sliderItems() {
        sliderItemList = ArrayList()
        sliderAdapter = SliderAdapter(viewPagerImgSlider, sliderItemList)
        viewPagerImgSlider.adapter = sliderAdapter
        viewPagerImgSlider.clipToPadding = false
        viewPagerImgSlider.clipChildren = false
        viewPagerImgSlider.offscreenPageLimit = 3
        viewPagerImgSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val comPosPageTarn = CompositePageTransformer()
        comPosPageTarn.addTransformer(MarginPageTransformer(400))
        comPosPageTarn.addTransformer { page, position ->
            val r :Float = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        viewPagerImgSlider.setPageTransformer(comPosPageTarn)
        sliderHandle = Handler()
        sliderRun = Runnable {
            viewPagerImgSlider.currentItem += 1
        }
        viewPagerImgSlider.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    sliderHandle.removeCallbacks(sliderRun)
                    sliderHandle.postDelayed(sliderRun, 10000)
                }
            }
        )
    }

    override fun onPause() {
        super.onPause()
        sliderHandle.removeCallbacks(sliderRun)
    }

    override fun onResume() {
        super.onResume()
        sliderHandle.postDelayed(sliderRun, 10000)
    }

    private fun itemSliderView() {
        sliderItemList.add(SliderItem(R.drawable.welcome_logo))
        sliderItemList.add(SliderItem(R.drawable.money))
        sliderItemList.add(SliderItem(R.drawable.ic_debt))
    }
}

