package com.example.shophub.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.shophub.navigation.AppRoutes

@Composable
fun ShopHubBottomBar(
    navController: NavHostController
) {
    val items = listOf(
        AppRoutes.Home,
        AppRoutes.ProductList,
        AppRoutes.CategoryList,
        AppRoutes.ProfileSettings
    )

    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            val label = when (screen) {
                AppRoutes.Home -> "Início"
                AppRoutes.ProductList -> "Produtos"
                AppRoutes.CategoryList -> "Categorias"
                AppRoutes.ProfileSettings -> "Perfil"
                else -> ""
            }

            val icon = when (screen) {
                AppRoutes.Home -> Icons.Default.Home
                AppRoutes.ProductList -> Icons.Default.ShoppingCart
                AppRoutes.CategoryList -> Icons.Default.Category
                AppRoutes.ProfileSettings -> Icons.Default.Person
                else -> Icons.Default.Home
            }

            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(AppRoutes.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = { Text(label) }
            )
        }
    }
}