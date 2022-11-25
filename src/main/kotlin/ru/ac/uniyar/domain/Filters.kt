package ru.ac.uniyar.domain

import org.http4k.core.*
import org.http4k.core.cookie.cookie
import org.http4k.lens.RequestContextLens
import ru.ac.uniyar.queries.FetchPermissionsViaIdQuery
import ru.ac.uniyar.queries.FetchUserViaUserId

fun authenticationFilter(
    currentUser: RequestContextLens<User?>,
    fetchUserViaUserId: FetchUserViaUserId,
    jwtTools: JwtTools,
): Filter = Filter { next: HttpHandler ->
    {request: Request ->
        val requestWithUser = request.cookie("token")?.value?.let { token ->
            jwtTools.subject(token)
        }?.let { userId ->
            fetchUserViaUserId(userId)
        }?.let { user ->
            request.with( currentUser of user)
        }?: request
        next(requestWithUser)
    }
}

fun authorizationFilter(
    currentUser: RequestContextLens<User?>,
    permissionsLens: RequestContextLens<RolePermissions>,
    fetchPermissionsViaIdQuery: FetchPermissionsViaIdQuery,
): Filter = Filter { next: HttpHandler ->
    {
        request: Request ->
        val permissions = currentUser(request)?.let {
            fetchPermissionsViaIdQuery(it.roleId)
        } ?: RolePermissions.ANONYMOUS_ROLE
        val authorizedRequest = request.with( permissionsLens of permissions)
        next(authorizedRequest)
    }
}