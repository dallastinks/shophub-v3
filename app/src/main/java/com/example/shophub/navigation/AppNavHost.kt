package com.example.shophub.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shophub.ui.components.ShopHubBottomBar
import com.example.shophub.ui.components.ShopHubTopBar
import com.example.shophub.ui.screens.AddCategoryScreen
import com.example.shophub.ui.screens.AddProductScreen
import com.example.shophub.ui.screens.CategoryDetailScreen
import com.example.shophub.ui.screens.CategoryListScreen
import com.example.shophub.ui.screens.HomeScreen
import com.example.shophub.ui.screens.LoginScreen
import com.example.shophub.ui.screens.ProductDetailScreen
import com.example.shophub.ui.screens.ProductListScreen
import com.example.shophub.ui.screens.ProfileSettingsScreen

@Composable
fun ShopHubApp() {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    val topBarTitle = when {
        currentRoute == AppRoutes.Login.route -> "Entrar"
        currentRoute == AppRoutes.Home.route -> "ShopHub"
        currentRoute == AppRoutes.ProductList.route -> "Produtos"
        currentRoute == AppRoutes.AddProduct.route -> "Novo produto"
        currentRoute?.startsWith("edit_product") == true -> "Editar produto"
        currentRoute?.startsWith("product_detail") == true -> "Detalhes do produto"
        currentRoute == AppRoutes.CategoryList.route -> "Categorias"
        currentRoute == AppRoutes.AddCategory.route -> "Nova categoria"
        currentRoute?.startsWith("edit_category") == true -> "Editar categoria"
        currentRoute?.startsWith("category_detail") == true -> "Detalhes da categoria"
        currentRoute == AppRoutes.ProfileSettings.route -> "Perfil"
        else -> "ShopHub"
    }

    val bottomBarRoutes = setOf(
        AppRoutes.Home.route,
        AppRoutes.ProductList.route,
        AppRoutes.CategoryList.route,
        AppRoutes.ProfileSettings.route
    )

    Scaffold(
        topBar = {
            ShopHubTopBar(
                title = topBarTitle,
                canGoBack = currentRoute !in setOf(
                    AppRoutes.Login.route,
                    AppRoutes.Home.route,
                    AppRoutes.ProductList.route,
                    AppRoutes.CategoryList.route,
                    AppRoutes.ProfileSettings.route
                ),
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                ShopHubBottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Login.route
        ) {
            composable(AppRoutes.Login.route) {
                LoginScreen(
                    innerPadding = innerPadding,
                    onLoginSuccess = {
                        navController.navigate(AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(AppRoutes.Home.route) {
                HomeScreen(
                    innerPadding = innerPadding,
                    onOpenProducts = { navController.navigate(AppRoutes.ProductList.route) },
                    onOpenCategories = { navController.navigate(AppRoutes.CategoryList.route) },
                    onOpenAddProduct = { navController.navigate(AppRoutes.AddProduct.route) },
                    onOpenAddCategory = { navController.navigate(AppRoutes.AddCategory.route) },
                    onOpenProfileSettings = { navController.navigate(AppRoutes.ProfileSettings.route) }
                )
            }

            composable(AppRoutes.ProductList.route) {
                ProductListScreen(
                    innerPadding = innerPadding,
                    onAddProduct = { navController.navigate(AppRoutes.AddProduct.route) },
                    onEditProduct = { productId ->
                        navController.navigate(AppRoutes.EditProduct.createRoute(productId))
                    },
                    onOpenDetails = { productId ->
                        navController.navigate(AppRoutes.ProductDetail.createRoute(productId))
                    }
                )
            }

            composable(AppRoutes.AddProduct.route) {
                AddProductScreen(
                    innerPadding = innerPadding,
                    onSave = {
                        navController.navigate(AppRoutes.ProductList.route) {
                            popUpTo(AppRoutes.AddProduct.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = AppRoutes.EditProduct.route,
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: -1
                AddProductScreen(
                    innerPadding = innerPadding,
                    productId = productId,
                    onSave = { navController.popBackStack() }
                )
            }

            composable(
                route = AppRoutes.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: -1
                ProductDetailScreen(
                    innerPadding = innerPadding,
                    productId = productId,
                    onEdit = { navController.navigate(AppRoutes.EditProduct.createRoute(productId)) },
                    onBackToList = { navController.popBackStack() }
                )
            }

            composable(AppRoutes.CategoryList.route) {
                CategoryListScreen(
                    innerPadding = innerPadding,
                    onAddCategory = { navController.navigate(AppRoutes.AddCategory.route) },
                    onEditCategory = { categoryId ->
                        navController.navigate(AppRoutes.EditCategory.createRoute(categoryId))
                    },
                    onOpenDetails = { categoryId ->
                        navController.navigate(AppRoutes.CategoryDetail.createRoute(categoryId))
                    }
                )
            }

            composable(AppRoutes.AddCategory.route) {
                AddCategoryScreen(
                    innerPadding = innerPadding,
                    onSave = {
                        navController.navigate(AppRoutes.CategoryList.route) {
                            popUpTo(AppRoutes.AddCategory.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = AppRoutes.EditCategory.route,
                arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
                AddCategoryScreen(
                    innerPadding = innerPadding,
                    categoryId = categoryId,
                    onSave = { navController.popBackStack() }
                )
            }

            composable(
                route = AppRoutes.CategoryDetail.route,
                arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: -1
                CategoryDetailScreen(
                    innerPadding = innerPadding,
                    categoryId = categoryId,
                    onEdit = { navController.navigate(AppRoutes.EditCategory.createRoute(categoryId)) },
                    onBackToList = { navController.popBackStack() }
                )
            }

            composable(AppRoutes.ProfileSettings.route) {
                ProfileSettingsScreen(
                    innerPadding = innerPadding,
                    onSaveProfile = { }
                )
            }
        }
    }
}