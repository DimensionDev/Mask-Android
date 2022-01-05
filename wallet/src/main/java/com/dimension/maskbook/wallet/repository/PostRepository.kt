package com.dimension.maskbook.wallet.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

data class PostData(
    val id: String,
    val title: String,
    val personaId: String,
)

interface IPostRepository {
    val posts: Flow<List<PostData>>
}

class FakePostRepository: IPostRepository {
    private val _posts = MutableStateFlow(emptyList<PostData>())
    override val posts: Flow<List<PostData>> = _posts.asSharedFlow()

}