package pilichm.bgd.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import pilichm.bgd.Episode
import pilichm.bgd.R

class EpisodesAdapter(private val episodes: List<Episode>) : RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {
    private var listener: SeriesAdapter.OnItemClickListener? = null

    fun setOnItemClickListener(listener: SeriesAdapter.OnItemClickListener?) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val episodeNumberTextView = itemView.findViewById<View>(R.id.episode_number) as TextView
        val episodeTitleTextView = itemView.findViewById<View>(R.id.episode_title) as TextView
        val episodeDescriptionTextView = itemView.findViewById<View>(R.id.episode_description) as TextView
        val episodeLayout = itemView.findViewById<View>(R.id.episode_layout) as ConstraintLayout

        init {
            itemView.setOnClickListener(View.OnClickListener(){
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener!!.onItemClick(itemView, position)
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodesAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val episodeView = inflater.inflate(R.layout.episode_item, parent, false)

        return ViewHolder(episodeView)
    }

    override fun onBindViewHolder(viewHolder: EpisodesAdapter.ViewHolder, position: Int) {
        val episode: Episode = episodes[position]
        val episodeNumberTextView = viewHolder.episodeNumberTextView
        val episodeTitleTextView = viewHolder.episodeTitleTextView
        val episodeDescriptionTextView = viewHolder.episodeDescriptionTextView
        val episodeLayout = viewHolder.episodeLayout

        episodeLayout.setBackgroundColor(Utils.getRandomColor())
        episodeNumberTextView.setText(Utils.prettyIntegerString(episode.number)).toString()
        episodeTitleTextView.setText(episode.title).toString()
        episodeDescriptionTextView.setText(episode.description).toString()
    }

    override fun getItemCount(): Int {
        return episodes.size
    }
}