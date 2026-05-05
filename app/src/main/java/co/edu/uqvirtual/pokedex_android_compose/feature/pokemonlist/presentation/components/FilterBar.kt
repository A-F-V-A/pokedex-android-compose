package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.edu.uqvirtual.pokedex_android_compose.R
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Generation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonType
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation.ActiveFilter

@Composable
fun FilterBar(
    activeFilter: ActiveFilter,
    types: List<PokemonType>,
    generations: List<Generation>,
    onTypeSelected: (String) -> Unit,
    onGenerationSelected: (String) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterDropdown(
            label = stringResource(id = R.string.filter_type),
            selected = (activeFilter as? ActiveFilter.Type)?.name,
            options = types.map { it.name },
            onOptionSelected = onTypeSelected
        )
        FilterDropdown(
            label = stringResource(id = R.string.filter_generation),
            selected = (activeFilter as? ActiveFilter.GenerationFilter)?.name,
            options = generations.map { it.name },
            onOptionSelected = onGenerationSelected
        )
        if (activeFilter !is ActiveFilter.None) {
            AssistChip(
                onClick = onClearFilters,
                label = { Text(stringResource(id = R.string.filter_clear)) },
                leadingIcon = { Icon(Icons.Filled.Close, contentDescription = null) }
            )
        }
    }
}

@Composable
private fun FilterDropdown(
    label: String,
    selected: String?,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val display = selected?.replaceFirstChar { it.titlecase() } ?: label
    AssistChip(
        onClick = { expanded = true },
        label = { Text(display) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null
            )
        },
        colors = AssistChipDefaults.assistChipColors()
    )
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option.replaceFirstChar { it.titlecase() }) },
                onClick = {
                    expanded = false
                    onOptionSelected(option)
                }
            )
        }
    }
}
