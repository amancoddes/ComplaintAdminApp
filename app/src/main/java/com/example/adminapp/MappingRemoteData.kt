package com.example.adminapp

import android.os.Parcelable
import androidx.paging.PagingData
import androidx.paging.map
import androidx.versionedparcelable.ParcelField
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.Parcelize
import javax.inject.Inject


enum class Mode{
    INDOOR,OUTDOOR
}
enum class Confidence {
    HIGH, MEDIUM, REJECT
}



@Parcelize
data class Complaint(
    val id: String = "",
    val complain: String = "",
    val description: String = "",
    val timestamp: Long = 0L,
    val address: String = "",
    val status: String = "PENDING",
    val userId: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val hash: String = "",
    val accuracy: Float = 0f,
    val confidence:Confidence = Confidence.MEDIUM,
    val mode:Mode = Mode.INDOOR,
    val numberOfPeoples: Int = 0,
    val updatedTime: Long = 0L,
    val imageUrlApp:String="",
    val resolvedImageUrl: String?=null
):Parcelable


class MappingRemoteData @Inject constructor(val remoteRepository: ComplaintRepository) {

    fun fetchAllData(filter: ComplaintFilter): Flow<PagingData<Complaint>> {
        val x= remoteRepository.getComplaints(filter)
            .map { pagingData ->

                pagingData.map { dto ->

                    dto.toDomain(dto.id)

                }

            }

        return x

    }



}



fun ComplaintDto.toDomain(id: String): Complaint {
    return Complaint(
        id = id,
        complain = complain,
        description = description,
        timestamp = timestamp?.toDate()?.time ?: 0L,
        address = address,
        status = status,
        userId = userId,
        latitude = latitude,
        longitude = longitude,
        hash = hash,
        accuracy = accuracy,
        confidence = confidence.toConfidence(), // enum conversion
        mode = mode.toMode(),
        numberOfPeoples = numberOfPeoples,
        updatedTime = updatedTime?.toDate()?.time ?: 0L,
        imageUrlApp = imageUrl,
        resolvedImageUrl = resolvedImageUrl
    )
}


fun String.toConfidence(): Confidence {
    return Confidence.entries
        .firstOrNull { it.name == this.uppercase() }
        ?: Confidence.MEDIUM
}




fun String.toMode():Mode {
    return Mode.entries
        .firstOrNull { it.name == this.uppercase() }
        ?: Mode.INDOOR
}