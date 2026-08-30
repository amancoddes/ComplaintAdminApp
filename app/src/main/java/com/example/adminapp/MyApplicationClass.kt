package com.example.adminapp

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.firestore
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build()) // not use Dish cache its prevent bugging problems
            .build()
        Firebase.firestore.firestoreSettings = settings
    }
}