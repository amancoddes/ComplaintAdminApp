package com.example.adminapp



import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ComplaintDetailViewModel @Inject constructor(private val repository: ComplaintRepository
) : ViewModel() {




    private val _snackBarEvent = MutableSharedFlow<String>()
    val snackBarEvent = _snackBarEvent.asSharedFlow()

    private val _complaint = MutableStateFlow<Complaint?>(null)
    val complaint = _complaint.asStateFlow()

    private val _resolvedImageUri = MutableStateFlow<Uri?>(null)
    val resolvedImageUri =
        _resolvedImageUri.asStateFlow()


    fun updateImageUri(uri: Uri){
         _resolvedImageUri.value=uri
    }




    private val _loading = MutableStateFlow<States>(States.Loading)
    val loading = _loading.asStateFlow()

    private var lastRefreshTime = 0L
    private val refreshCooldown = 5_000L


    fun getComplaintById(id: String) {
        viewModelScope.launch {


            val currentTime = System.currentTimeMillis()

            if (currentTime - lastRefreshTime < refreshCooldown) {
                _snackBarEvent.emit("Please wait before refreshing again after 10 seconds ⏳")
                return@launch
            }
            _loading.value = States.Loading



            lastRefreshTime = currentTime

            Log.e("fetch", "fetching run ")

            val result = repository.getComplaintById(id)

            if (result.isSuccess) {
                Log.e("resolved", "getComplaintById: ${result.getOrNull()!!.resolvedImageUrl}")
                _complaint.value = result.getOrNull()
                _loading.value = States.Success
            } else {
                _loading.value = States.Error(
                    message = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                )
                return@launch
            }

            return@launch
        }

    }

    fun updateData() {

        viewModelScope.launch {
            _loading.value = States.Loading

            val currentTime = System.currentTimeMillis()

            if (currentTime - lastRefreshTime < refreshCooldown) {
                _snackBarEvent.emit("Please wait before update again after 5 seconds ⏳")
                return@launch
            }




            lastRefreshTime = currentTime


            val uri = _resolvedImageUri.value

            Log.e("update", " 1 -> image uri: $uri")

            if (uri == null) {

                _snackBarEvent.emit(
                    "Please capture image first"
                )

                return@launch
            }

            val result = repository.uploadImage(uri = uri,)

            val imageUrl = result.getOrElse {

                _snackBarEvent.emit(
                    "Image upload failed ❌"
                )

                return@launch
            }
            Log.e("update", " 2   get image url from the backend-> $imageUrl")


         val result2=   repository.updateComplaintData(
            complaintId = complaint.value!!.id,

                resolvedImageUrl = imageUrl

            )

            result2.fold(
                onSuccess = {
                    _snackBarEvent.emit(

                        "Complaint updated successfully ✅"

                    )

                    getComplaintById(

                        complaint.value!!.id

                    )
                } ,
                onFailure = {
                    _snackBarEvent.emit(
                        result2.exceptionOrNull()?.message ?: "Unknown error occurred"
                    )
                }
            )
            _loading.value = States.Success
        }


    }

}

sealed class States{
    data object Loading : States()
    data class Error(val message:String):States()
    data object Success : States()

}