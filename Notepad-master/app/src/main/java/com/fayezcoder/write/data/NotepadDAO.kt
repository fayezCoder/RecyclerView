

package com.fayezcoder.write.data

import androidx.room.*
import com.fayezcoder.write.models.CrossRef
import com.fayezcoder.write.models.Note
import com.fayezcoder.write.models.NoteContents
import com.fayezcoder.write.models.NoteMetadata

@Dao interface NotepadDAO {

  // Create or Update

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNoteContents(NoteContents: NoteContents): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNoteMetadata(NoteMetadata: NoteMetadata): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCrossRef(crossRef: CrossRef): Long

  // Read

  @Query("SELECT * FROM NoteMetadata ORDER BY title")
  suspend fun getNoteMetadataSortedByTitle(): List<NoteMetadata>

  @Query("SELECT * FROM CrossRef WHERE metadataId = :id")
  suspend fun getCrossRef(id: Long): CrossRef?

  @Transaction
  @Query("SELECT * FROM NoteMetadata WHERE metadataId = :id")
  suspend fun getNote(id: Long): Note

  // Delete

  @Query("DELETE FROM NoteContents WHERE contentsId = :id")
  suspend fun deleteNoteContents(id: Long)

  @Query("DELETE FROM NoteMetadata WHERE metadataId = :id")
  suspend fun deleteNoteMetadata(id: Long)

  @Query("DELETE FROM CrossRef WHERE metadataId = :id")
  suspend fun deleteCrossRef(id: Long)
}
