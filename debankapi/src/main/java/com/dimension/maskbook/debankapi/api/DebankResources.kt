package com.dimension.maskbook.debankapi.api

import com.dimension.maskbook.debankapi.api.ChainResources
import com.dimension.maskbook.debankapi.api.TokenResources
import com.dimension.maskbook.debankapi.api.UserResources

interface DebankResources: ChainResources, TokenResources, UserResources, TransactionResources