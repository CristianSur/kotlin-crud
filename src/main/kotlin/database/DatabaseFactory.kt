package com.example.database

import com.example.model.Products
import io.ktor.server.application.ApplicationEnvironment
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    //    Solution for H2 (in-memoru database)
    //    fun init() {
//        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
//        transaction {
//            SchemaUtils.create(Products)
//        }
//    }
    fun init(environment: ApplicationEnvironment) {
        val config = environment.config.config("database")
        val driver = config.property("driver").getString()
        val url = config.property("url").getString()
        val user = config.property("user").getString()
        val password = config.property("password").getString()
        val databaseName = config.property("name").getString()

        Database.Companion.connect(
            url = "$url/$databaseName",
            driver = driver,
            user = user,
            password = password
        )

        transaction {
            SchemaUtils.create(Products)
        }

    }
}