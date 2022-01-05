package com.dimension.maskbook.repository

import com.dimension.maskbook.wallet.repository.IPostRepository
import com.dimension.maskbook.wallet.repository.PostData
import kotlinx.coroutines.flow.Flow

class PostRepository : IPostRepository {
    override val posts: Flow<List<PostData>>
        get() = TODO("Not yet implemented")
}

