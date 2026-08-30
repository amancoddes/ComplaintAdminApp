package com.example.adminapp




import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserLoginRepository @Inject constructor(private val auth: FirebaseAuth){

    suspend fun singnUpRepo(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val user = result.user
                ?: return Result.failure(Exception("User creation failed"))

            user.sendEmailVerification().await()

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isEmailVerified(): Boolean {
        auth.currentUser?.reload()?.await()
        return auth.currentUser?.isEmailVerified == true
    }



    suspend fun login(email: String, password: String): Result<FirebaseUser?> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(auth.currentUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}