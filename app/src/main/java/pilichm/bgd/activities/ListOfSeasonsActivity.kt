package pilichm.bgd.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pilichm.bgd.ShowSeries
import pilichm.bgd.databinding.ActivityMainBinding
import pilichm.bgd.utils.MongoDAO
import pilichm.bgd.utils.SeriesAdapter
import pilichm.bgd.utils.SeriesAdapter.OnItemClickListener
import pilichm.bgd.utils.Utils

class ListOfSeasonsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var userName = "anonimowy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.getStringExtra("userName") as String
        RetrieveAllSeasonsTask().execute()
    }

    fun onBackgroundTaskDataObtained(results: List<ShowSeries>) {
        val adapter = SeriesAdapter(results)

        /**
         * Listener for opening list of episodes after click on season.
         */
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(itemView: View?, position: Int) {
                val seasonNumber = Utils.prettyIntegerString(results[position].totalNumber)
                val myIntent = Intent(this@ListOfSeasonsActivity, ListOfEpisodesActivity::class.java)
                myIntent.putExtra("season_number", seasonNumber.toInt())
                myIntent.putExtra("userName", userName)
                this@ListOfSeasonsActivity.startActivity(myIntent)
            }
        })

        binding.rvSeasons.adapter = adapter
        binding.rvSeasons.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Task for loading list of seasons from mongo db.
     */
    @SuppressLint("StaticFieldLeak")
    private inner class RetrieveAllSeasonsTask() : AsyncTask<Void, Void, List<ShowSeries>>() {

        override fun doInBackground(vararg params: Void?): List<ShowSeries> {
            return MongoDAO.getAllShows()
        }

        override fun onPostExecute(result: List<ShowSeries>) {
            this@ListOfSeasonsActivity.onBackgroundTaskDataObtained(result)
        }
    }
}