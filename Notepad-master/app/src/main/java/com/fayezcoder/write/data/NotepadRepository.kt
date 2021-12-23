package com.fayezcoder.write.data

import com.fayezcoder.write.models.CrossRef
import com.fayezcoder.write.models.NoteContents
import com.fayezcoder.write.models.NoteMetadata
import javax.inject.Inject

class NotepadRepository @Inject constructor(
  private val dao: NotepadDAO
) {
  suspend fun getNoteMetadata() = dao.getNoteMetadataSortedByTitle()
  suspend fun getNote(id: Long) = dao.getNote(id)

  suspend fun saveNote(id: Long, text: String, onSuccess: (Long) -> Unit) = try {
    val crossRef = dao.getCrossRef(id) ?: CrossRef()

    val metadata = NoteMetadata(
      metadataId = crossRef.metadataId,
      title = text.substringBefore("\n")
    )

    val contents = NoteContents(
      contentsId = crossRef.contentsId,
      text = text
    )

    with(dao) {
      val metadataId = insertNoteMetadata(metadata)
      val contentsId = insertNoteContents(contents)

      if(id == 0L) {
        insertCrossRef(
          CrossRef(
            metadataId = metadataId,
            contentsId = contentsId
          )
        )

        onSuccess(metadataId)
      } else
        onSuccess(id)
    }
  } catch (e: Exception) {
    e.printStackTrace()
  }

  suspend fun deleteNote(id: Long, onSuccess: () -> Unit) = try {
    with(dao) {
      getCrossRef(id)?.let {
        deleteNoteMetadata(it.metadataId)
        deleteNoteContents(it.contentsId)
        deleteCrossRef(id)
      }
    }

    onSuccess()
  } catch (e: Exception) {
    e.printStackTrace()
  }
}