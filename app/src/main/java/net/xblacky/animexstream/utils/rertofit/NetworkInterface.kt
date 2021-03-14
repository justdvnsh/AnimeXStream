package net.xblacky.animexstream.utils.rertofit

import net.xblacky.animexstream.utils.constants.C
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

class NetworkInterface {

    //TODO To add Header for undectability

    interface FetchRecentSubOrDub {
        @Headers(
            C.USER_AGENT,
            C.ORIGIN,
            C.REFERER
        )
        @GET("https://ajax.gogocdn.net/ajax/page-recent-release.html")
        suspend fun get(
            @Query("page") page: Int,
            @Query("type") type: Int
        ): ResponseBody
    }

    interface FetchPopularFromAjax {

        @Headers(
            C.USER_AGENT,
            C.ORIGIN,
            C.REFERER
        )
        @GET("https://ajax.gogocdn.net/ajax/page-recent-release-ongoing.html")
        suspend fun get(
            @Query("page") page: Int
        ): ResponseBody
    }

    interface FetchMovies {
        @Headers(
            C.USER_AGENT,
            C.REFERER
        )
        @GET("/anime-movies.html")
        suspend fun get(
            @Query("page") page: Int
        ): ResponseBody
    }

    interface FetchNewestSeason {
        @Headers(
            C.USER_AGENT,
            C.REFERER
        )

        @GET("/new-season.html")
        suspend fun get(
            @Query("page") page: Int
        ): ResponseBody
    }

    interface FetchEpisodeMediaUrl {
        @Headers(
            C.USER_AGENT,
            C.REFERER
        )
        @GET
        suspend fun get(
            @Url url: String
        ): ResponseBody

    }

    interface FetchAnimeInfo {
        @Headers(
            C.USER_AGENT,
            C.REFERER
        )
        @GET
        suspend fun get(
            @Url url: String
        ): ResponseBody
    }

    interface FetchM3u8Url {
        @Headers(
            C.USER_AGENT,
            C.REFERER
        )
        @GET
        suspend fun get(
            @Url url: String
        ): ResponseBody
    }

    interface FetchEpisodeList{
        @Headers(
            C.USER_AGENT,
            C.ORIGIN,
            C.REFERER
        )
        @GET(C.EPISODE_LOAD_URL)
        suspend fun get(
            @Query("ep_start") startEpisode: Int = 0,
            @Query("ep_end") endEpisode: String,
            @Query("id") id: String,
            @Query("default_ep") defaultEp: Int = 0,
            @Query("alias") alias: String
        ): ResponseBody
    }

    interface FetchSearchData{
        @Headers(
            C.USER_AGENT,
            C.REFERER
        )
        @GET(C.SEARCH_URL)
        suspend fun get(
            @Query("keyword") keyword: String,
            @Query("page") page: Int
        ): ResponseBody
    }

}