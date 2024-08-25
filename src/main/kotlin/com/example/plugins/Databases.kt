package com.example.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/kt_ktor_restful_api",
        driver = "org.postgresql.Driver",
        user = "banggibima",
        password = ""
    )
}