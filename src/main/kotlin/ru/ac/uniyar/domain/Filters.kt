package ru.ac.uniyar.domain

import org.http4k.core.*
import org.http4k.core.cookie.cookie
import org.http4k.lens.RequestContextLens
import ru.ac.uniyar.queries.FetchPermissionsQ
import ru.ac.uniyar.queries.FetchUserQ



fun authenticationFilter(
    currentUser: RequestContextLens<User?>,
    fetchUserQ: FetchUserQ,
    jwtTools: JwtTools,
): Filter = Filter { next: HttpHandler ->
    {request: Request ->
        val requestWithUser = request.cookie("token")?.value?.let { token ->
            jwtTools.subject(token)
        }?.let { userId ->
            fetchUserQ(userId)
        }?.let { user ->
            request.with( currentUser of user)
        }?: request
        next(requestWithUser)
    }
}
fun authorizationFilter(
    currentUser: RequestContextLens<User?>,
    permissionsLens: RequestContextLens<RolePermissions>,
    fetchPermissionsQ: FetchPermissionsQ,
): Filter = Filter { next: HttpHandler ->
    {
        request: Request ->
        val permissions = currentUser(request)?.let {
            fetchPermissionsQ(it.roleId)
        } ?: RolePermissions.ANONYMOUS_ROLE
        val authorizedRequest = request.with( permissionsLens of permissions)
        next(authorizedRequest)
    }
}