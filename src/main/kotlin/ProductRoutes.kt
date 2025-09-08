package com.example

import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Route.productRoutes() {
    route("/products") {
        get {
            val products = transaction {
                Products.selectAll().map { it.toProductDTO() }
            }
            call.respond(products)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@get
            }

            val product = transaction {
                Products.select(Products.id eq id)
                    .mapNotNull { it.toProductDTO() }
                    .singleOrNull()
            }

            if (product == null) {
                call.respondText("Not found", status = io.ktor.http.HttpStatusCode.NotFound)
            } else {
                call.respond(product)
            }
        }

        post {
            val product = call.receive<ProductDTO>()
            val id = transaction {
                Products.insertAndGetId {
                    it[name] = product.name
                    it[price] = product.price.toBigDecimal()
                }.value
            }
            call.respond(ProductDTO(id, product.name, product.price))
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respondText(
                "Invalid ID",
                status = io.ktor.http.HttpStatusCode.BadRequest
            )
            val updated = call.receive<ProductDTO>()

            transaction {
                Products.update({ Products.id eq id }) {
                    it[name] = updated.name
                    it[price] = updated.price.toBigDecimal()
                }
            }
            call.respondText("Updated")
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)

            transaction {
                Products.deleteWhere { Products.id eq id }
            }
            call.respondText("Deleted")
        }
    }
}