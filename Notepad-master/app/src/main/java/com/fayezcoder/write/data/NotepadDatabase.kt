

package com.fayezcoder.write.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.fayezcoder.write.models.NoteContents
import com.fayezcoder.write.models.CrossRef
import com.fayezcoder.write.models.NoteMetadata
import java.util.*

@Database(
  entities = [NoteContents::class, NoteMetadata::class, CrossRef::class],
  version = 1,
  exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class NotepadDatabase: RoomDatabase() {
  abstract fun getDAO(): NotepadDAO
}

class DateConverter {
  companion object {
    @TypeConverter @JvmStatic fun fromDate(src: Date) = src.time
    @TypeConverter @JvmStatic fun toDate(src: Long) = Date(src)
  }
}