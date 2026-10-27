package com.example.shophub.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Formulário de categoria. Serve para duas coisas:
 *  - categoryId == null -> ADICIONAR (dao.inserir)
 *  - categoryId != null -> EDITAR    (dao.buscarPorId + dao.atualizar)
 */
@Composable
fun AddCategoryScreen(
    innerPadding: PaddingValues,
    onSave: () -> Unit,
    categoryId: Int? = null
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val isEditing = categoryId != null

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var existing by remember { mutableStateOf<Category?>(null) }

    LaunchedEffect(Unit) {
        if (categoryId != null) {
            try {
                withContext(Dispatchers.IO) {
                    val category = db.categoryDao().buscarPorId(categoryId)
                    if (category != null) {
                        existing = category
                        name = category.name
                        description = category.description
                    } else {
                        errorMessage = "Categoria não encontrada."
                    }
                }
            } catch (e: Exception) {
                Log.e("AddCategoryScreen", "Erro ao carregar categoria", e)
                errorMessage = "Erro ao carregar dados."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isEditing) "Editar categoria" else "Adicionar categoria",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome da categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição da categoria") },
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
                when {
                    name.isBlank() || description.isBlank() -> {
                        errorMessage = "Preencha todos os campos."
                    }
                    isEditing && existing == null -> {
                        errorMessage = "Categoria não encontrada."
                    }
                    else -> {
                        errorMessage = ""
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val original = existing
                                if (original == null) {
                                    db.categoryDao().inserir(
                                        Category(name = name, description = description)
                                    )
                                } else {
                                    db.categoryDao().atualizar(
                                        original.copy(name = name, description = description)
                                    )
                                }
                                withContext(Dispatchers.Main) { onSave() }
                            } catch (e: Exception) {
                                Log.e("AddCategoryScreen", "Erro ao salvar categoria", e)
                                errorMessage = "Erro ao salvar a categoria."
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Salvar alterações" else "Salvar categoria")
        }
    }
}
