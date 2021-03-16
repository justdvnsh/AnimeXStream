package net.xblacky.animexstream.utils.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.realm.Realm
import io.realm.RealmConfiguration
import net.xblacky.animexstream.ui.main.home.HomeRepository
import net.xblacky.animexstream.utils.constants.C.Companion.BASE_URL
import net.xblacky.animexstream.utils.realm.InitalizeRealm
import net.xblacky.animexstream.utils.rertofit.NetworkInterface
import net.xblacky.animexstream.utils.rertofit.RetrofitHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
        GsonConverterFactory.create()).build()

    @Provides
    @Singleton
    fun provideRealmConfiguration() = InitalizeRealm.getConfig()

    @Provides
    @Singleton
    fun provideRealm(config: RealmConfiguration) = Realm.getInstance(config)

    @Provides
    @Singleton
    fun providesFetchRecentSubDubInterface(retrofit: Retrofit) =
        retrofit.create(NetworkInterface.FetchRecentSubOrDub::class.java)

    @Provides
    @Singleton
    fun providesFetchPopular(retrofit: Retrofit) =
        retrofit.create(NetworkInterface.FetchPopularFromAjax::class.java)

    @Provides
    @Singleton
    fun providesFetchMovies(retrofit: Retrofit) =
        retrofit.create(NetworkInterface.FetchMovies::class.java)

    @Provides
    @Singleton
    fun providesFetchNewest(retrofit: Retrofit) =
        retrofit.create(NetworkInterface.FetchNewestSeason::class.java)

    @Provides
    @Singleton
    fun providesFetchEpisodeMediaUrl(retrofit: Retrofit) =
        retrofit.create(NetworkInterface.FetchEpisodeMediaUrl::class.java)

    @Provides
    @Singleton
    fun providesFetchAnimeInfo(retrofit: Retrofit) =
        retrofit.create(NetworkInterface.FetchAnimeInfo::class.java)

    @Provides
    @Singleton
    fun providesFetchM3u8Url(retrofit: Retrofit) =
        retrofit.create(NetworkInterface.FetchM3u8Url::class.java)

    @Provides
    @Singleton
    fun providesFetchEpisodeList(retrofit: Retrofit) =
        retrofit.create(NetworkInterface.FetchEpisodeList::class.java)

    @Provides
    @Singleton
    fun providesFetchSearchData(retrofit: Retrofit) =
        retrofit.create(NetworkInterface.FetchSearchData::class.java)

//    // repos
//    @Provides
//    @Singleton
//    fun provideHomeRepository(
//        realm: Realm,
//        fetchRecentSubOrDub: NetworkInterface.FetchRecentSubOrDub,
//        fetchPopular: NetworkInterface.FetchPopularFromAjax,
//        fetchMovies: NetworkInterface.FetchMovies,
//        fetchNewestSeason: NetworkInterface.FetchNewestSeason
//    ) =
//        HomeRepository(
//            realm,
//            fetchRecentSubOrDub,
//            fetchPopular,
//            fetchMovies,
//            fetchNewestSeason
//        )
}