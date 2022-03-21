package com.dimension.maskbook.wallet.usecase

import com.dimension.maskbook.setting.export.SettingServices
import kotlinx.coroutines.flow.firstOrNull

class VerifyPaymentPasswordUseCase(
    private val service: SettingServices
) {
    suspend operator fun invoke(pwd: String) = runCatching {
        service.paymentPassword.firstOrNull()?.let {
            if (it.isNotEmpty() && it == pwd) Unit else throw Error()
        } ?: throw Error()
    }
}