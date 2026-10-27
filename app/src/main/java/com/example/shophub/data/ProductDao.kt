package com.example.shophub.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shophub.model.Product

@Dao
interface ProductDao {

    @Insert
    suspend fun inserir(product: Product)

    @Query("SELECT * FROM produtos ORDER BY id")
    suspend fun buscarTodos(): List<Product>

    // Consulta filtrada por campo: usada pela tela de detalhes do produto.
    @Query("SELECT * FROM produtos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Product?

    // Segunda consulta filtrada: produtos relacionados na tela de detalhes da categoria.
    @Query("SELECT * FROM produtos WHERE categoryId = :categoryId ORDER BY id")
    suspend fun buscarPorCategoria(categoryId: Int): List<Product>

    @Update
    suspend fun atualizar(product: Product)

    @Delete
    suspend fun deletar(product: Product)
}
