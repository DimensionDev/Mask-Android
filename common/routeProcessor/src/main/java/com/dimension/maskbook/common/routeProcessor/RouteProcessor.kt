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

import com.dimension.maskbook.common.routeProcessor.annotations.Route
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.google.devtools.ksp.visitor.KSEmptyVisitor
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.Taggable
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

internal class RouteProcessor(
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .getSymbolsWithAnnotation(
                Route::class.qualifiedName
                    ?: throw CloneNotSupportedException("Can not get qualifiedName for Route")
            )
            .filterIsInstance<KSClassDeclaration>()
        val ret = symbols.filter { !it.validate() }.toList()
        symbols
            .filter { it.validate() }
            .forEach { it.accept(RouteVisitor(), symbols.toList()) }
        return ret
    }

    inner class RouteVisitor : KSEmptyVisitor<List<KSClassDeclaration>, Unit>() {
        @OptIn(KspExperimental::class)
        override fun defaultHandler(node: KSNode, data: List<KSClassDeclaration>) {
            if (node !is KSClassDeclaration) {
                throw IllegalArgumentException("Expected KSClassDeclaration, got ${node::class.qualifiedName}")
            }

            val annotation = node.getAnnotationsByType(Route::class).first()
            val schema = annotation.schema
            val packageName = annotation.packageName.takeIf { it.isNotEmpty() }
                ?: node.packageName.asString()
            val className = node.qualifiedName?.getShortName() ?: "<ERROR>"
            val route = generateRoute(declaration = node)
                .takeIf {
                    it is NestedRouteDefinition
                }?.let {
                    PrefixRouteDefinition(
                        schema = schema,
                        child = it as NestedRouteDefinition,
                        className = className,
                    )
                } ?: throw IllegalArgumentException("Expected NestedRouteDefinition, got ${node::class.qualifiedName}")

            val dependencies = Dependencies(
                true,
                *(data.mapNotNull { it.containingFile } + listOfNotNull(node.containingFile)).toTypedArray()
            )
            generateFile(
                dependencies,
                packageName,
                className,
                route.generateRoute()
            )
        }

        @OptIn(KotlinPoetKspPreview::class)
        private fun generateFile(
            dependencies: Dependencies,
            packageName: String,
            className: String,
            route: Taggable
        ) {
            FileSpec.builder(packageName, className)
                .apply {
                    when (route) {
                        is TypeSpec -> addType(route)
                        is FunSpec -> addFunction(route)
                        is PropertySpec -> addProperty(route)
                    }
                }
                .build()
                .writeTo(codeGenerator, dependencies)
        }

        @OptIn(KotlinPoetKspPreview::class)
        private fun generateRoute(
            declaration: KSDeclaration,
            parent: RouteDefinition? = null
        ): RouteDefinition {
            val name = declaration.simpleName.getShortName()
            return when (declaration) {
                is KSClassDeclaration -> {
                    if (declaration.declarations.any { it is KSFunctionDeclaration && it.simpleName.getShortName() == "invoke" }) {
                        ParameterRouteDefinition(
                            name,
                            parent,
                        ).also { definition ->
                            definition.childRoute.addAll(
                                declaration.declarations
                                    .filter { it.simpleName.getShortName() != "<init>" }
                                    .map { generateRoute(it, definition) }
                            )
                        }
                    } else {
                        NestedRouteDefinition(
                            name = name,
                            parent = parent,
                        ).also { nestedRouteDefinition ->
                            nestedRouteDefinition.childRoute.addAll(
                                declaration.declarations
                                    .filter { it.simpleName.getShortName() != "<init>" }
                                    .map { generateRoute(it, nestedRouteDefinition) }
                            )
                        }
                    }
                }
                is KSPropertyDeclaration -> {
                    val isConst = declaration.modifiers.contains(Modifier.CONST)
                    ConstRouteDefinition(name, parent, isConst)
                }
                is KSFunctionDeclaration -> {
                    FunctionRouteDefinition(
                        name = name,
                        parent = parent,
                        parameters = declaration.parameters.map {
                            val parameterName = it.name?.getShortName() ?: "_"
                            val parameterType = it.type.toTypeName()
                            RouteParameter(
                                name = parameterName,
                                type = parameterType,
                                parameter = it
                            )
                        },
                    )
                }
                else -> throw NotImplementedError()
            }
        }
    }
}
