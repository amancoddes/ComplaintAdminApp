package com.example.adminapp
import com.google.firebase.storage.FirebaseStorage

import android.net.Uri
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

// make admin -> https://makeadmin-v3rf35pewa-uc.a.run.app

data class ComplaintDto(
    val id: String = "",
    val complain: String = "",
    val description: String = "",
    val timestamp: Timestamp?=null,
    val address: String = "",
    val status: String = "PENDING",
    val userId: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val hash: String = "",
    val accuracy: Float = 0f,
    val confidence: String = "MEDIUM",
    val mode: String = "OUTDOOR",
    val numberOfPeoples: Int = 0,
    val updatedTime:Timestamp?=null,
    val imageUrl:String="",
    val resolvedImageUrl: String?=null
)


class ComplaintRepository @Inject constructor(private val db: FirebaseFirestore){


    suspend fun updateComplaintData(

        complaintId: String,

        resolvedImageUrl: String

    ): Result<Unit> {

        Log.e("update", " 2 -> updateComplaintData: $resolvedImageUrl")
        return try {

            db.collection("complaints")

                .document(complaintId)

                .update(

                    mapOf(

                        "resolvedImageUrl" to resolvedImageUrl,

                        "status" to "RESOLVED",

                        "updatedTime" to FieldValue.serverTimestamp()


                    )
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {


            Log.e("resolvedImage", " 3 -> updateComplaintData: ${e.message}")
            Result.failure(e)

        }
    }

// get  image url
    private val storageRef =// its give the ref of starting point not give any folder ref
        FirebaseStorage.getInstance().reference// add in hilt
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun uploadImage(uri: Uri): Result<String> {
        return try {

            val result = FirebaseAuth.getInstance()
                .currentUser
                ?.getIdToken(true)
                ?.await()

            Log.d(
                "CLAIMS",
                result?.claims.toString()
            )



            Log.e("checkUid", userId.toString())
            val fileName = "complaintImages/${userId}/${System.currentTimeMillis()}.jpg"
            val imageRef = storageRef.child(fileName)

            // upload
            imageRef.putFile(uri).await()

            // url
            val downloadUrl = imageRef.downloadUrl.await()

            Log.e("update"," 1.5 -> image url: $downloadUrl")
            Result.success(downloadUrl.toString())

        } catch (e: Exception) {
            Result.failure(e)

        }
    }






    suspend fun getComplaintById(
        id: String
    ): Result<Complaint?> {

        return try {

            val complaint = withTimeout(10_000L) {

                val snapshot = db
                    .collection("complaints")
                    .document(id)
                    .get()
                    .await()

                snapshot
                    .toObject(ComplaintDto::class.java)
                    ?.toDomain(id = snapshot.id)
            }

            Log.e("imageResolved", "getComplaintById -> from remote data : ${complaint?.resolvedImageUrl}")
            Result.success(complaint)

        } catch (e: TimeoutCancellationException) {
            Log.e("imageResolved"," ${e.message}")
            Result.failure(
                Exception("Request timeout. Please try again.")
            )

        } catch (e: Exception) {

            Result.failure(e)

        }
    }









    class ComplaintPagingSource(
        private val db: FirebaseFirestore,
        private val filter: ComplaintFilter
    ) : PagingSource<DocumentSnapshot, ComplaintDto>() {

        override suspend fun load(
            params: LoadParams<DocumentSnapshot>
        ): LoadResult<DocumentSnapshot, ComplaintDto> {

            return try {

                val status = when (filter) {
                    ComplaintFilter.PENDING -> "PENDING"
                    ComplaintFilter.RESOLVED -> "RESOLVED"
                    ComplaintFilter.REJECTED -> "REJECTED"
                }

                val baseQuery = db.collection("complaints")
                    .whereEqualTo("status", status)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(params.loadSize.toLong())

                val snapshot = if (params.key == null) {
                    baseQuery.get().await()
                } else {
                    baseQuery.startAfter(params.key).get().await()
                }

                val lastDoc = snapshot.documents.lastOrNull()

                val data = snapshot.documents.mapNotNull {
                    it.toObject(ComplaintDto::class.java)
                }

                LoadResult.Page(
                    data = data,
                    prevKey = null,
                    nextKey = if (snapshot.size() < params.loadSize) null else lastDoc
                )

            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(
            state: PagingState<DocumentSnapshot, ComplaintDto>
        ): DocumentSnapshot? = null
    }


    fun getComplaints(
        filter: ComplaintFilter
    ): Flow<PagingData<ComplaintDto>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = {
                ComplaintPagingSource(db, filter)
            }
        ).flow
    }
}


