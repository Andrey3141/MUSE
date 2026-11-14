package com.example.muse.data

sealed class CopyVerification {
    data class Verified(val size: Long) : CopyVerification()
    data object Unverified : CopyVerification()
    data object Invalid : CopyVerification()
    data class RetryableError(val cause: Throwable) : CopyVerification()
    data class PermissionError(val cause: Throwable) : CopyVerification()
}