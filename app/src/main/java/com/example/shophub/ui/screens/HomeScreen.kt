package com.example.shophub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shophub.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    onOpenProducts: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenAddProduct: () -> Unit,
    onOpenAddCategory: () -> Unit,
    onOpenProfileSettings: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    var productCount by remember { mutableIntStateOf(0) }
    var categoryCount by remember { mutableIntStateOf(0) }

    // Resumo lido do banco ao abrir a tela
    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                productCount = db.productDao().buscarTodos().size
                categoryCount = db.categoryDao().buscarTodos().size
            }
        } catch (e: Exception) {
            Log.e("HomeScreen", "Erro ao carregar resumo", e)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Bem-vindo ao ShopHub",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Gerencie produtos e categorias com navegação real, listas, formulários e telas de detalhes.",
            style = MaterialTheme.typography.bodyLarge
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Resumo do app",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "Produtos cadastrados: $productCount")
                Text(text = "Categorias cadastradas: $categoryCount")
            }
        }

        Button(
            onClick = onOpenProducts,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver produtos")
        }

        Button(
            onClick = onOpenCategories,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver categorias")
        }

        Button(
            onClick = onOpenAddProduct,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar produto")
        }

        Button(
            onClick = onOpenAddCategory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar categoria")
        }

        Button(
            onClick = onOpenProfileSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Configurações do perfil")
        }
    }
}