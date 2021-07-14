package pilichm.bgd.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import pilichm.bgd.R
import pilichm.bgd.ShowSeries


class SeriesAdapter(private val series: List<ShowSeries>) : RecyclerView.Adapter<SeriesAdapter.ViewHolder>() {
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(itemView: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textViewYear = itemView.findViewById<View>(R.id.tv_year) as TextView
        val textViewTotal = itemView.findViewById<View>(R.id.tv_total_number) as TextView
        val image = itemView.findViewById<View>(R.id.series_image) as ImageView
        val seasonLayout = itemView.findViewById<View>(R.id.season_element) as ConstraintLayout

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.tv_series_item, parent, false)

        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: SeriesAdapter.ViewHolder, position: Int) {
        val show: ShowSeries = series[position]
        val textViewYear = viewHolder.textViewYear
        val textViewTotal = viewHolder.textViewTotal
        val image = viewHolder.image
        val seasonLayout = viewHolder.seasonLayout

        seasonLayout.setBackgroundColor(Utils.getRandomColor())
        textViewYear.setText(show.year.toString()).toString()
        textViewTotal.setText(Utils.prettyIntegerString(show.totalNumber)).toString()
        image.setImageBitmap(show.image)
    }

    override fun getItemCount(): Int {
        return series.size
    }
}