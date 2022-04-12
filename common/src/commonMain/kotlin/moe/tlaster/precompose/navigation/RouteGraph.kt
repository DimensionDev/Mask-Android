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
package moe.tlaster.precompose.navigation

internal data class RouteGraph(
    val initialRoute: String,
    val routes: List<Route>,
) {
    private val routeParser: RouteParser by lazy {
        fun matchRoute(route: Route): List<String> {
            val matches = RouteParser.expandOptionalVariables(route.route)
            if (route !is ComposeRoute) return matches

            return if (route is NavigationRoute) {
                matches + route.graph.routes.flatMapTo(mutableListOf()) { childRoute ->
                    matchRoute(childRoute)
                }
            } else if (route.deepLinks.isNotEmpty()) {
                matches + route.deepLinks.flatMap {
                    RouteParser.expandOptionalVariables(it)
                }
            } else {
                matches
            }
        }

        RouteParser().apply {
            routes.asSequence()
                .map { route -> matchRoute(route) to route }
                .flatMap { it.first.map { route -> route to it.second } }
                .forEach { insert(it.first, it.second) }
        }
    }

    fun findRoute(route: String): RouteMatchResult? {
        val routePath = route.substringBefore('?')
        return routeParser.find(path = routePath)
    }
}
