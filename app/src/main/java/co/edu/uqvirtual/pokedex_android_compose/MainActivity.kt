package co.edu.uqvirtual.pokedex_android_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import co.edu.uqvirtual.pokedex_android_compose.navigation.PokedexAppRoot
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.theme.PokedexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PokedexAppRoot()
                }
            }
        }
    }
}
