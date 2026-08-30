package com.example.adminapp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage


@Composable
fun ShowList(items: LazyPagingItems<Complaint>, clickItem: (Complaint) -> Unit ,isConnected: Boolean) {
// check its offline or not
    var waitingForRetry by remember {
        mutableStateOf(false)
    }
    if (!isConnected && items.itemCount == 0 ) {
        waitingForRetry = true
        ErrorSection("No Internet") {
            waitingForRetry = false
            items.refresh() }

        return
    }
    if (waitingForRetry && items.itemCount == 0) {

        ErrorSection("Tap Retry to load data") {

            waitingForRetry = false
            items.refresh()

        }

        return
    }

    when(val loadState = items.loadState.refresh){

        is LoadState.Error -> {
            ErrorSection(message = loadState.error.message?:"something wrong try again") {
                items.retry()
            }
        }


         is LoadState.Loading -> {
             LoadingSection()
         }

        is LoadState.NotLoading -> {
            if (

                isConnected &&

                items.itemCount == 0 &&

                items.loadState.append.endOfPaginationReached

            ) {

                EmptySection()

            }
            else {


                LazyColumn(
                    modifier = Modifier.fillMaxSize().background(Color.Transparent)
                ) {
                    items(items.itemCount) { index ->
                        val item = items[index]

                        item?.let {
                            CardContent(clickItem = clickItem, item = item,)
                        }?: Text("no data available :( ")
                    }

                    // Load states
                    items.apply {
                        when (items.loadState.append) {// next page status

                            is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                                    }
                                }
                            }

                            is LoadState.Error -> {
                                val error = (items.loadState.append as LoadState.Error).error

                                item {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = error.localizedMessage ?: "Something went wrong",
                                            color = Color.Red
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Button(onClick = { items.retry() }) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            }

                            else -> Unit
                        }
                    }
                }

            }

        }


        }

        }





fun formatTime(time: Long): String {
    val diff = System.currentTimeMillis() - time
    val minutes = diff / (1000 * 60)

    return when {
        minutes < 60 -> "$minutes min ago"
        minutes < 1440 -> "${minutes / 60} hr ago"
        else -> "${minutes / 1440} days ago"
    }
}

@Composable
fun CardContent(clickItem: (Complaint) -> Unit,item:Complaint){
    Card(
        modifier = Modifier
            .fillMaxWidth().background(Color(0xFFFFC0CB))
            .padding(12.dp).clickable {
                clickItem(item)
            } ,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column {

            //  IMAGE + TITLE + STATUS
            Box {

                AsyncImage(
                    model = item.imageUrlApp,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    error = painterResource(R.drawable.ic_launcher_foreground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),

                    contentScale = ContentScale.Crop
                )

                //  Title overlay

                Text(
                    text = item.complain,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                )

                // Status badge
                val isResolved = item.status == "RESOLVED"

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(
                            color = if (isResolved) Color(0xFF2ECC71) else Color(0xFFE74C3C),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Text(
                        text = if (isResolved) "Resolved" else "Pending",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }

            // DETAILS SECTION
            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = "Category: ${item.complain}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))

                if(item.address.isNotBlank()){
                    Text(
                        text = item.address,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "⏱ ${formatTime(item.timestamp)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}













@Composable
fun EmptySection(

) {

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.0f)
                    .padding(10.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.3f)
                ),

                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "No complaints",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(2f)
                )
            }


            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "No complaints yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground

            )
        }
    }
}



//    val loadState = items.loadState.refresh// give current page status
//    Log.e("check empty","is error run $loadState")
//    if (!isConnected && items.itemCount == 0) {
//
//        ErrorSection(
//
//            message = "No Internet",
//
//            onRetry = { items.retry() }
//
//        )
//
//        return
//
//    }
//
//    if (loadState is LoadState.Error) {
//        val error = loadState.error
//
//        ErrorSection(
//            message = error.localizedMessage ?: "No Internet",
//            onRetry = { items.retry() }
//
//        )
//        Log.e("check empty","is error run")
//
//        return
//    }
//
//
//
//    if (loadState is LoadState.Loading) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator()
//        }
//        return
//    }
//
//    val isEmpty =
//        items.itemCount == 0 &&
//                items.loadState.refresh is LoadState.NotLoading &&
//                items.loadState.append.endOfPaginationReached
//
//    if (isEmpty) {
//        EmptySection()
//
//        Log.e("check empty","is empty run")
//        return
//    }

