package com.dimension.maskbook.persona.model

import com.dimension.maskbook.common.ext.decodeJson
import com.dimension.maskbook.persona.model.options.ParamOptions
import com.dimension.maskbook.persona.model.options.QueryProfileOptions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ParamOptionsTest {

    @Test
    fun test_param_options() {
        val json = "{\"options\":{\"identifier\":\"person:twitter.com/aaa\"}}"
        val options = json.decodeJson<ParamOptions<QueryProfileOptions>>().options
        assertNotNull(options)
        assertEquals(options.identifier, "person:twitter.com/aaa")
    }
}