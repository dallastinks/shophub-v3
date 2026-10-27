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
import androidx.compose.material3.OutlinedTextField
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

/**
 * Formulário de produto. Serve para duas coisas:
 *  - productId == null  -> ADICIONAR (dao.inserir)
 *  - productId != null  -> EDITAR    (busca com dao.buscarPorId e salva com dao.atualizar)
 */
@Composable
fun AddProductScreen(
    innerPadding: PaddingValues,
    onSave: () -> Unit,
    productId: Int? = null
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val isEditing = productId != null

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var categoryIdText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    // Produto original (no modo edição), para preservar id e "favorito" ao atualizar
    var existing by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                categories = db.categoryDao().buscarTodos()
                if (productId != null) {
                    val product = db.productDao().buscarPorId(productId)
                    if (product != null) {
                        existing = product
                        name = product.name
                        price = product.price
                        categoryIdText = product.categoryId.toString()
                        description = product.description
                    } else {
                        errorMessage = "Produto não encontrado."
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AddProductScreen", "Erro ao carregar dados do formulário", e)
            errorMessage = "Erro ao carregar dados."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isEditing) "Editar produto" else "Adicionar produto",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Categorias disponíveis:")
                categories.forEach { category ->
                    Text("${category.id} - ${category.name}")
                }
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome do produto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = categoryIdText,
            onValueChange = { categoryIdText = it },
            label = { Text("ID da categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                val categoryId = categoryIdText.toIntOrNull()
                val categoryExists = categories.any { it.id == categoryId }

                when {
                    name.isBlank() || price.isBlank() || description.isBlank() || categoryId == null -> {
                        errorMessage = "Preencha todos os campos corretamente."
                    }
                    !categoryExists -> {
                        errorMessage = "Informe um ID de categoria válido."
                    }
                    isEditing && existing == null -> {
                        errorMessage = "Produto não encontrado."
                    }
                    else -> {
                        errorMessage = ""
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val original = existing
                                if (original == null) {
                                    // INSERIR: id = 0 -> o Room gera o id automaticamente
                                    db.productDao().inserir(
                                        Product(
                                            name = name,
                                            price = price,
                                            categoryId = categoryId,
                                            description = description
                                        )
                                    )
                                } else {
                                    // ATUALIZAR: mantém o mesmo id (e o "favorito")
                                    db.productDao().atualizar(
                                        original.copy(
                                            name = name,
                                            price = price,
                                            categoryId = categoryId,
                                            description = description
                                        )
                                    )
                                }
                                // Navegação precisa acontecer na thread principal
                                withContext(Dispatchers.Main) { onSave() }
                            } catch (e: Exception) {
                                Log.e("AddProductScreen", "Erro ao salvar produto", e)
                                errorMessage = "Erro ao salvar o produto."
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Salvar alterações" else "Salvar produto")
        }
    }
}
