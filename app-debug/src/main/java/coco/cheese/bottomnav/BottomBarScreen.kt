package coco.cheese.bottomnav

import coco.cheese.R

sealed class BottomBarScreen(
    val route:String,
    val title:String,
    val icon:Int,
    val icon_focused:Int
) {


    object Home: BottomBarScreen(
        route = "home",
        title = "主页",
        icon = coco.cheese.core.R.drawable.home,
        icon_focused = coco.cheese.core.R.drawable.home,
    )

    object Debug: BottomBarScreen(
        route = "debug",
        title = "日志",
        icon = coco.cheese.core.R.drawable.debug,
        icon_focused = coco.cheese.core.R.drawable.debug,
    )

    object Permissions: BottomBarScreen(
        route = "permissions",
        title = "权限",
        icon = coco.cheese.core.R.drawable.permissions,
        icon_focused = coco.cheese.core.R.drawable.permissions,
    )

    object Module: BottomBarScreen(
        route = "module",
        title = "模块",
        icon = coco.cheese.core.R.drawable.module,
        icon_focused = coco.cheese.core.R.drawable.module,
    )





}