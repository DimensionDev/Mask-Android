/*
 *  Mask-Android
 *
 *  Copyright (C) 2022  DimensionDev and Contributors
 *
 *  This file is part of Mask-Android.
 *
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Mask-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook.common.routeProcessor

import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.Taggable
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

private const val RouteDivider = "/"

internal interface RouteDefinition {
    val name: String
    val parent: RouteDefinition?
    fun generateRoute(): Taggable
}

internal fun RouteDefinition.parents(): List<RouteDefinition> {
    val list = arrayListOf<RouteDefinition>()
    var p = parent
    while (p != null) {
        list.add(0, p)
        p = p.parent
    }
    return list
}

internal val RouteDefinition.parentPath
    get() = parents()
        .joinToString(RouteDivider) { it.name }

internal data class PrefixRouteDefinition(
    val schema: String,
    val child: NestedRouteDefinition,
    val className: String,
) : RouteDefinition {

    override val name: String
        get() = if (schema.isEmpty()) "" else "$schema:$RouteDivider"
    override val parent: RouteDefinition?
        get() = null

    init {
        child.name = className
        child.parent = this
    }

    override fun generateRoute(): Taggable {
        return child.generateRoute()
    }
}

internal data class NestedRouteDefinition(
    override var name: String,
    override var parent: RouteDefinition? = null,
    val childRoute: ArrayList<RouteDefinition> = arrayListOf(),
) : RouteDefinition {
    override fun generateRoute(): Taggable {
        return TypeSpec.objectBuilder(name)
            .apply {
                childRoute.forEach {
                    it.generateRoute().addTo(this)
                }
            }
            .build()
    }
}

private fun Taggable.addTo(builder: TypeSpec.Builder) {
    when (this) {
        is TypeSpec -> builder.addType(this)
        is FunSpec -> builder.addFunction(this)
        is PropertySpec -> builder.addProperty(this)
    }
}

internal data class ConstRouteDefinition(
    override val name: String,
    override val parent: RouteDefinition? = null,
) : RouteDefinition {
    override fun generateRoute(): Taggable {
        return PropertySpec.builder(name, String::class)
            .addModifiers(KModifier.CONST)
            .initializer("%S + %S + %S", parentPath, RouteDivider, name)
            .build()
    }
}

internal data class FunctionRouteDefinition(
    override val name: String,
    override val parent: RouteDefinition? = null,
    val parameters: List<RouteParameter>,
) : RouteDefinition {
    override fun generateRoute(): Taggable {
        val p = parameters.filter { !it.parameter.type.resolve().isMarkedNullable }
        val query = parameters.filter { it.parameter.type.resolve().isMarkedNullable }
        return TypeSpec.objectBuilder(name)
            .addFunction(
                FunSpec.builder("invoke")
                    .addModifiers(KModifier.OPERATOR)
                    .returns(String::class)
                    .addParameters(
                        parameters.map {
                            ParameterSpec.builder(it.name, it.type)
                                .build()
                        }
                    )
                    .addStatement("val path = %S + %S + %S", parentPath, RouteDivider, name)
                    .also {
                        if (p.any()) {
                            it.addStatement(
                                "val params = %S + %P",
                                RouteDivider,
                                p.joinToString(RouteDivider) { if (it.type == ClassName("kotlin", "String")) "\${${encode(it.name)}}" else "\${${it.name}}" },
                            )
                        } else {
                            it.addStatement("val params = \"\"")
                        }
                        if (query.any()) {
                            it.addStatement(
                                "val query = \"?\" + %P",
                                query.joinToString("&") {
                                    if (it.type == ClassName("kotlin", "String")) {
                                        "${it.name}=\${${encodeNullable(it.name)}}"
                                    } else {
                                        "${it.name}=\${${it.name}}"
                                    }
                                }
                            )
                        } else {
                            it.addStatement("val query = \"\"")
                        }
                    }
                    .addStatement("return path + params + query")
                    .build()
            )
            .addProperty(
                PropertySpec.builder("path", String::class)
                    .addModifiers(KModifier.CONST)
                    .initializer(
                        "%S + %S + %S + %S + %S",
                        parentPath,
                        RouteDivider,
                        name,
                        RouteDivider,
                        p.joinToString(RouteDivider) { "{${it.name}}" }
                    )
                    .build()
            )
            .build()
    }

    private fun encode(value: String) = "java.net.URLEncoder.encode($value, \"UTF-8\")"
    private fun encodeNullable(value: String) =
        "java.net.URLEncoder.encode(if($value == null) \"\" else $value, \"UTF-8\")"
}

internal data class RouteParameter(
    val name: String,
    val type: TypeName,
    val parameter: KSValueParameter,
)
