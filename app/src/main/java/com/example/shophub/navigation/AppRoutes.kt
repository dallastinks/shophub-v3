package com.example.shophub.navigation

sealed class AppRoutes(val route: String) {
    data object Login : AppRoutes("login")
    data object Home : AppRoutes("home")
    data object ProductList : AppRoutes("products")
    data object AddProduct : AppRoutes("add_product")
    data object EditProduct : AppRoutes("edit_product/{productId}") {
        fun createRoute(productId: Int) = "edit_product/$productId"
    }
    data object ProductDetail : AppRoutes("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }

    data object CategoryList : AppRoutes("categories")
    data object AddCategory : AppRoutes("add_category")
    data object EditCategory : AppRoutes("edit_category/{categoryId}") {
        fun createRoute(categoryId: Int) = "edit_category/$categoryId"
    }
    data object CategoryDetail : AppRoutes("category_detail/{categoryId}") {
        fun createRoute(categoryId: Int) = "category_detail/$categoryId"
    }

    data object ProfileSettings : AppRoutes("profile_settings")
}
