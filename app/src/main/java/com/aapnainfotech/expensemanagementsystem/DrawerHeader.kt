package com.aapnainfotech.expensemanagementsystem

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DrawerHeader(val mcontext: Context , val layoutResId : Int){

    lateinit var accountHolder : TextView
    lateinit var auth : FirebaseAuth

    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null


    fun loadProfile(): View?{

        val layoutInflater : LayoutInflater = LayoutInflater.from(mcontext)

        val view = layoutInflater.inflate(layoutResId , null)

        accountHolder = view.findViewById(R.id.acountHolder)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Profile")

        val user = auth.currentUser
        val userreference = databaseReference?.child(user?.uid!!)

//        acountHolder.text = user?.email

        userreference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                accountHolder.text = snapshot.child("username").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return view
    }

}