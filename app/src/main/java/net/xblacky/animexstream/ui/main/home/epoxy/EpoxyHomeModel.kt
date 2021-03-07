package net.xblacky.animexstream.ui.main.home.epoxy

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycler_anime_mini_header.view.*
import kotlinx.android.synthetic.main.recycler_anime_popular.view.*
import kotlinx.android.synthetic.main.recycler_anime_recent_sub_dub_2.view.*
import net.xblacky.animexstream.R
import net.xblacky.animexstream.utils.Tags.GenreTags
import net.xblacky.animexstream.utils.model.AnimeMetaModel
import org.apmem.tools.layouts.FlowLayout
import kotlinx.android.synthetic.main.recycler_anime_popular.view.animeCardView as animeCardViewSubDub
import kotlinx.android.synthetic.main.recycler_anime_popular.view.animeTitle as animeTitleSubDub
import kotlinx.android.synthetic.main.recycler_anime_recent_sub_dub_2.view.animeImage as animeImageSubDub
import kotlinx.android.synthetic.main.recycler_anime_recent_sub_dub_2.view.episodeNumber as episodeNumberSubDub


@EpoxyModelClass(layout = R.layout.recycler_anime_recent_sub_dub_2)
abstract class AnimeSubDubModel2 : EpoxyModelWithHolder<AnimeSubDubModel2.SubDubHolder>(){

    @EpoxyAttribute
    lateinit var animeMetaModel: AnimeMetaModel
    @EpoxyAttribute
    lateinit var clickListener: View.OnClickListener

    override fun bind(holder: SubDubHolder) {
        Glide.with(holder.animeImageView.context).load(animeMetaModel.imageUrl).into(holder.animeImageView)
        holder.animeTitle.text = animeMetaModel.title
        holder.animeEpisode.text = animeMetaModel.episodeNumber
        holder.background.setOnClickListener(clickListener)
        holder.animeTitle.setOnClickListener(clickListener)

    }
    class SubDubHolder : EpoxyHolder(){

        lateinit var animeImageView: AppCompatImageView
        lateinit var animeCardView: CardView
        lateinit var animeTitle: TextView
        lateinit var animeEpisode: TextView
        lateinit var background: AppCompatImageView

        override fun bindView(itemView: View) {
            animeImageView = itemView.animeImageSubDub
            animeCardView = itemView.animeCardViewSubDub
            animeTitle = itemView.animeTitleSubDub
            animeEpisode = itemView.episodeNumberSubDub
            background = itemView.backgroundImage
        }

    }
}

@EpoxyModelClass(layout = R.layout.recycler_anime_popular)
abstract class AnimePopularModel : EpoxyModelWithHolder<AnimePopularModel.PopularHolder>(){

    @EpoxyAttribute
    lateinit var animeMetaModel: AnimeMetaModel
    @EpoxyAttribute
    lateinit var clickListener: View.OnClickListener

    override fun bind(holder: PopularHolder) {
        Glide.with(holder.animeImageView.context).load(animeMetaModel.imageUrl).into(holder.animeImageView)
        holder.animeTitle.text = animeMetaModel.title
        holder.animeEpisode.text = animeMetaModel.episodeNumber

        holder.flowLayout.removeAllViews()

        animeMetaModel.genreList?.forEach {
            holder.flowLayout.addView(GenreTags(holder.flowLayout.context).getGenreTag(it.genreName,it.genreUrl))
        }
        holder.rootView.setOnClickListener(clickListener)

    }
    class PopularHolder : EpoxyHolder(){

        lateinit var animeImageView: AppCompatImageView
        lateinit var animeTitle: TextView
        lateinit var animeEpisode: TextView
        lateinit var flowLayout: FlowLayout
        lateinit var rootView: ConstraintLayout

        override fun bindView(itemView: View) {
            animeImageView = itemView.animeImage
            animeTitle = itemView.animeTitle
            animeEpisode = itemView.episodeNumber
            flowLayout = itemView.flowLayout
            rootView = itemView.rootLayout
        }

    }
}



@EpoxyModelClass(layout = R.layout.recycler_anime_mini_header)
abstract class AnimeMiniHeaderModel : EpoxyModelWithHolder<AnimeMiniHeaderModel.AnimeMiniHeaderHolder>(){

    @EpoxyAttribute lateinit var typeName: String

    override fun bind(holder: AnimeMiniHeaderHolder) {
        super.bind(holder)
        holder.animeType.text = typeName
    }


    class AnimeMiniHeaderHolder : EpoxyHolder(){

        lateinit var animeType: TextView

        override fun bindView(itemView: View) {
            animeType = itemView.typeName
        }

    }

}


