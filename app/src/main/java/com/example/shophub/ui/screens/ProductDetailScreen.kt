package com.example.shophub.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.shophub.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProductDetailScreen(
    innerPadding: PaddingValues,
    productId: Int,
    onEdit: () -> Unit,
    onBackToList: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    var product by remember { mutableStateOf<Product?>(null) }
    var categoryName by remember { mutableStateOf("Sem categoria") }
    var loading by remember { mutableStateOf(true) }

    // Recebe só o ID pela navegação e busca o produto direto no banco (buscarPorId)
    LaunchedEffect(productId) {
        try {
            withContext(Dispatchers.IO) {
                val found = db.productDao().buscarPorId(productId)
                product = found
                if (found != null) {
                    categoryName = db.categoryDao().buscarPorId(found.categoryId)?.name
                        ?: "Sem categoria"
                }
            }
        } catch (e: Exception) {
            Log.e("ProductDetailScreen", "Erro ao buscar produto $productId", e)
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (loading) {
            Text("Carregando...")
            return
        }

        val current = product
        if (current == null) {
            Text(
                text = "Produto não encontrado.",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(onClick = onBackToList) {
                Text("Voltar para produtos")
            }
            return
        }

        Text(
            text = "Detalhes do produto",
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
                Text(text = "Preço: R$ ${current.price}")
                Text(text = "Categoria: $categoryName")
                Text(text = "Descrição: ${current.description}")
                Text(
                    text = if (current.isFavorite) "Marcado como favorito" else "Ainda não favoritado"
                )
            }
        }

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar produto")
        }

        OutlinedButton(
            onClick = onBackToList,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar para a lista")
        }
    }
}
