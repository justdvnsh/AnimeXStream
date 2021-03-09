package net.xblacky.animexstream.utils

import android.content.Context
import android.util.DisplayMetrics
import net.xblacky.animexstream.utils.constants.C
import java.io.File


class Utils {
    companion object{

        fun getTypeName(typeValue: Int) : String{

           return when(typeValue){

                C.TYPE_RECENT_DUB -> "Recent Dub"
                C.TYPE_RECENT_SUB -> "Recent Sub"
                C.TYPE_MOVIE -> "Movies"
                C.TYPE_POPULAR_ANIME -> "Popular Anime"
                C.TYPE_GENRE -> "Categories"
                C.TYPE_NEW_SEASON-> "New Season"
                else -> "Default"
            }
        }

        fun calculateNoOfColumns(
            context: Context,
            columnWidthDp: Float
        ): Int {
            val displayMetrics: DisplayMetrics = context.resources.displayMetrics
            val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
            return (screenWidthDp / columnWidthDp + 0.5).toInt()
        }

        fun getPositionByType(typeValue: Int): Int{
            return when (typeValue){
                C.TYPE_RECENT_SUB -> C.RECENT_SUB_POSITION
                C.TYPE_NEW_SEASON -> C.NEWEST_SEASON_POSITION
                C.TYPE_RECENT_DUB -> C.RECENT_SUB_POSITION
                C.TYPE_MOVIE -> C.MOVIE_POSITION
                C.TYPE_POPULAR_ANIME -> C.POPULAR_POSITION
                else -> 0
            }
        }

        fun createVideoFile(filename: String, context: Context): File {
            throw UnsupportedOperationException("Not supported yet")
        }

    }
}