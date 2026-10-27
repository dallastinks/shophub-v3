package com.example.shophub.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.shophub.data.AppDatabase
import com.example.shophub.model.Category
import com.example.shophub.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CategoryDetailScreen(
    innerPadding: PaddingValues,
    categoryId: Int,
    onEdit: () -> Unit,
    onBackToList: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    var category by remember { mutableStateOf<Category?>(null) }
    var relatedProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    // Recebe só o ID pela navegação e busca a categoria + produtos relacionados no banco
    LaunchedEffect(categoryId) {
        try {
            withContext(Dispatchers.IO) {
                category = db.categoryDao().buscarPorId(categoryId)
                relatedProducts = db.productDao().buscarPorCategoria(categoryId)
            }
        } catch (e: Exception) {
            Log.e("CategoryDetailScreen", "Erro ao buscar categoria $categoryId", e)
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (loading) {
            Text("Carregando...")
            return
        }

        val current = category
        if (current == null) {
            Text(
                text = "Categoria não encontrada.",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(onClick = onBackToList) {
                Text("Voltar para categorias")
            }
            return
        }

        Text(
            text = "Detalhes da categoria",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = current.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = "Descrição: ${current.description}")
                Text(
                    text = if (current.featured) "Categoria em destaque" else "Categoria comum"
                )
                Text(text = "Quantidade de produtos: ${relatedProducts.size}")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Produtos relacionados",
                    style = MaterialTheme.typography.titleMedium
                )

                if (relatedProducts.isEmpty()) {
                    Text("Nenhum produto cadastrado nesta categoria.")
                } else {
                    relatedProducts.forEach { product ->
                        Text("- ${product.name} | R$ ${product.price}")
                    }
                }
            }
        }

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar categoria")
        }

        OutlinedButton(
            onClick = onBackToList,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar para a lista")
        }
    }
}
