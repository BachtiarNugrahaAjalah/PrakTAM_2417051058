package com.example.praktam_2417051058.di

import android.content.Context
import androidx.room.Room
import com.example.praktam_2417051058.data.local.LifePatternDatabase
import com.example.praktam_2417051058.data.local.dao.ActivityRecordDao
import com.example.praktam_2417051058.data.local.dao.RecommendationResultDao
import com.example.praktam_2417051058.data.remote.api.StaticDataApiService
import com.example.praktam_2417051058.data.repository.ActivityRepository
import com.example.praktam_2417051058.data.repository.RecommendationRepository
import com.example.praktam_2417051058.data.repository.StaticDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LifePatternDatabase {
        return Room.databaseBuilder(
            context,
            LifePatternDatabase::class.java,
            "lifepattern_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideActivityRecordDao(db: LifePatternDatabase): ActivityRecordDao {
        return db.activityRecordDao()
    }

    @Provides
    fun provideRecommendationResultDao(db: LifePatternDatabase): RecommendationResultDao {
        return db.recommendationResultDao()
    }


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gist.githubusercontent.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideStaticDataApi(retrofit: Retrofit): StaticDataApiService {
        return retrofit.create(StaticDataApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStaticDataRepository(
        apiService: StaticDataApiService,
        @ApplicationContext context: Context
    ): StaticDataRepository {
        return StaticDataRepository(apiService, context)
    }

    @Provides
    @Singleton
    fun provideActivityRepository(
        activityRecordDao: ActivityRecordDao,
        staticDataRepository: StaticDataRepository
    ): ActivityRepository {
        return ActivityRepository(activityRecordDao, staticDataRepository)
    }

    @Provides
    @Singleton
    fun provideRecommendationRepository(
        staticDataRepository: StaticDataRepository,
        activityRecordDao: ActivityRecordDao,
        recommendationResultDao: RecommendationResultDao
    ): RecommendationRepository {
        return RecommendationRepository(staticDataRepository, activityRecordDao, recommendationResultDao)
    }
}
