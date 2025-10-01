package com.example.myapplicationforclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplicationforclass.ui.theme.MyApplicationForClassTheme

data class TodoItem(val id: Long, val title: String, val done: Boolean = false)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationForClassTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CatTodoScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CatTodoScreen(modifier: Modifier = Modifier) {
    // Simple in-memory list (good for class demo). Swap to Room/DataStore later if you need persistence.
    val items = remember { mutableStateListOf<TodoItem>() }
    var input by remember { mutableStateOf("") }
    val focus = LocalFocusManager.current

    Box(modifier = modifier) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.cat_background),
            contentDescription = "Cat background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Soft dark overlay so content is readable
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
        )

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My To-Do List 🐾",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))

            // Input row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Add a task…") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = {
                        val trimmed = input.trim()
                        if (trimmed.isNotEmpty()) {
                            items.add(0, TodoItem(id = System.currentTimeMillis(), title = trimmed))
                            input = ""
                            focus.clearFocus()
                        }
                    }
                ) { Text("Add") }
            }

            Spacer(Modifier.height(12.dp))

            // Empty state
            if (items.isEmpty()) {
                Text(
                    text = "Nothing yet. Add your first task!",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }

            // To-do list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    TodoRow(
                        item = item,
                        onCheckedChange = { checked ->
                            val idx = items.indexOfFirst { it.id == item.id }
                            if (idx != -1) items[idx] = items[idx].copy(done = checked)
                        },
                        onDelete = {
                            items.removeAll { it.id == item.id }
                        }
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun TodoRow(
    item: TodoItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.done,
                onCheckedChange = onCheckedChange
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = item.title,
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    color = Color.Black,
                    textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None
                )
            )
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatTodoPreview() {
    MyApplicationForClassTheme {
        CatTodoScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}
