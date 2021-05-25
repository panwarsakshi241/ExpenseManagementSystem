package com.aapnainfotech.expensemanagementsystem

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.inflate
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.aapnainfotech.expensemanagementsystem.fragments.income.IncomeFragment
import com.aapnainfotech.expensemanagementsystem.login.LoginActivity
import com.aapnainfotech.expensemanagementsystem.model.Budget
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import java.util.*

class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    lateinit var drawer_layout: DrawerLayout
    lateinit var navigation_view: NavigationView
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var toolbar: Toolbar
    lateinit var ref: DatabaseReference
    private var mToolBarNavigationListenerIsRegistered: Boolean = false

    //budget planning dialog box views

    lateinit var BudgetAmount: EditText
    lateinit var selectMonth: Spinner

    companion object {
        var currentUser: String? = ""
    }

    var timeStamp = ""
    var selectedMonth = ""

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

        loadProfile()
        val current_user = currentUser?.replace(".", "")
        ref = FirebaseDatabase.getInstance().getReference("Users/" + current_user)

        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
            .apply {
                drawer_layout.addDrawerListener(this)
                this.syncState()
            }
//        drawer_layout.addDrawerListener(toggle)
//        toggle.syncState()

        supportFragmentManager.addOnBackStackChangedListener(this)

        navigationViewListener()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.nav_menu, menu)
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
        val navController = findNavController(R.id.hostfragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
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

                    showDialogBox()

                    Toast.makeText(
                        applicationContext,
                        "Plan your monthly budget !", Toast.LENGTH_SHORT
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


    fun showDialogBox() {

        val builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.budget_planning_dialog_layout, null)

        BudgetAmount = dialogLayout.findViewById(R.id.enterBudgetAmtET)
        selectMonth = dialogLayout.findViewById(R.id.selectMonthSpinner)


        selectMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val monthArray = resources.getStringArray(R.array.selectMonth)
                selectedMonth = monthArray.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        with(builder) {
            setTitle("Plan Your Budget")

            setPositiveButton("Save") { _, _ ->
                //saveBudgetDetails
                saveBudgetDetails()
            }
            setNegativeButton("Cancel") { _, _ ->

            }

            setView(dialogLayout)
            show()
        }

    }

    private fun saveBudgetDetails() {

        if (BudgetAmount.text.isNotEmpty()
            && !selectedMonth.equals(getString(R.string.select))
        ) {

            val userId: String =
                ref.push().key.toString()

            val amt = BudgetAmount.text.toString().trim()
            //timeStamp
            val date = Date()
            timeStamp = IncomeFragment.dateTimeFormat.format(date)

            val year = timeStamp.substring(0, 4)
            val path = "Budget/$year/$selectedMonth"

            val user = Budget(
                userId,
                amt,
                selectedMonth,
                timeStamp
            )

            ref.child(path).setValue(user).addOnCompleteListener {
                Toast.makeText(this, "Budget details saved successfully", Toast.LENGTH_SHORT)
                    .show()
            }

        } else {

            if (BudgetAmount.text.isEmpty()) {
                BudgetAmount.error = "Please enter Expense !"
                return
            }
            if (selectedMonth.equals(getString(R.string.select))) {
                (selectMonth.getSelectedView() as TextView).error =
                    "Please Choose some category"
                return
            }
        }
    }


}