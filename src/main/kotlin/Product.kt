package com.example

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object Products : IntIdTable() {
    val name = varchar("name", 100)
    val price = decimal("price", 10, 2)
}

@Serializable
data class ProductDTO(val id: Int? = null, val name: String, val price: Double)

fun ResultRow.toProductDTO() = ProductDTO(
    id = this[Products.id].value,
    name = this[Products.name],
    price = this[Products.price].toDouble()
)