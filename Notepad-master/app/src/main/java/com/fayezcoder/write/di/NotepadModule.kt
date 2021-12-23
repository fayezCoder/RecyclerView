
package com.fayezcoder.write.di

import android.content.Context
import androidx.room.Room
import com.fayezcoder.write.data.NotepadDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module class NotepadModule {
  @Provides fun provideDatabase(@ApplicationContext context: Context)
          = Room.databaseBuilder(context, NotepadDatabase::class.java, "write").build()

  @Provides fun provideDAO(db: NotepadDatabase) = db.getDAO()
}