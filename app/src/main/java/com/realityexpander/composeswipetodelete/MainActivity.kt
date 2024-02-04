@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.realityexpander.composeswipetodelete

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.realityexpander.composeswipetodelete.ui.theme.ComposeSwipeToDeleteTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

val listOfCities = listOf(
    "Austin, TX",
    "Brunswick, ME",
    "Tepoztlán, Mexico",
    "Poolesville, MD",
    "San Francisco, CA",
    "New York, NY",
    "Washington, DC",
    "Another City 1",
    "Another City 2",
    "Another City 3",
    "Another City 4",
    "Another City 5",
    "Another City 6",
    "Another City 7",
    "Another City 8",
    "Another City 9",
    "Another City 10",
    "Another City 12",
    "Another City 13",
    "Another City 14",
    "Another City 15",
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeSwipeToDeleteTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val programmingLanguages = remember {
                        mutableStateListOf<String>()
                    }.also {
                        it.addAll(listOfCities)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(
                            items = programmingLanguages,
                            key = { it }
                        ) { city ->
                            SwipeToDeleteContainer(
                                item = city,
                                onDelete = {
                                    programmingLanguages -= city
                                }
                            ) { _ ->
                                Text(
                                    text = city,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.background)
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    animationDuration: Duration = 500.milliseconds,
    content: @Composable (T) -> Unit
) {
    var shouldDelete by remember {
        mutableStateOf(false)
    }
    var shouldCancel by remember {
        mutableStateOf(false)
    }
    val state = rememberDismissState(
//        // Use this if you don't want confirmation step
//        confirmValueChange = { value ->
//            if (value == DismissValue.DismissedToStart) {
//                isRemoved = true
//                true
//            } else {
//                false
//            }
//        },
        positionalThreshold = {
            600f // Derived thru trial and error
        }

    )

    // Triggers the command to delete the item
    LaunchedEffect(key1 = shouldDelete) {
        if(shouldDelete) {
            delay(animationDuration)
            onDelete(item)
            shouldDelete = false
        }
    }

    // Triggers the command to cancel the delete
    LaunchedEffect(key1 = shouldCancel) {
        if(shouldCancel) {
            state.reset()
            shouldCancel = false
        }
    }

    AnimatedVisibility(
        visible = !shouldDelete,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration.toInt(DurationUnit.MILLISECONDS)),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismiss(
            state = state,
            background = {
                DeleteBackground(
                    swipeDismissState = state,
                    onDelete = {
                        shouldDelete = true
                    },
                    onCancel = {
                        shouldCancel = true
                    }
                )
            },
            dismissContent = { content(item) },
            directions = setOf(DismissDirection.EndToStart)
        )
    }
}

@Composable
fun DeleteBackground(
    swipeDismissState: DismissState,
    onDelete: () -> Unit = { },
    onCancel: () -> Unit = { }
) {
    val color = if (swipeDismissState.dismissDirection == DismissDirection.EndToStart) {
        Color.Red
    } else
        Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row {
            Text(
                "Delete this city?",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onError
            )
            Spacer(modifier = Modifier.width(20.dp))

            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onError,
                modifier = Modifier.clickable {
                    onCancel()
                }
            )
            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onError,
                modifier = Modifier.clickable {
                    onDelete()
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=1340px,dpi=440",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
fun DefaultPreview() {
    ComposeSwipeToDeleteTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val programmingLanguages = remember {
                mutableStateListOf<String>()
            }.also {
                it.addAll(listOfCities)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(
                    items = programmingLanguages,
                    key = { it }
                ) { language ->
                    SwipeToDeleteContainer(
                        item = language,
                        onDelete = {
                            programmingLanguages -= language
                        }
                    ) { _ ->
                        Text(
                            text = language,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    device = "spec:width=1080px,height=200px,dpi=440",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
fun DeleteBackgroundPreview() {
    ComposeSwipeToDeleteTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Red // Does not preview properly using Material3 as of 2/4/24
        ) {
            DeleteBackground(swipeDismissState = rememberDismissState())
        }
    }
}
