package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatBar(
    label: String,
    value: Int,
    max: Int = 200,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = { (value.coerceAtMost(max).toFloat() / max).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}
