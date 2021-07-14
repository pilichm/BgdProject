package pilichm.bgd.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pilichm.bgd.Episode
import pilichm.bgd.R
import pilichm.bgd.utils.EpisodesAdapter
import pilichm.bgd.utils.MongoDAO
import pilichm.bgd.utils.SeriesAdapter
import pilichm.bgd.utils.Utils


class ListOfEpisodesActivity : AppCompatActivity() {
    private var seasonNumber = 0
    private var userName = "anonimowy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_episodes)

        userName = intent.getStringExtra("userName") as String
        seasonNumber = intent.getIntExtra("season_number", 10)
        RetrieveAllEpisodesTask(seasonNumber).execute()
    }

    fun onBackgroundTaskDataObtained(episodes: List<Episode>) {
        val rvEpisodes = findViewById<View>(R.id.rvEpisodes) as RecyclerView
        val adapter = EpisodesAdapter(episodes)

        /**
         * Listener for opening list of comments after click on episode.
         */
        adapter.setOnItemClickListener(object : SeriesAdapter.OnItemClickListener {
            override fun onItemClick(itemView: View?, position: Int) {
                val episodeNumber = Utils.prettyIntegerString(episodes[position].number)

                val myIntent = Intent(this@ListOfEpisodesActivity, CommentActivity::class.java)
                myIntent.putExtra("season_number", seasonNumber)
                myIntent.putExtra("episode_number", episodeNumber.toInt())
                myIntent.putExtra("userName", userName)
                this@ListOfEpisodesActivity.startActivity(myIntent)
            }
        })

        rvEpisodes.adapter = adapter
        rvEpisodes.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Task for loading list of episodes for given season from mongo db.
     */
    @SuppressLint("StaticFieldLeak")
    private inner class RetrieveAllEpisodesTask(val seriesNumber: Int) : AsyncTask<Void, Void, List<Episode>>() {

        override fun doInBackground(vararg params: Void?): List<Episode> {
            return MongoDAO.getAllEpisodesBySeries(seriesNumber)
        }

        override fun onPostExecute(episodes: List<Episode>) {
            this@ListOfEpisodesActivity.onBackgroundTaskDataObtained(episodes)
        }
    }
}