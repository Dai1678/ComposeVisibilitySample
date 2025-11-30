package dev.dai.compose.visibility.sample.feature.imagelist.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutBoundsHolder
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ImageCard(
    imageItem: ImageItem,
    position: Int,
    viewport: LayoutBoundsHolder,
    onVisibilityLogged: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .onVisibilityChanged(
                minDurationMs = 1000L, // 1秒以上可視状態が継続
                minFractionVisible = 0.5f, // 50%以上表示
                viewportBounds = viewport,
            ) { visible ->
                if (visible) {
                    onVisibilityLogged(imageItem.id, position)
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = imageItem.downloadUrl,
                contentDescription = "Photo by ${imageItem.author}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = imageItem.author,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
private fun ImageCardPreview() {
    MaterialTheme {
        ImageCard(
            imageItem = ImageItem(
                id = "1",
                author = "John Doe",
                width = 600,
                height = 400,
                downloadUrl = "https://picsum.photos/id/1/600/400"
            ),
            position = 0,
            viewport = LayoutBoundsHolder(),
            onVisibilityLogged = { _, _ -> }
        )
    }
}
