package com.aapnainfotech.expensemanagementsystem

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.aapnainfotech.expensemanagementsystem.fragments.income.IncomeFragment
import com.aapnainfotech.expensemanagementsystem.login.LoginActivity
import com.aapnainfotech.expensemanagementsystem.model.Budget
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import java.util.*

class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar
    private lateinit var ref: DatabaseReference

    //budget planning dialog box views

    private lateinit var budgetAmount: EditText
    private lateinit var selectMonth: Spinner

    companion object {
        var currentUser: String? = ""
    }

    private var timeStamp = ""
    var selectedMonth = ""

    private lateinit var auth: FirebaseAuth

    private var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ExpenseManagementSystem)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        actionBar?.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Profile")

        loadProfile()
        val user = currentUser?.replace(".", "")
        ref = FirebaseDatabase.getInstance().getReference("Users/$user")

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
            .apply {
                drawerLayout.addDrawerListener(this)
                this.syncState()
            }

        supportFragmentManager.addOnBackStackChangedListener(this)

        navigationViewListener()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        if (item.itemId == R.id.setting) {
            findNavController(R.id.host_fragment).navigate(R.id.settingFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        val navController = findNavController(R.id.host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun navigationViewListener() {

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    findNavController(R.id.host_fragment).navigate(R.id.homeFragment)
                    drawerLayout.closeDrawers()
                    Toast.makeText(
                        applicationContext,
                        "home is Clicked",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                }

                R.id.account -> {

                    findNavController(R.id.host_fragment).navigate(R.id.accountFragment)

                    Toast.makeText(
                        applicationContext,
                        "Enter your Account Details",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    drawerLayout.closeDrawers()
                }

                R.id.budget -> {

                    showDialogBox()

                    Toast.makeText(
                        applicationContext,
                        "Plan your monthly budget !", Toast.LENGTH_SHORT
                    )
                        .show()
                    drawerLayout.closeDrawers()
                }

                R.id.reports -> {

                    findNavController(R.id.host_fragment).navigate(R.id.viewFragment)

                    Toast.makeText(
                        applicationContext,
                        "Reports is under development", Toast.LENGTH_SHORT
                    )
                        .show()
                    drawerLayout.closeDrawers()
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
                    drawerLayout.closeDrawers()
                }
                R.id.invite -> {
                    Toast.makeText(
                        applicationContext,
                        "Invite is Clicked", Toast.LENGTH_SHORT
                    )
                        .show()
                    drawerLayout.closeDrawers()
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


    override fun onBackStackChanged() {
//        display HomeUp or Hamburger
    }


    private fun showDialogBox() {

        val builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.budget_planning_dialog_layout, null)

        budgetAmount = dialogLayout.findViewById(R.id.et_budget_amount)
        selectMonth = dialogLayout.findViewById(R.id.spinner_select_month)


        selectMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val monthArray = resources.getStringArray(R.array.selectMonth)
                selectedMonth = monthArray[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        with(builder) {
            setTitle("Plan Your Budget")

            setPositiveButton("Save") { _, _ ->
                //Save budget details
                saveBudgetDetails()
            }
            setNegativeButton("Cancel") { _, _ ->

            }

            setView(dialogLayout)
            show()
        }

    }

    private fun saveBudgetDetails() {

        if (budgetAmount.text.isNotEmpty()
            && selectedMonth != getString(R.string.select)
        ) {

            val userId: String =
                ref.push().key.toString()

            val amt = budgetAmount.text.toString().trim()
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

            if (budgetAmount.text.isEmpty()) {
                budgetAmount.error = "Please enter Expense !"
                return
            }
            if (selectedMonth == getString(R.string.select)) {
                (selectMonth.selectedView as TextView).error =
                    "Please Choose some category"
                return
            }
        }
    }


}