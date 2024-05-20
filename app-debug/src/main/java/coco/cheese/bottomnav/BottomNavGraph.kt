package coco.cheese.bottomnav

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost

import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import coco.cheese.screen.DebugScreen
import coco.cheese.screen.HomeScreen
import coco.cheese.screen.ModuleScreen
import coco.cheese.screen.PermissionsScreen


@ExperimentalMaterial3Api
@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomBarScreen.Home.route) {
        composable(route = BottomBarScreen.Home.route)
        {
            HomeScreen()
        }
        composable(route = BottomBarScreen.Debug.route)
        {
            DebugScreen()
        }
        composable(route = BottomBarScreen.Permissions.route)
        {
            PermissionsScreen()
        }
        composable(route = BottomBarScreen.Module.route)
        {
            ModuleScreen()
        }

    }

}