
package com.fayezcoder.write.ui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fayezcoder.write.R
import com.fayezcoder.write.models.NoteMetadata
import com.fayezcoder.write.ui.routes.NoteListPreview
import com.fayezcoder.write.ui.routes.RightPaneState
import com.fayezcoder.write.ui.routes.RightPaneState.View
import com.fayezcoder.write.ui.routes.viewNote

@Composable fun NoteListContent(
  notes: List<NoteMetadata>,
  rightPaneState: MutableState<RightPaneState>? = null,
  navController: NavController? = null
) {
  when(notes.size) {
    0 -> Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(id = R.string.no_notes_found),
        color = colorResource(id = R.color.primary),
        fontWeight = FontWeight.Thin,
        fontSize = 30.sp
      )
    }

    else -> LazyColumn {
      items(notes.size) {
        Column(modifier = Modifier
          .clickable {
            val id = notes[it].metadataId

            rightPaneState?.let {
              it.value = View(id)
            } ?: navController?.viewNote(id)
          }
        ) {
          Text(
            text = notes[it].title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
              .padding(
                horizontal = 16.dp,
                vertical = 12.dp
              )
          )

          Divider()
        }
      }
    }
  }
}

@Preview @Composable fun NoteListContentPreview() = NoteListPreview()