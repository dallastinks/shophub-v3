package com.example.shophub.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProductListScreen(
    innerPadding: PaddingValues,
    onAddProduct: () -> Unit,
    onEditProduct: (Int) -> Unit,
    onOpenDetails: (Int) -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }

    // Busca os dados no banco (Room) - sempre fora da thread principal.
    suspend fun carregar() {
        try {
            withContext(Dispatchers.IO) {
                products = db.productDao().buscarTodos()
                categories = db.categoryDao().buscarTodos()
            }
        } catch (e: Exception) {
            Log.e("ProductListScreen", "Erro ao buscar produtos", e)
        }
    }

    // Ao abrir a tela, busca os itens do banco.
    LaunchedEffect(Unit) {
        carregar()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Lista de produtos",
            style = MaterialTheme.typography.headlineSmall
        )

        Button(
            onClick = onAddProduct,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar novo produto")
        }

        if (products.isEmpty()) {
            Text("Nenhum produto cadastrado.")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    categoryName = categories.find { it.id == product.categoryId }?.name
                        ?: "Sem categoria",
                    onToggleFavorite = {
                        // Atualizar: grava a mudança de "favorito" no banco
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                db.productDao().atualizar(
                                    product.copy(isFavorite = !product.isFavorite)
                                )
                                carregar()
                            } catch (e: Exception) {
                                Log.e("ProductListScreen", "Erro ao favoritar produto", e)
                            }
                        }
                    },
                    onRemove = {
                        // Remover: apaga a linha do banco
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                db.productDao().deletar(product)
                                carregar()
                            } catch (e: Exception) {
                                Log.e("ProductListScreen", "Erro ao remover produto", e)
                            }
                        }
                    },
                    onEdit = { onEditProduct(product.id) },
                    onDetails = { onOpenDetails(product.id) }
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    categoryName: String,
    onToggleFavorite: () -> Unit,
    onRemove: () -> Unit,
    onEdit: () -> Unit,
    onDetails: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "Preço: R$ ${product.price}")
            Text(text = "Categoria: $categoryName")
            Text(text = product.description)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Checkbox(
                        checked = product.isFavorite,
                        onCheckedChange = { onToggleFavorite() }
                    )
                    Text(
                        text = "Favorito",
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Row {
                    IconButton(onClick = onDetails) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Detalhes"
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar"
                        )
                    }
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remover"
                        )
                    }
                }
            }
        }
    }
}
