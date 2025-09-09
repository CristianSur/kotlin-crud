package com.example.routes

import com.example.model.ProductDTO
import com.example.model.Products
import com.example.model.toProductDTO
import io.ktor.http.*
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
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText(
                "Invalid ID",
                status = HttpStatusCode.BadRequest
            )

            val product = transaction {
                Products.select(Products.id eq id).mapNotNull { it.toProductDTO() }.singleOrNull()
            }

            product?.let {
                call.respond(it)
            } ?: call.respondText("Not found", status = HttpStatusCode.NotFound)
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
                status = HttpStatusCode.BadRequest
            )

            val updated = call.receive<ProductDTO>()

            val rowsAffected = transaction {
                Products.update({ Products.id eq id }) {
                    it[name] = updated.name
                    it[price] = updated.price.toBigDecimal()
                }
            }

            if (rowsAffected > 0) {
                call.respondText("Updated $rowsAffected rows", status = HttpStatusCode.OK)
            } else {
                call.respondText("Updated $rowsAffected rows", status = HttpStatusCode.NotFound)
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText(
                "Invalid ID",
                status = HttpStatusCode.BadRequest
            )

            val deleted = transaction {
                Products.deleteWhere { Products.id eq id }
            }

            if (deleted > 0) {
                call.respondText("Deleted $deleted rows", status = HttpStatusCode.OK)
            } else {
                call.respondText("Deleted $deleted rows", status = HttpStatusCode.NotFound)
            }
        }
    }
}