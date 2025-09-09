package com.example.routes

import com.example.model.ProductDTO
import com.example.services.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.productRoutes() {
    route("/products") {
        get {
            val products = getAllProducts()
            call.respond(products)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText(
                "Invalid ID",
                status = HttpStatusCode.BadRequest
            )

            val product = getProductById(id)

            product?.let {
                call.respond(it)
            } ?: call.respondText("Not found", status = HttpStatusCode.NotFound)
        }

        post {
            val product = call.receive<ProductDTO>()
            val id = saveProduct(product)
            call.respond(ProductDTO(id, product.name, product.price))
        }

        put("/{id}") {
            val updated = call.receive<ProductDTO>()

            val rowsAffected = updateProduct(updated)

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

            val deleted = deleteProduct(id)

            if (deleted > 0) {
                call.respondText("Deleted $deleted rows", status = HttpStatusCode.OK)
            } else {
                call.respondText("Deleted $deleted rows", status = HttpStatusCode.NotFound)
            }
        }
    }
}