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
fun CategoryListScreen(
    innerPadding: PaddingValues,
    onAddCategory: () -> Unit,
    onEditCategory: (Int) -> Unit,
    onOpenDetails: (Int) -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    suspend fun carregar() {
        try {
            withContext(Dispatchers.IO) {
                categories = db.categoryDao().buscarTodos()
                products = db.productDao().buscarTodos()
            }
        } catch (e: Exception) {
            Log.e("CategoryListScreen", "Erro ao buscar categorias", e)
        }
    }

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
            text = "Lista de categorias",
            style = MaterialTheme.typography.headlineSmall
        )

        Button(
            onClick = onAddCategory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar nova categoria")
        }

        if (categories.isEmpty()) {
            Text("Nenhuma categoria cadastrada.")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories, key = { it.id }) { category ->
                CategoryCard(
                    category = category,
                    productCount = products.count { it.categoryId == category.id },
                    onToggleFeatured = {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                db.categoryDao().atualizar(
                                    category.copy(featured = !category.featured)
                                )
                                carregar()
                            } catch (e: Exception) {
                                Log.e("CategoryListScreen", "Erro ao atualizar destaque", e)
                            }
                        }
                    },
                    onRemove = {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                db.categoryDao().deletar(category)
                                carregar()
                            } catch (e: Exception) {
                                Log.e("CategoryListScreen", "Erro ao remover categoria", e)
                            }
                        }
                    },
                    onEdit = { onEditCategory(category.id) },
                    onDetails = { onOpenDetails(category.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    productCount: Int,
    onToggleFeatured: () -> Unit,
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
                text = category.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = category.description)
            Text(text = "Produtos associados: $productCount")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Checkbox(
                        checked = category.featured,
                        onCheckedChange = { onToggleFeatured() }
                    )
                    Text(
                        text = "Destaque",
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
