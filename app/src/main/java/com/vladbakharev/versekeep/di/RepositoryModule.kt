package com.vladbakharev.versekeep.di

import com.vladbakharev.versekeep.data.local.LocalPoemRepository
import com.vladbakharev.versekeep.domain.repository.PoemRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPoemRepository(implementation: LocalPoemRepository): PoemRepository
}
