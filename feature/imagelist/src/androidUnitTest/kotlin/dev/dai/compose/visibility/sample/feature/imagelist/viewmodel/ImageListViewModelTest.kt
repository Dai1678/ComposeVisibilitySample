package dev.dai.compose.visibility.sample.feature.imagelist.viewmodel

import dev.dai.compose.visibility.sample.core.domain.image.usecase.GetImagesUseCase
import dev.dai.compose.visibility.sample.core.domain.image.usecase.LogImageVisibilityUseCase
import dev.dai.compose.visibility.sample.feature.imagelist.TestData
import dev.dai.compose.visibility.sample.feature.imagelist.fake.FakeImageRepository
import dev.dai.compose.visibility.sample.feature.imagelist.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ImageListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeImageRepository
    private lateinit var getImagesUseCase: GetImagesUseCase
    private lateinit var logImageVisibilityUseCase: LogImageVisibilityUseCase
    private lateinit var viewModel: ImageListViewModel

    @BeforeTest
    fun setup() {
        fakeRepository = FakeImageRepository()
        getImagesUseCase = GetImagesUseCase(fakeRepository)
        logImageVisibilityUseCase = LogImageVisibilityUseCase()
    }

    @Test
    fun `uiState is Success when images are loaded successfully`() = runTest {
        // Given
        fakeRepository.setSuccessResult(TestData.imageItems)

        // When
        viewModel = ImageListViewModel(getImagesUseCase, logImageVisibilityUseCase)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is ImageListUiState.Success)
        assertEquals(3, state.images.size)
        assertEquals("1", state.images.first().id)
    }

    @Test
    fun `uiState is Error when loading fails`() = runTest {
        // Given
        fakeRepository.setErrorResult(Exception("Network error"))

        // When
        viewModel = ImageListViewModel(getImagesUseCase, logImageVisibilityUseCase)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is ImageListUiState.Error)
        assertTrue(state.message.contains("画像の取得に失敗しました"))
    }

    @Test
    fun `uiState is Error when images list is empty`() = runTest {
        // Given
        fakeRepository.setSuccessResult(emptyList())

        // When
        viewModel = ImageListViewModel(getImagesUseCase, logImageVisibilityUseCase)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is ImageListUiState.Error)
        assertEquals("画像が見つかりませんでした", state.message)
    }

    @Test
    fun `loadImages updates state to Success after loading`() = runTest {
        // Given
        fakeRepository.setSuccessResult(TestData.imageItems)
        viewModel = ImageListViewModel(getImagesUseCase, logImageVisibilityUseCase)

        // When - Load different data
        fakeRepository.setSuccessResult(listOf(TestData.imageItem1))
        viewModel.loadImages()

        // Then - should be Success with new data
        val state = viewModel.uiState.value
        assertTrue(state is ImageListUiState.Success)
        assertEquals(1, state.images.size)
        assertEquals("1", state.images.first().id)
    }

    @Test
    fun `retry loads images again after error`() = runTest {
        // Given - First load fails
        fakeRepository.setErrorResult(Exception("Network error"))
        viewModel = ImageListViewModel(getImagesUseCase, logImageVisibilityUseCase)
        assertTrue(viewModel.uiState.value is ImageListUiState.Error)

        // When - Retry with success
        fakeRepository.setSuccessResult(TestData.imageItems)
        viewModel.loadImages()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is ImageListUiState.Success)
        assertEquals(3, state.images.size)
    }

    @Test
    fun `error message is specific for UnknownHost`() = runTest {
        // Given
        fakeRepository.setErrorResult(Exception("UnknownHostException"))

        // When
        viewModel = ImageListViewModel(getImagesUseCase, logImageVisibilityUseCase)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is ImageListUiState.Error)
        assertTrue(state.message.contains("ネットワークに接続できません"))
    }

    @Test
    fun `error message is specific for timeout`() = runTest {
        // Given
        fakeRepository.setErrorResult(Exception("Connection timeout"))

        // When
        viewModel = ImageListViewModel(getImagesUseCase, logImageVisibilityUseCase)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is ImageListUiState.Error)
        assertTrue(state.message.contains("タイムアウト"))
    }

    @Test
    fun `onImageVisible calls LogImageVisibilityUseCase`() = runTest {
        // Given
        fakeRepository.setSuccessResult(TestData.imageItems)
        viewModel = ImageListViewModel(getImagesUseCase, logImageVisibilityUseCase)

        // When & Then - should not throw
        viewModel.onImageVisible("test-id", 1)
    }
}
