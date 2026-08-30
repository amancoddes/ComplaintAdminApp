package com.example.adminapp.di

import android.content.Context
import com.example.adminapp.ComplaintRepository
import com.example.adminapp.ConnectivityObserver
import com.example.adminapp.MappingRemoteData
import com.example.adminapp.RemoteAdminRequest
import com.example.adminapp.UserLoginRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object HiltDependencies {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun fireStore()= Firebase.firestore


    @Provides
    @Singleton
    fun provideLoginRepository(auth: FirebaseAuth)=UserLoginRepository(auth)

    @Provides
    @Singleton
    fun provideComplaintRepository(firestore: FirebaseFirestore): ComplaintRepository {
        return ComplaintRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideMappingRemoteData(remoteRepository: ComplaintRepository)=MappingRemoteData(remoteRepository = remoteRepository)


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://makeadmin-v3rf35pewa-uc.a.run.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAdminApi(retrofit: Retrofit): RemoteAdminRequest {
        return retrofit.create(RemoteAdminRequest::class.java)
    }

}
