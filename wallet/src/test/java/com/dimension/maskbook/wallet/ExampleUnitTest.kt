package com.dimension.maskbook.wallet

import org.junit.Test

import org.junit.Assert.*
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val a = URLEncoder.encode("1+3", "UTF-8")
        val b = URLDecoder.decode(a, "UTF-8")
        assert(b == "1+3")
    }
}