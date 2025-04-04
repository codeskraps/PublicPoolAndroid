package com.codeskraps.publicpool.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- DTOs for blockchain.info API ---

@Serializable
data class WalletInfoDto(
    val hash160: String? = null,
    val address: String,
    @SerialName("n_tx") val nTx: Long? = null,
    @SerialName("n_unredeemed") val nUnredeemed: Long? = null,
    @SerialName("total_received") val totalReceived: Long? = null,
    @SerialName("total_sent") val totalSent: Long? = null,
    @SerialName("final_balance") val finalBalance: Long? = null,
    val txs: List<TransactionDto>? = null
)

@Serializable
data class TransactionDto(
    val hash: String,
    val ver: Int? = null,
    @SerialName("vin_sz") val vinSz: Int? = null,
    @SerialName("vout_sz") val voutSz: Int? = null,
    val size: Long? = null,
    val weight: Long? = null,
    val fee: Long? = null,
    @SerialName("relayed_by") val relayedBy: String? = null,
    @SerialName("lock_time") val lockTime: Long? = null,
    @SerialName("tx_index") val txIndex: Long? = null,
    @SerialName("double_spend") val doubleSpend: Boolean? = null,
    val time: Long? = null, // Unix timestamp
    @SerialName("block_index") val blockIndex: Long? = null,
    @SerialName("block_height") val blockHeight: Long? = null,
    val inputs: List<InputDto>? = null,
    val out: List<OutputDto>? = null,
    val result: Long? = null, // Net result of tx for this address (in satoshis)
    val balance: Long? = null // Balance after this tx (in satoshis)
)

@Serializable
data class InputDto(
    val sequence: Long? = null,
    val witness: String? = null,
    val script: String? = null,
    val index: Int? = null,
    @SerialName("prev_out") val prevOut: PrevOutDto? = null
)

@Serializable
data class OutputDto(
    val type: Int? = null,
    val spent: Boolean? = null,
    val value: Long? = null, // Value in satoshis
    @SerialName("spending_outpoints") val spendingOutpoints: List<SpendingOutpointDto>? = null,
    val n: Int? = null,
    @SerialName("tx_index") val txIndex: Long? = null,
    val script: String? = null,
    val addr: String? = null
)

@Serializable
data class PrevOutDto(
    val type: Int? = null,
    val spent: Boolean? = null,
    val value: Long? = null, // Value in satoshis
    @SerialName("spending_outpoints") val spendingOutpoints: List<SpendingOutpointDto>? = null,
    val n: Int? = null,
    @SerialName("tx_index") val txIndex: Long? = null,
    val script: String? = null,
    val addr: String? = null
)

@Serializable
data class SpendingOutpointDto(
    @SerialName("tx_index") val txIndex: Long? = null,
    val n: Int? = null
) 