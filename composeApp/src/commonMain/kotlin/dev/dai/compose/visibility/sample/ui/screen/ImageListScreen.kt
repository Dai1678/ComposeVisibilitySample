package dev.dai.compose.visibility.sample.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.dai.compose.visibility.sample.ui.component.ErrorView
import dev.dai.compose.visibility.sample.ui.component.ImageCard
import dev.dai.compose.visibility.sample.ui.component.LoadingView
import dev.dai.compose.visibility.sample.ui.viewmodel.ImageListUiState
import dev.dai.compose.visibility.sample.ui.viewmodel.ImageListViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageListScreen(
    viewModel: ImageListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visibility Sample") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ImageListUiState.Loading -> {
                    LoadingView()
                }
                is ImageListUiState.Success -> {
                    if (state.images.isEmpty()) {
                        Text(
                            text = "画像がありません",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(
                                items = state.images,
                                key = { _, item -> item.id },
                                contentType = { _, _ -> "ImageCard" }
                            ) { index, imageItem ->
                                ImageCard(
                                    imageItem = imageItem,
                                    position = index + 1, // 1始まり
                                    onVisibilityLogged = { id, pos ->
                                        viewModel.onImageVisible(id, pos)
                                    }
                                )
                            }
                        }
                    }
                }
                is ImageListUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadImages() }
                    )
                }
            }
        }
    }
}
