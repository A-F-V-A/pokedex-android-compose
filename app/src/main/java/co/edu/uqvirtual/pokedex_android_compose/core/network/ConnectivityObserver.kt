package co.edu.uqvirtual.pokedex_android_compose.core.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    val status: Flow<Status>

    fun isCurrentlyAvailable(): Boolean

    enum class Status { Available, Unavailable, Losing, Lost }
}
