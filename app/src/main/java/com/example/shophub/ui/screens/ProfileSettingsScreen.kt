package com.example.shophub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileSettingsScreen(
    innerPadding: PaddingValues,
    onSaveProfile: () -> Unit
) {
    var userName by remember { mutableStateOf("Usuário ShopHub") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Configurações do perfil",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Personalize a experiência do usuário",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Esta tela é fictícia e serve para dar ao app uma experiência mais real."
                )
            }
        }

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Nome do usuário") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefone") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Cidade") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
            Text(
                text = "Receber notificações",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it }
            )
            Text(
                text = "Ativar modo escuro",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            onClick = {
                message = "Configurações salvas com sucesso."
                onSaveProfile()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar configurações")
        }

        if (message.isNotBlank()) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}