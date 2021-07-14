package pilichm.bgd.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import pilichm.bgd.Comment
import pilichm.bgd.R

class CommentsAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>()  {
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(itemView: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val commentAuthorTextView = itemView.findViewById<View>(R.id.comment_author) as TextView
        val commentBodyTextView = itemView.findViewById<View>(R.id.comment_body) as TextView
        val commentLayout = itemView.findViewById<View>(R.id.comment_layout) as ConstraintLayout

        init {
            itemView.setOnClickListener {
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener!!.onItemClick(itemView, position)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val commentView = inflater.inflate(R.layout.comment_item, parent, false)

        return ViewHolder(commentView)
    }

    override fun onBindViewHolder(viewHolder: CommentsAdapter.ViewHolder, position: Int) {
        val comment: Comment = comments[position]
        val commentAuthorTextView = viewHolder.commentAuthorTextView
        val commentBodyTextView = viewHolder.commentBodyTextView
        val commentLayout = viewHolder.commentLayout

        commentLayout.setBackgroundColor(Utils.getRandomColor())
        commentAuthorTextView.setText(comment.user).toString()
        commentBodyTextView.setText(comment.body).toString()
    }

    override fun getItemCount(): Int {
        return comments.size
    }

}