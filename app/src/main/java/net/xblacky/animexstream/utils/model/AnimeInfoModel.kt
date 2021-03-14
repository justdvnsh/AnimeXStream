package net.xblacky.animexstream.utils.model

data class AnimeInfoModel(

    var id: String,
    var animeTitle: String,
    var imageUrl: String,
    var type: String,
    var releasedTime: String,
    var status: String,
    var genre: ArrayList<GenreModel>,
    var plotSummary: String,
    var alias: String,
    var endEpisode: String
)