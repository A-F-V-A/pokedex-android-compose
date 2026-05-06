package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.uqvirtual.pokedex_android_compose.R
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Generation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Habitat
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonColor
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonType
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation.ActiveFilter
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.theme.TypeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(
    activeFilter: ActiveFilter,
    searchQuery: String,
    types: List<PokemonType>,
    generations: List<Generation>,
    habitats: List<Habitat>,
    colors: List<PokemonColor>,
    onSearchQueryChange: (String) -> Unit,
    onTypeSelected: (String) -> Unit,
    onGenerationSelected: (String) -> Unit,
    onHabitatSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text(stringResource(id = R.string.search_hint)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterDropdown(
                label = stringResource(id = R.string.filter_type),
                selected = (activeFilter as? ActiveFilter.Type)?.name,
                accentColor = (activeFilter as? ActiveFilter.Type)?.let { TypeColors.forType(it.name) },
                options = types.map { it.name },
                onOptionSelected = onTypeSelected
            )
            FilterDropdown(
                label = stringResource(id = R.string.filter_generation),
                selected = (activeFilter as? ActiveFilter.GenerationFilter)?.name,
                accentColor = null,
                options = generations.map { it.name },
                onOptionSelected = onGenerationSelected
            )
            FilterDropdown(
                label = stringResource(id = R.string.filter_habitat),
                selected = (activeFilter as? ActiveFilter.HabitatFilter)?.name,
                accentColor = null,
                options = habitats.map { it.name },
                onOptionSelected = onHabitatSelected
            )
            FilterDropdown(
                label = stringResource(id = R.string.filter_color),
                selected = (activeFilter as? ActiveFilter.ColorFilter)?.name,
                accentColor = (activeFilter as? ActiveFilter.ColorFilter)?.let { colorAccent(it.name) },
                options = colors.map { it.name },
                onOptionSelected = onColorSelected
            )
        }

        ActiveFilterChip(activeFilter = activeFilter, onClear = onClearFilters)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    label: String,
    selected: String?,
    accentColor: Color?,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val display = selected?.cap() ?: label

    AssistChip(
        onClick = { expanded = true },
        label = {
            Text(
                text = display,
                fontWeight = if (selected != null) FontWeight.Bold else FontWeight.Normal
            )
        },
        trailingIcon = {
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
        },
        colors = if (selected != null && accentColor != null) {
            AssistChipDefaults.assistChipColors(
                containerColor = accentColor.copy(alpha = 0.22f),
                labelColor = MaterialTheme.colorScheme.onSurface
            )
        } else {
            AssistChipDefaults.assistChipColors()
        }
    )

    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option.cap()) },
                onClick = {
                    expanded = false
                    onOptionSelected(option)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActiveFilterChip(activeFilter: ActiveFilter, onClear: () -> Unit) {
    if (activeFilter is ActiveFilter.None) return
    val labelText: String
    val accent: Color
    when (activeFilter) {
        is ActiveFilter.Type -> {
            labelText = "${stringResource(id = R.string.filter_type)}: ${activeFilter.name.cap()}"
            accent = TypeColors.forType(activeFilter.name)
        }
        is ActiveFilter.GenerationFilter -> {
            labelText = "${stringResource(id = R.string.filter_generation)}: ${activeFilter.name.cap()}"
            accent = MaterialTheme.colorScheme.secondary
        }
        is ActiveFilter.HabitatFilter -> {
            labelText = "${stringResource(id = R.string.filter_habitat)}: ${activeFilter.name.cap()}"
            accent = MaterialTheme.colorScheme.tertiary
        }
        is ActiveFilter.ColorFilter -> {
            labelText = "${stringResource(id = R.string.filter_color)}: ${activeFilter.name.cap()}"
            accent = colorAccent(activeFilter.name)
        }
        is ActiveFilter.Search -> {
            labelText = "\"${activeFilter.query}\""
            accent = MaterialTheme.colorScheme.primary
        }
        else -> return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
    ) {
        AssistChip(
            onClick = onClear,
            label = { Text(labelText) },
            leadingIcon = { Icon(Icons.Filled.Close, contentDescription = null) },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = accent.copy(alpha = 0.18f),
                labelColor = MaterialTheme.colorScheme.onSurface,
                leadingIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

private fun String.cap(): String =
    replaceFirstChar { it.titlecase() }.replace('-', ' ')

private fun colorAccent(name: String): Color = when (name.lowercase()) {
    "black" -> Color(0xFF333333)
    "blue" -> Color(0xFF1E88E5)
    "brown" -> Color(0xFF6D4C41)
    "gray" -> Color(0xFF9E9E9E)
    "green" -> Color(0xFF43A047)
    "pink" -> Color(0xFFEC407A)
    "purple" -> Color(0xFF8E24AA)
    "red" -> Color(0xFFE53935)
    "white" -> Color(0xFFBDBDBD)
    "yellow" -> Color(0xFFFBC02D)
    else -> Color(0xFF607D8B)
}
