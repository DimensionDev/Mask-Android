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

import com.dimension.maskbook.common.routeProcessor.annotations.Back
import com.dimension.maskbook.common.routeProcessor.annotations.NavGraphDestination
import com.dimension.maskbook.common.routeProcessor.annotations.Navigate
import com.dimension.maskbook.common.routeProcessor.annotations.Path
import com.dimension.maskbook.common.routeProcessor.annotations.Query
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import com.squareup.kotlinpoet.withIndent

private val navControllerType = ClassName("androidx.navigation", "NavController")
private const val navControllerName = "controller"

@OptIn(KotlinPoetKspPreview::class, KspExperimental::class)
internal class RouteGraphProcessor(
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .getSymbolsWithAnnotation(
                NavGraphDestination::class.qualifiedName
                    ?: throw CloneNotSupportedException("Can not get qualifiedName for RouteGraphDestination")
            ).filterIsInstance<KSFunctionDeclaration>().toList()
        val ret = symbols.filter {
            try {
                it.getAnnotationsByType(NavGraphDestination::class).first().route
                false
            } catch (e: Throwable) {
                true
            }
        }

        val actualSymbols = symbols - ret.toSet()
        actualSymbols.groupBy {
            it.getAnnotationsByType(NavGraphDestination::class).first().generatedFunctionName
        }.forEach { (name, items) ->
            generateRoute(items, name)
        }
        return ret
    }

    private fun generateRoute(data: List<KSFunctionDeclaration>, generatedFunctionName: String) {
        if (data.isEmpty()) {
            return
        }
        val dependencies = Dependencies(
            true,
            *(data.mapNotNull { it.containingFile }).toTypedArray()
        )
        val packageName = data.first().packageName
        FileSpec.builder(packageName.asString(), "RouteGraph")
            .addImport("androidx.navigation", "NavType")
            .addImport("androidx.navigation", "navDeepLink")
            .addImport("androidx.navigation", "navArgument")
            .also { fileBuilder ->
                fileBuilder.addFunction(
                    FunSpec.builder(generatedFunctionName)
                        .receiver(ClassName("androidx.navigation", "NavGraphBuilder"))
                        .addParameter(
                            navControllerName,
                            navControllerType,
                        )
                        .also { builder ->
                            data.forEach { ksFunctionDeclaration ->
                                if (packageName != ksFunctionDeclaration.packageName) {
                                    fileBuilder.addImport(ksFunctionDeclaration.packageName.asString(), ksFunctionDeclaration.simpleName.asString())
                                }
                                val annotation =
                                    ksFunctionDeclaration.getAnnotationsByType(
                                        NavGraphDestination::class
                                    )
                                        .first()
                                fileBuilder.addImport(
                                    annotation.packageName,
                                    annotation.functionName
                                )
                                builder.addStatement(
                                    "%N(",
                                    annotation.functionName,
                                )
                                builder.addCode(
                                    buildCodeBlock {
                                        withIndent {
                                            addStatement(
                                                "route = %S,",
                                                annotation.route,
                                            )
                                            addStatement("arguments = listOf(")
                                            withIndent {
                                                ksFunctionDeclaration.parameters.filter {
                                                    it.isAnnotationPresent(
                                                        Query::class
                                                    ) || it.isAnnotationPresent(Path::class)
                                                }.forEach {
                                                    addStatement(
                                                        "navArgument(%S) { type = NavType.%NType; nullable = %L },",
                                                        it.name?.asString() ?: "",
                                                        it.type.resolve().declaration.simpleName.asString(),
                                                        it.isAnnotationPresent(Query::class)
                                                    )
                                                }
                                            }
                                            addStatement("),")
                                            addStatement("deepLinks = listOf(")
                                            withIndent {
                                                annotation.deeplink.forEach {
                                                    addStatement(
                                                        "navDeepLink { uriPattern = %S }",
                                                        it
                                                    )
                                                }
                                            }
                                            addStatement("),")
                                        }
                                    }
                                )
                                builder.beginControlFlow(")")
                                ksFunctionDeclaration.parameters.forEach {
                                    if (it.isAnnotationPresent(Path::class)) {
                                        require(!it.type.resolve().isMarkedNullable)
                                    }
                                    if (it.isAnnotationPresent(Query::class)) {
                                        require(it.type.resolve().isMarkedNullable)
                                    }
                                    if (it.isAnnotationPresent(Path::class)) {
                                        val path = it.getAnnotationsByType(Path::class).first()
                                        builder.addStatement(
                                            "val ${it.name?.asString()} = it.arguments!!.get(%S) as %T",
                                            path.name,
                                            it.type.toTypeName()
                                        )
                                    } else if (it.isAnnotationPresent(Query::class)) {
                                        val query =
                                            it.getAnnotationsByType(Query::class).first()
                                        builder.addStatement(
                                            "val ${it.name?.asString()} = it.arguments?.get(%S) as? %T",
                                            query.name,
                                            it.type.toTypeName()
                                        )
                                    }
                                }
                                builder.addCode(
                                    buildCodeBlock {
                                        addStatement(
                                            "%N(",
                                            ksFunctionDeclaration.simpleName.asString()
                                        )
                                        withIndent {
                                            ksFunctionDeclaration.parameters.forEach {
                                                when {
                                                    it.type.toTypeName() == navControllerType -> {
                                                        addStatement(
                                                            "%N = %N,",
                                                            it.name?.asString() ?: "",
                                                            navControllerName
                                                        )
                                                    }
                                                    it.isAnnotationPresent(Query::class) || it.isAnnotationPresent(Path::class) -> {
                                                        addStatement(
                                                            "%N = %N,",
                                                            it.name?.asString() ?: "",
                                                            it.name?.asString() ?: ""
                                                        )
                                                    }
                                                    it.isAnnotationPresent(Back::class) -> {
                                                        addStatement(
                                                            "%N = { %N.navigateUp() },",
                                                            it.name?.asString() ?: "",
                                                            navControllerName
                                                        )
                                                    }
                                                    it.isAnnotationPresent(Navigate::class) -> {
                                                        val target = it.getAnnotationsByType(Navigate::class).first().target
                                                        val type = it.type.resolve()
                                                        require(type.isFunctionType)
                                                        val declaration = type.declaration as KSClassDeclaration
                                                        val parameters = declaration.getDeclaredFunctions().first().parameters
                                                        val parameter = if (parameters.any()) {
                                                            "\\{(\\w+)}".toRegex().findAll(target).map { it.groups[1]?.value }.joinToString(",") + " ->"
                                                        } else {
                                                            ""
                                                        }
                                                        addStatement(
                                                            "%N = { $parameter %N.navigate(%P) },",
                                                            it.name?.asString() ?: "",
                                                            navControllerName,
                                                            target.replace("{", "\${")
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        addStatement(")")
                                    }
                                )
                                builder.endControlFlow()
                            }
                        }
                        .build()
                )
            }
            .build()
            .writeTo(codeGenerator, dependencies)
    }
}
