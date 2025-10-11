package com.example.database

import com.example.model.Products
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    /*
    * Solution for H2 in-memory database
    * Make sure to include the H2 dependency in your build.gradle.kts file:
    * implementation("com.h2database:h2:2.3.232")
    * And then use the following init function:
    * */
//      fun init() {
//        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
//        transaction {
//            SchemaUtils.create(Products)
//        }

    fun init(environment: ApplicationEnvironment) {
        val config = environment.config.config("database")
        val driver = config.property("driver").getString()
        
        // Read database host from environment variable, default to localhost
        val host = config.property("name").getString()
        val user =  config.property("user").getString()
        val password = config.property("password").getString()
        val databaseName =  config.property("name").getString()
        
        val url = "jdbc:postgresql://$host:5432/$databaseName"

        Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password
        )

        transaction {
            SchemaUtils.create(Products)
        }

    }
}