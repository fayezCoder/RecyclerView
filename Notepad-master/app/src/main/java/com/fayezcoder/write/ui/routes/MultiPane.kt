
package com.fayezcoder.write.ui.routes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.fayezcoder.write.R
import com.fayezcoder.write.android.NotepadViewModel
import com.fayezcoder.write.models.NoteMetadata
import com.fayezcoder.write.ui.content.NoteListContent
import com.fayezcoder.write.ui.menus.NoteListMenu
import com.fayezcoder.write.ui.routes.RightPaneState.Edit
import com.fayezcoder.write.ui.routes.RightPaneState.Empty
import com.fayezcoder.write.ui.routes.RightPaneState.View
import com.fayezcoder.write.ui.widgets.*
import kotlinx.coroutines.launch

sealed interface RightPaneState {
  object Empty: RightPaneState
  data class View(val id: Long): RightPaneState
  data class Edit(val id: Long? = null): RightPaneState
}

@Composable fun MultiPane(
  navController: NavController,
  vm: NotepadViewModel = hiltViewModel()
) {
  val state = produceState(listOf<NoteMetadata>()) {
    launch {
      value = vm.getNoteMetadata()
    }
  }

  MultiPane(
    notes = state.value,
    navController = navController,
    vm = vm
  )
}

@Composable fun MultiPane(
  notes: List<NoteMetadata>,
  navController: NavController? = null,
  vm: NotepadViewModel? = null
) {
  val rightPaneState = remember { mutableStateOf<RightPaneState>(Empty) }

  val showAboutDialog = remember { mutableStateOf(false) }
  AboutDialog(showAboutDialog, vm)

  val showSettingsDialog = remember { mutableStateOf(false) }
  SettingsDialog(showSettingsDialog)

  Scaffold(
    topBar = {
      TopAppBar(
        title = { AppBarText(stringResource(id = R.string.app_name)) },
        backgroundColor = colorResource(id = R.color.primary),
        actions = {
          when(val state = rightPaneState.value) {
            Empty -> {
              NoteListMenu(
                navController = navController,
                vm = vm,
                showAboutDialog = showAboutDialog,
                showSettingsDialog = showSettingsDialog,
              )
            }
/*

            is View -> {
              EditButton(state.id, navController)
              DeleteButton(state.id, navController, vm)
              NoteViewEditMenu(note.contents.text, vm)
            }

            is Edit -> {
              SaveButton(state.id, textState.value.text, navController, vm)
              DeleteButton(state.id, navController, vm)
              NoteViewEditMenu(textState.value.text, vm)
            }
 */
          }
        }
      )
    },
    floatingActionButton = {
      if(rightPaneState.value == Empty) {
        FloatingActionButton(
          onClick = { rightPaneState.value = Edit() },
          backgroundColor = colorResource(id = R.color.primary),
          content = {
            Icon(
              imageVector = Icons.Filled.Add,
              contentDescription = null,
              tint = Color.White
            )
          }
        )
      }
    },
    content = {
      Row {
        Box(modifier = Modifier.weight(1f)) {
          NoteListContent(notes, rightPaneState, navController)
        }

        Divider(
          modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
        )

        Box(modifier = Modifier.weight(2f)) {
          when(val state = rightPaneState.value) {
            Empty -> EmptyDetails()
            is View -> ViewNote(id = state.id, isMultiPane = true)
            is Edit -> EditNote(id = state.id, isMultiPane = true)
          }
        }
      }
    }
  )
}

@Composable fun EmptyDetails() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Image(
      painter = painterResource(id = R.drawable.notepad_logo),
      contentDescription = null,
      modifier = Modifier
        .height(512.dp)
        .width(512.dp)
        .alpha(0.5f)
    )
  }
}

@Suppress("FunctionName")
fun NavGraphBuilder.MultiPaneRoute(
  navController: NavController
) = composable(route = "MultiPane") {
  MultiPane(
    navController = navController
  )
}

@Preview(device = Devices.PIXEL_C)
@Composable fun MultiPanePreview() = MaterialTheme {
  MultiPane(
    notes = listOf(
      NoteMetadata(title = "Test Note 1"),
      NoteMetadata(title = "Test Note 2")
    )
  )
}

@Preview(device = Devices.PIXEL_C)
@Composable fun MultiPaneEmptyPreview() = MaterialTheme {
  MultiPane(
    notes = emptyList()
  )
}