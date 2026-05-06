package co.edu.uqvirtual.pokedex_android_compose.shared.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.edu.uqvirtual.pokedex_android_compose.R
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.theme.PokedexRed

private fun friendlyError(raw: String?): String = when {
    raw == null -> "Algo salio mal"
    raw.contains("Unable to resolve host", ignoreCase = true) ||
        raw.contains("UnknownHostException", ignoreCase = true) ||
        raw.contains("No address associated", ignoreCase = true) ->
        "No pudimos conectarnos a PokeAPI. Revisa tu conexion a internet o el DNS del emulador."
    raw.contains("timeout", ignoreCase = true) ->
        "La conexion tardo demasiado. Intenta de nuevo en un momento."
    raw.contains("HTTP", ignoreCase = true) || raw.contains("404") || raw.contains("500") ->
        "El servidor de PokeAPI esta teniendo problemas. Intenta mas tarde."
    else -> raw
}

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PokeballIcon(modifier = Modifier.size(80.dp))
        Text(
            text = friendlyError(message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp),
            fontWeight = FontWeight.Medium
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PokedexRed,
                contentColor = Color.White
            )
        ) {
            Text(text = stringResource(id = R.string.action_retry))
        }
    }
}
