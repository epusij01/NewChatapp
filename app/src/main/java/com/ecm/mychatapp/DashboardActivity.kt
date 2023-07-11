package com.ecm.mychatapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayout.TabView
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {
    var sectionAdapter:  SectionPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        supportActionBar!!.title = "DashBoard"

        sectionAdapter = SectionPagerAdapter(supportFragmentManager)
        var dashViewPager = findViewById<ViewPager>(R.id.dashViewPagerId)
        dashViewPager.adapter = sectionAdapter
        var mainTabs = findViewById<TabLayout>(R.id.mainTabs)
        mainTabs.setupWithViewPager(dashViewPager)
        mainTabs.setTabTextColors(Color.WHITE, Color.GREEN)

        if (intent.extras != null){
            var userName = intent.getStringExtra("name")
            Toast.makeText(this, userName.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item != null){
            if (item.itemId == R.id.logoutId){
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }
            if (item.itemId == R.id.settingsId){

                startActivity(Intent(this, SettingsActivity::class.java))

            }
        }
        return true
    }
}