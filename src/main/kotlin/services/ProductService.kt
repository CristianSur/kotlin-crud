package com.example.services

import com.example.model.ProductDTO
import com.example.model.Products
import com.example.model.toProductDTO
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun getAllProducts() = transaction {
    Products.selectAll()
}

fun getProductById(id: Int) = transaction {
    Products.select(Products.id eq id).mapNotNull { it.toProductDTO() }.singleOrNull()
}

fun saveProduct(product: ProductDTO) = transaction {
    Products.insertAndGetId {
        it[name] = product.name
        it[price] = product.price.toBigDecimal()
    }.value
}

fun updateProduct(product: ProductDTO) = transaction {
    Products.update({ Products.id eq product.id }) {
        it[name] = product.name
        it[price] = product.price.toBigDecimal()
    }
}

fun deleteProduct(id: Int) = transaction {
    Products.deleteWhere { Products.id eq id }
}
