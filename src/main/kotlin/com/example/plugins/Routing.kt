package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.DriverManager
import java.time.LocalDateTime
import java.util.*

fun Application.configureRouting() {
    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/kt_ktor_restful_api",
        "banggibima",
        ""
    )

    val userService = UserService(connection)

    routing {
        get("/users") {
            val users = userService.findAll()
            call.respond(HttpStatusCode.OK, users)
        }

        get("/users/{id}") {
            val id = call.parameters["id"]?.let { UUID.fromString(it) }
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@get
            }

            val user = userService.findById(id)
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        }

        post("/users") {
            val user = call.receive<User>()
            // Generate a new UUID for the user
            val newUser = user.copy(
                id = UUID.randomUUID(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            userService.save(newUser)
            call.respond(HttpStatusCode.Created, newUser)
        }

        put("/users/{id}") {
            val id = call.parameters["id"]?.let { UUID.fromString(it) }
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@put
            }

            val updatedUser = call.receive<User>()
            val existingUser = userService.findById(id)
            if (existingUser != null) {
                val userToUpdate = existingUser.copy(
                    username = updatedUser.username,
                    password = updatedUser.password,
                    updatedAt = LocalDateTime.now()
                )
                userService.update(userToUpdate)
                call.respond(HttpStatusCode.OK, userToUpdate)
            } else {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        }

        delete("/users/{id}") {
            val id = call.parameters["id"]?.let { UUID.fromString(it) }
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@delete
            }

            val existingUser = userService.findById(id)
            if (existingUser != null) {
                userService.remove(id)
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        }
    }
}