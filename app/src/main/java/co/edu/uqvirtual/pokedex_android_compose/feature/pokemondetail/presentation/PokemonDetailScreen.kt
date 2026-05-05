package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import co.edu.uqvirtual.pokedex_android_compose.R
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.AbilityDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EncounterLocation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonSpeciesInfo
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation.components.CatchAnimationOverlay
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation.components.CatchButton
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation.components.EvolutionRow
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation.components.StatBar
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.ConnectivityBanner
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.ErrorView
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.LoadingIndicator
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.TypeChip
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.theme.TypeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    onBack: () -> Unit,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val detail = uiState.detail
    val typeColor = detail?.types?.firstOrNull()?.let { TypeColors.forType(it) } ?: MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = detail?.name?.replaceFirstChar { it.titlecase() } ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = typeColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                ConnectivityBanner(isOffline = uiState.isOffline)
                when {
                    uiState.isLoading && detail == null -> LoadingIndicator()
                    detail == null && uiState.errorMessage != null -> ErrorView(
                        message = uiState.errorMessage
                            ?: stringResource(id = R.string.error_generic),
                        onRetry = viewModel::retry
                    )
                    detail != null -> PokemonDetailContent(
                        detail = detail,
                        species = uiState.species,
                        evolutionStages = uiState.evolutionChain?.stages.orEmpty(),
                        abilities = uiState.abilities,
                        encounters = uiState.encounters,
                        isLoadingEvolution = uiState.isLoadingEvolution,
                        isCaught = uiState.isCaught,
                        showCatchAnimation = uiState.showCatchAnimation,
                        onCatchToggle = viewModel::toggleCatch
                    )
                }
            }

            AnimatedVisibility(
                visible = uiState.showCatchAnimation,
                enter = fadeIn() + scaleIn(initialScale = 0.4f),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                CatchAnimationOverlay(visible = true)
            }
        }
    }
}

@Composable
private fun PokemonDetailContent(
    detail: PokemonDetail,
    species: PokemonSpeciesInfo?,
    evolutionStages: List<co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EvolutionStage>,
    abilities: List<AbilityDetail>,
    encounters: List<EncounterLocation>,
    isLoadingEvolution: Boolean,
    isCaught: Boolean,
    showCatchAnimation: Boolean,
    onCatchToggle: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { HeroHeader(detail = detail, isCaught = isCaught, onCatchToggle = onCatchToggle, showCatchAnimation = showCatchAnimation) }
        item { Spacer16() }

        species?.let {
            item { SpeciesBadgesRow(species = it) }
            item { Spacer16() }
            if (it.flavorText.isNotBlank()) {
                item {
                    SectionCard(title = stringResource(id = R.string.detail_pokedex_entry)) {
                        Text(text = it.flavorText, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                item { Spacer16() }
            }
        }

        item {
            SectionCard(title = stringResource(id = R.string.detail_stats)) {
                MetricsRow(detail)
                StatBar(label = "HP", value = detail.stats.hp)
                StatBar(label = "Attack", value = detail.stats.attack)
                StatBar(label = "Defense", value = detail.stats.defense)
                StatBar(label = "Sp. Attack", value = detail.stats.specialAttack)
                StatBar(label = "Sp. Defense", value = detail.stats.specialDefense)
                StatBar(label = "Speed", value = detail.stats.speed)
            }
        }
        item { Spacer16() }

        item {
            SectionCard(title = stringResource(id = R.string.detail_evolution)) {
                if (isLoadingEvolution && evolutionStages.isEmpty()) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Text(
                                text = "  " + stringResource(id = R.string.loading_evolution),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                } else {
                    EvolutionRow(stages = evolutionStages, onStageClick = {})
                }
            }
        }
        item { Spacer16() }

        item {
            SectionCard(title = stringResource(id = R.string.detail_abilities)) {
                if (abilities.isEmpty()) {
                    detail.abilities.forEach { ability ->
                        Text(
                            text = "- ${ability.replaceFirstChar { it.titlecase() }}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    abilities.forEach { ability ->
                        AbilityCard(ability = ability)
                    }
                }
            }
        }
        item { Spacer16() }

        item {
            SectionCard(title = stringResource(id = R.string.detail_encounters)) {
                if (encounters.isEmpty()) {
                    Text(
                        text = "- ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    encounters.take(8).forEach { enc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = enc.name.replaceFirstChar { it.titlecase() },
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "${enc.maxChance}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroHeader(
    detail: PokemonDetail,
    isCaught: Boolean,
    showCatchAnimation: Boolean,
    onCatchToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TypeColors.gradientFor(detail.types))
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "#%03d".format(detail.id),
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = detail.name.replaceFirstChar { it.titlecase() },
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                detail.types.forEach { type -> TypeChip(type = type) }
            }
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(detail.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(220.dp),
                contentScale = ContentScale.Fit
            )
            CatchButton(
                isCaught = isCaught,
                isAnimating = showCatchAnimation,
                onClick = onCatchToggle,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun SpeciesBadgesRow(species: PokemonSpeciesInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (species.isLegendary) BadgeChip(text = stringResource(id = R.string.legendary_badge))
        if (species.isMythical) BadgeChip(text = stringResource(id = R.string.mythical_badge))
        species.habitat?.let { BadgeChip(text = it.replaceFirstChar { c -> c.titlecase() }) }
        BadgeChip(text = stringResource(id = R.string.detail_capture_rate) + ": " + species.captureRate)
    }
}

@Composable
private fun BadgeChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
private fun MetricsRow(detail: PokemonDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Metric(label = stringResource(id = R.string.detail_height), value = "${detail.height / 10.0} m")
        Metric(label = stringResource(id = R.string.detail_weight), value = "${detail.weight / 10.0} kg")
        Metric(label = stringResource(id = R.string.detail_base_xp), value = detail.baseExperience.toString())
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AbilityCard(ability: AbilityDetail) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)) {
        Text(
            text = ability.name.replaceFirstChar { it.titlecase() }.replace('-', ' '),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        if (ability.shortEffect.isNotBlank()) {
            Text(
                text = ability.shortEffect,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun Spacer16() {
    Box(modifier = Modifier.height(16.dp))
}
