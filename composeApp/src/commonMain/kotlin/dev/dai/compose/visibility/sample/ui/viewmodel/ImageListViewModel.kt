package dev.dai.compose.visibility.sample.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.dai.compose.visibility.sample.domain.model.ImageItem
import dev.dai.compose.visibility.sample.domain.usecase.GetImagesUseCase
import dev.dai.compose.visibility.sample.domain.usecase.LogImageVisibilityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ImageListUiState {
    data object Loading : ImageListUiState
    data class Success(val images: List<ImageItem>) : ImageListUiState
    data class Error(val message: String) : ImageListUiState
}

class ImageListViewModel(
    private val getImagesUseCase: GetImagesUseCase,
    private val logImageVisibilityUseCase: LogImageVisibilityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ImageListUiState>(ImageListUiState.Loading)
    val uiState: StateFlow<ImageListUiState> = _uiState.asStateFlow()

    init {
        loadImages()
    }

    fun loadImages() {
        viewModelScope.launch {
            _uiState.value = ImageListUiState.Loading
            getImagesUseCase(page = 1, limit = 30)
                .onSuccess { images ->
                    if (images.isEmpty()) {
                        _uiState.value = ImageListUiState.Error(
                            message = "画像が見つかりませんでした"
                        )
                    } else {
                        _uiState.value = ImageListUiState.Success(images)
                    }
                }
                .onFailure { error ->
                    _uiState.value = ImageListUiState.Error(
                        message = getErrorMessage(error)
                    )
                }
        }
    }

    private fun getErrorMessage(error: Throwable): String {
        return when {
            error.message?.contains("UnknownHost", ignoreCase = true) == true ->
                "ネットワークに接続できません。インターネット接続を確認してください。"
            error.message?.contains("timeout", ignoreCase = true) == true ->
                "接続がタイムアウトしました。しばらくしてから再度お試しください。"
            error.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                "サーバーに接続できません。ネットワーク接続を確認してください。"
            else ->
                "画像の取得に失敗しました。${error.message ?: ""}"
        }
    }

    fun onImageVisible(imageId: String, position: Int) {
        logImageVisibilityUseCase(imageId, position)
    }
}
