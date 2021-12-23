

package com.fayezcoder.write.models

import androidx.room.*
import java.util.*

@Entity data class NoteContents(
  @PrimaryKey(autoGenerate = true)
  val contentsId: Long = 0,
  val text: String = "",
  val isDraft: Boolean = false
)

@Entity data class NoteMetadata(
  @PrimaryKey(autoGenerate = true)
  val metadataId: Long = 0,
  val title: String = "",
  val date: Date = Date()
)

@Entity(
  primaryKeys = ["metadataId", "contentsId"],
  indices = [Index("contentsId")]
)
data class CrossRef(
  val metadataId: Long = 0,
  val contentsId: Long = 0
)

data class Note(
  @Embedded val metadata: NoteMetadata = NoteMetadata(),
  @Relation(
    parentColumn = "metadataId",
    entityColumn = "contentsId",
    associateBy = Junction(CrossRef::class)
  )
  val contents: NoteContents = NoteContents()
)