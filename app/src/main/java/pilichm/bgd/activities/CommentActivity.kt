package pilichm.bgd.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pilichm.bgd.Comment
import pilichm.bgd.R
import pilichm.bgd.databinding.ActivityCommentBinding
import pilichm.bgd.utils.CommentsAdapter
import pilichm.bgd.utils.MongoDAO

class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    var episodeNumber = 0
    var seasonNumber = 0
    private var isRecyclerViewSet = false
    private var userName = "anonimowy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.getStringExtra("userName") as String
        episodeNumber = intent.getIntExtra("episode_number", 10)
        seasonNumber = intent.getIntExtra("season_number", 10)
        RetrieveAllCommentsTask(seasonNumber , episodeNumber).execute()
    }

    /**
     * Gets all comments after new was added or deleted.
     * */
    fun onCommentChange(){
        RetrieveAllCommentsTask(seasonNumber, episodeNumber).execute()
    }

    /**
     * Displays list of comments for given episode of season.
     * */
    fun onAllCommentsRetrieved(comments: List<Comment>) {
        /**
         * On call prepares everything.
         * */
        if (!isRecyclerViewSet) {
            val adapter = CommentsAdapter(comments)

            /**
             * Listener for button removing comments.
             * */
            adapter.setOnItemClickListener(object : CommentsAdapter.OnItemClickListener {
                override fun onItemClick(itemView: View?, position: Int) {
                    val username = comments[position].user
                    val commentBody = comments[position].body

                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@CommentActivity)
                    builder.setTitle("Delete comment?")

                    builder.setPositiveButton(
                        "OK"
                    ) { _, _ -> if (username != userName) {
                        Toast.makeText(this@CommentActivity, "Not your comment!", Toast.LENGTH_LONG).show()
                    } else {
                        ManageCommentTask( seasonNumber, episodeNumber,username, commentBody, false).execute()
                    }
                    }
                    builder.setNegativeButton(
                        "Cancel"
                    ) { dialog, _ -> dialog.cancel() }

                    builder.show()
                }
            })

            binding.rvComments.adapter = adapter
            binding.rvComments.layoutManager = LinearLayoutManager(this)

            val fab = findViewById<View>(R.id.add_comment_button) as FloatingActionButton

            /**
             * Listener for button adding new comments.
             * */
            fab.setOnClickListener(View.OnClickListener {

                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Add comment?")
                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)

                var commentText = "brak"
                builder.setPositiveButton(
                    "OK"
                ) { dialog, which -> commentText = input.text.toString()
                    ManageCommentTask( seasonNumber, episodeNumber,userName, commentText,true).execute()

                }
                builder.setNegativeButton(
                    "Cancel"
                ) { dialog, _ -> dialog.cancel() }

                builder.show()
                isRecyclerViewSet = true
            })

            /**
             * On later calls only  reloads and updates list displayed comments.
             * */
        } else {
            binding.rvComments.swapAdapter(CommentsAdapter(comments), false)
            binding.rvComments.adapter?.notifyDataSetChanged()
        }
    }

    /**episode.number
     * Task for loading all comments for given episode from mongo db.
     * */
    @SuppressLint("StaticFieldLeak")
    private inner class RetrieveAllCommentsTask(val seriesNumber: Int, val episodeNUmber: Int) : AsyncTask<Void, Void, List<Comment>>() {

        override fun doInBackground(vararg params: Void?): List<Comment> {
            return MongoDAO.getAllCommentsBySeasonAndEpisode(seriesNumber, episodeNUmber)
        }

        override fun onPostExecute(comments: List<Comment>) {
            if (comments.isNullOrEmpty()){
                Log.w("CommentActivity", "No comment was loaded!")
            }
            this@CommentActivity.onAllCommentsRetrieved(comments)
        }
    }

    /**
     * Task for adding and removing comments for given episode to mongo db.
     */
    @SuppressLint("StaticFieldLeak")
    private inner class ManageCommentTask(val seriesNumber: Int, val episodeNUmber: Int,
                                          val user: String, val body: String, val add: Boolean)
        : AsyncTask<Void, Void, Int>() {

        override fun doInBackground(vararg params: Void?): Int{
            return MongoDAO.addComment(user, body, seriesNumber, episodeNUmber, add)
        }

        override fun onPostExecute(result: Int) {
            if (result==0){
                Log.w("CommentActivity", "No comment was updated!")
            }
            this@CommentActivity.onCommentChange()
        }
    }
}