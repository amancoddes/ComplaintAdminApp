package com.example.adminapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems


@Composable
fun HomeScreen(viewModel: HomeScreenViewModel,navHostController: NavHostController){
    val isConnected by viewModel.isConnected.collectAsState()
    val items = viewModel.complaintsFlow.collectAsLazyPagingItems()
    HomeScreenContent(selectedFilter = viewModel.filterFlow.collectAsState().value, onClick = { complaintFilter ->  viewModel.updateState(complaintFilter ) },items, isConnected = isConnected, onClickItem = {
      item ->
        navHostController.navigate("complaint_detail/${item.id}")
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent( selectedFilter: ComplaintFilter,onClick: (ComplaintFilter) -> Unit ,items: LazyPagingItems<Complaint>, isConnected: Boolean,onClickItem: (Complaint) -> Unit){

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = {
                Text("Complaints Data")
            })

}

    ) { innerPadding ->


        Column(Modifier.padding(innerPadding).background(
            Brush.verticalGradient(
                listOf(Color(0xFF6C63FF), Color.White)
            )
        )) {

        Box{
            FilterRow(selectedFilter, onClick = onClick)
        }

                when (selectedFilter) {
                    ComplaintFilter.PENDING -> ShowList(items,clickItem =onClickItem,isConnected)
                    ComplaintFilter.RESOLVED -> ShowList(items,clickItem = onClickItem,isConnected)
                    ComplaintFilter.REJECTED -> ShowList(items,clickItem = onClickItem,isConnected)
                }
            }

    }
}




@Composable
fun FilterRow(selectedFilter: ComplaintFilter,onClick: (ComplaintFilter) -> Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly) {
        FilterChip(title = "PENDING", isSelected = selectedFilter == ComplaintFilter.PENDING , onClick = {onClick(ComplaintFilter.PENDING)})
        FilterChip(title = "RESOLVED", isSelected = selectedFilter == ComplaintFilter.RESOLVED , onClick = {onClick(ComplaintFilter.RESOLVED)})
        FilterChip(title = "REJECTED", isSelected = selectedFilter == ComplaintFilter.REJECTED , onClick = {onClick(ComplaintFilter.REJECTED)})
    }



}


@Composable
fun FilterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color.Red
        else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

//
//
//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview(){
//    HomeScreenContent( ComplaintFilter.PENDING, onClick = {}, uiState = UiState.Loading, items = )
//}