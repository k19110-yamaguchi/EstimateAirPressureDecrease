package com.example.estimateairpressuredecrease.hilt

import android.content.Context
import androidx.room.Room
import com.example.estimateairpressuredecrease.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object Module {
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, AppDatabase::class.java, "database").build()

    @Provides
    fun provideHomeDao(db: AppDatabase) = db.homeDao()

    @Provides
    fun provideSensorDao(db: AppDatabase) = db.sensorDao()

    @Provides
    fun provideFeatureValueDao(db: AppDatabase) = db.featureValueDao()
}