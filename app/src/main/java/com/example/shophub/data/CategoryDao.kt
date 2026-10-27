package com.example.shophub.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shophub.model.Category

@Dao
interface CategoryDao {

    @Insert
    suspend fun inserir(category: Category)

    @Query("SELECT * FROM categorias ORDER BY id")
    suspend fun buscarTodos(): List<Category>

    // Consulta filtrada por campo: usada pela tela de detalhes da categoria.
    // Retorna nulo caso o id não exista (ex.: item removido).
    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun buscarPorId(id: Int): Category?

    @Update
    suspend fun atualizar(category: Category)

    @Delete
    suspend fun deletar(category: Category)
}
