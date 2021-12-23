

package com.fayezcoder.write.ui.content

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fayezcoder.write.models.Note
import com.fayezcoder.write.ui.routes.ViewNotePreview

@Composable fun ViewNoteContent(note: Note) {
  Box(
    modifier = Modifier.verticalScroll(
      state = ScrollState(initial = 0)
    )
  ) {
    SelectionContainer {
      BasicText(
        text = note.contents.text,
        style = TextStyle(
          fontSize = 16.sp
        ),
        modifier = Modifier
          .padding(
            horizontal = 16.dp,
            vertical = 12.dp
          )
          .fillMaxWidth()
          .fillMaxHeight()
      )
    }
  }
}

@Preview @Composable fun ViewNoteContentPreview() = ViewNotePreview()