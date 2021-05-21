package com.huawei.hime.helpers

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

object FirebaseDBCopyHelper {

    fun copyFromToPath(fromPath: DatabaseReference,toPath:DatabaseReference){
        val eventListener = object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                toPath.setValue(p0.value)
            }
        }
        fromPath.addListenerForSingleValueEvent(eventListener)
    }
}


