package com.aapnainfotech.expensemanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.aapnainfotech.expensemanagementsystem.fragments.setting.SettingFragment
import com.aapnainfotech.expensemanagementsystem.login.LoginActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*

class MainActivity : AppCompatActivity() {

    lateinit var drawer_layout: DrawerLayout
    lateinit var navigation_view: NavigationView
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var toolbar: Toolbar

    companion object {
        var currentUser: String? = ""
    }

    lateinit var auth: FirebaseAuth

    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ExpenseManagementSystem)
        setContentView(R.layout.activity_main)
//        supportActionBar?.hide()
        setupActionBarWithNavController(findNavController(R.id.hostfragment))


        drawer_layout = findViewById(R.id.drawer_layout)
        navigation_view = findViewById(R.id.navigation_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Profile")

        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


        loadProfile()
        navigationViewListener()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
//        val navController = findNavController(R.id.hostfragment)
//        return navController.navigateUp() || super.onSupportNavigateUp()
        return true
    }

    private fun navigationViewListener() {

        navigation_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    findNavController(R.id.hostfragment).navigate(R.id.homeFragment)
                    drawer_layout.closeDrawers()
                    Toast.makeText(
                        applicationContext,
                        "home is Clicked",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                }
                R.id.update -> {
                    Toast.makeText(
                        applicationContext,
                        "update is under development", Toast.LENGTH_SHORT
                    )
                        .show()
                    drawer_layout.closeDrawers()
                }

                R.id.history -> {
                    Toast.makeText(
                        applicationContext,
                        "History is under development",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    drawer_layout.closeDrawers()
                }
                R.id.account -> {
                    findNavController(R.id.hostfragment).navigate(R.id.accountFragment)
                    Toast.makeText(
                        applicationContext,
                        "Enter your Account Details",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    drawer_layout.closeDrawers()
                }

                R.id.budget -> {
                    Toast.makeText(
                        applicationContext,
                        "Budget is under development", Toast.LENGTH_SHORT
                    )
                        .show()
                    drawer_layout.closeDrawers()
                }
                R.id.reports -> {
                    Toast.makeText(
                        applicationContext,
                        "Reports is under development", Toast.LENGTH_SHORT
                    )
                        .show()
                    drawer_layout.closeDrawers()
                }
                R.id.logout -> {
                    Toast.makeText(
                        applicationContext,
                        "Logout is Clicked", Toast.LENGTH_SHORT
                    )
                        .show()

                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    onBackPressed()
                }

                R.id.rateus -> {
                    Toast.makeText(
                        applicationContext,
                        "Rate us is Clicked", Toast.LENGTH_SHORT
                    )
                        .show()
                    drawer_layout.closeDrawers()
                }
                R.id.invite -> {
                    Toast.makeText(
                        applicationContext,
                        "Invite is Clicked", Toast.LENGTH_SHORT
                    )
                        .show()
                    drawer_layout.closeDrawers()
                }
                R.id.profilepicture -> {
                    Toast.makeText(
                        applicationContext,
                        "Profile picture is clicked", Toast.LENGTH_LONG
                    )
                        .show()
                    drawer_layout.closeDrawers()
                }

            }
            true
        }

    }

    private fun loadProfile() {
        val user = auth.currentUser

        currentUser = user?.email

        Toast.makeText(this@MainActivity, user?.email, Toast.LENGTH_LONG).show()

    }

}