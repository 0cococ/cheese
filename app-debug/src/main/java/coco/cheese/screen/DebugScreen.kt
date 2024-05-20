package coco.cheese.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coco.cheese.core.engine.javet.node
import coco.cheese.core.ui.textList



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun DebugScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("不知今夕是何年")
                },
//                navigationIcon = {
//                    IconButton(
//                        onClick = { } //do something
//                    ) {
//                        Icon(Icons.Filled.ArrowBack, null)
//                    }
//                },
                actions = {
                    IconButton(
                        onClick = {
                            textList.clear()
                        } //do something
                    ) {
                        Icon(
                            painterResource(id = coco.cheese.core.R.drawable.delete),
                            null
                        )
                    }
                }
            )
        },
    ) {
        Column(
            modifier =  Modifier.padding(top = 60.dp)
        ) {
            Column (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(5.dp)

            ){
                TextList(textList = textList)
            }
        }

    }
}


@Composable
fun TextList(textList: List<String>) {

    val scrollState = rememberLazyListState()
    LaunchedEffect(textList) {
        if (textList.isNotEmpty()) {
            scrollState.animateScrollToItem(textList.size - 1)
        }
    }
    LazyColumn(
        state = scrollState,
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        items(textList) { text ->
            TextRow(rowItems = listOf(text))
        }
    }
}

@Composable
fun TextRow(rowItems: List<String>) {
    Row(
//        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        rowItems.forEach { text ->
            Text(
                text = text,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}


