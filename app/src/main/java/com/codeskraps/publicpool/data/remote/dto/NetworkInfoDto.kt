package com.codeskraps.publicpool.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkInfoDto(
    val blocks: Long?,
    @SerialName("currentblockweight") val currentBlockWeight: Long?,
    @SerialName("currentblocktx") val currentBlockTx: Long?,
    val difficulty: Double?, // Using Double as it seems to be a large number
    @SerialName("networkhashps") val networkHashPS: Double?, // Using Double for large hash rate value
    @SerialName("pooledtx") val pooledTx: Long?,
    val chain: String?,
    val warnings: List<String>?
) 