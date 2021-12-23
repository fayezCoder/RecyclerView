
package com.fayezcoder.write.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.fayezcoder.write.ui.routes.AppSettingsRoute
import com.fayezcoder.write.ui.routes.EditNoteRoute
import com.fayezcoder.write.ui.routes.MultiPaneRoute
import com.fayezcoder.write.ui.routes.NoteListRoute
import com.fayezcoder.write.ui.routes.ViewNoteRoute
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable fun NotepadComposeApp() {
  val navController = rememberNavController()
  val systemUiController = rememberSystemUiController()

  val configuration = LocalConfiguration.current
  val startDestination =
    if(configuration.screenWidthDp >= 600)
      "MultiPane"
    else
      "NoteList"

  MaterialTheme {
    NavHost(
      navController = navController,
      startDestination = startDestination
    ) {
      NoteListRoute(navController)
      ViewNoteRoute(navController)
      EditNoteRoute(navController)
      MultiPaneRoute(navController)
      AppSettingsRoute(navController)
    }
  }

  SideEffect {
    systemUiController.setNavigationBarColor(
      color = Color.White
    )
  }
}