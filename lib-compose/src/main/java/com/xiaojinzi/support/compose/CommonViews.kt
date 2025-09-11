package com.xiaojinzi.support.compose

import androidx.compose.runtime.Composable

@Deprecated("SystemUiController is Deprecated")
@Composable
fun StateBar(
    content: @Composable () -> Unit
) {
    /*val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    SideEffect {
        systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
    }*/
    content()
}