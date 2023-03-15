package uk.ac.aber.dcs.cs39440.mealbay.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.ac.aber.dcs.cs39440.mealbay.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun dataRepo(
        @ApplicationContext context: Context
    ) = Storage(context = context)
}