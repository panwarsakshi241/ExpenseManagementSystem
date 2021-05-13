package com.aapnainfotech.expensemanagementsystem

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.aapnainfotech.expensemanagementsystem.fragments.home.HomeFragment
import com.aapnainfotech.expensemanagementsystem.fragments.income.IncomeFragment
import com.aapnainfotech.expensemanagementsystem.fragments.setting.SettingFragment
import com.aapnainfotech.expensemanagementsystem.login.LoginActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*

class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    lateinit var drawer_layout: DrawerLayout
    lateinit var navigation_view: NavigationView
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var toolbar: Toolbar
    private var mToolBarNavigationListenerIsRegistered: Boolean = false

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
//        setupActionBarWithNavController(findNavController(R.id.hostfragment))
        val actionBar = supportActionBar
//        actionBar!!.title = getString(R.string.app_name)

        drawer_layout = findViewById(R.id.drawer_layout)
        navigation_view = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        actionBar?.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Profile")


        toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.open, R.string.close)
            .apply {
                drawer_layout.addDrawerListener(this)
                this.syncState()
            }
//        drawer_layout.addDrawerListener(toggle)
//        toggle.syncState()

        supportFragmentManager.addOnBackStackChangedListener(this)
//        displayHomeUporHamburger()


        loadProfile()
        navigationViewListener()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       val inflater =  menuInflater
        inflater.inflate(R.menu.nav_menu,menu)
        return true
    }

//    private fun displayHomeUporHamburger() {
//        //enable up btn only if there are entries in the back stack
//        val upBtn : Boolean = supportFragmentManager.backStackEntryCount > 0
//
//        if (upBtn){
//            //can't swipe left to open drawer
//            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
////            remove hamburger
//            actionBar?.setDisplayHomeAsUpEnabled(false)
//            toggle.setDrawerIndicatorEnabled(false)
//            //need listener for upbtn
//            if (!mToolBarNavigationListenerIsRegistered){
//                toggle.setToolbarNavigationClickListener(object : View.OnClickListener {
//                    override fun onClick(p0: View?) {
//                        supportFragmentManager.popBackStackImmediate()
//                    }
//
//                })
//                mToolBarNavigationListenerIsRegistered = true
//            }
//        }else{
//            //enable swiping
//            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
//            //show hamburger
//            actionBar?.setDisplayHomeAsUpEnabled(true)
//            toggle.setDrawerIndicatorEnabled(true)
//            //remove the drawer toggle listener
//            toggle.setToolbarNavigationClickListener(null)
//            mToolBarNavigationListenerIsRegistered = false
//        }
//    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        if (item.itemId == R.id.setting) {
            findNavController(R.id.hostfragment).navigate(R.id.settingFragment)
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

                    findNavController(R.id.hostfragment).navigate(R.id.viewFragment)

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

            }
            true
        }

    }

    private fun loadProfile() {
        val user = auth.currentUser

        currentUser = user?.email

        Toast.makeText(this@MainActivity, user?.email, Toast.LENGTH_LONG).show()

    }
//    private fun initBackStackChangeListener() {
//        supportFragmentManager.addOnBackStackChangedListener {
//            val fragment = supportFragmentManager.findFragmentById(R.id.hostfragment)
//
//            if (fragment is IncomeFragment) {
//                toggle.isDrawerIndicatorEnabled = false
//                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//                toggle.setToolbarNavigationClickListener { onBackPressed() }
//                supportActionBar?.setDisplayHomeAsUpEnabled(true)
//            } else {
//                supportActionBar?.setDisplayHomeAsUpEnabled(false)
//                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//                toggle.isDrawerIndicatorEnabled = true
//                toggle.toolbarNavigationClickListener = null
//                toggle.syncState()
//            }
//        }
//    }

    override fun onBackStackChanged() {
//        displayHomeUporHamburger()
    }

}