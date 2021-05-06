package com.aapnainfotech.expensemanagementsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DrawerHeader : Fragment() {

    lateinit var accountHolder : TextView
    lateinit var auth : FirebaseAuth

    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val  view = inflater.inflate(R.layout.nav_header, container, false)
        accountHolder = view.findViewById(R.id.acountHolder)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Profile")

        loadProfile()

        return view
    }

    private fun loadProfile(){
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

    }

}