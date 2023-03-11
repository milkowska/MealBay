package uk.ac.aber.dcs.cs39440.mealbay.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

import kotlinx.coroutines.tasks.await



/*
suspend fun getDataFromFireStore():Recipe{
    val db = FirebaseFirestore.getInstance()
    var recipe = Recipe()

    try {
        db.collection("recipes").get().await().map {
          val result = it.toObject(Recipe::class.java)
            recipe = result
        }
    }catch (e: FirebaseFirestoreException){
        Log.d("err", "getDataFromFirestore: $e")
    }

    return recipe
}*/
