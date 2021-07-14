package pilichm.bgd.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pilichm.bgd.R
import pilichm.bgd.ShowSeries
import pilichm.bgd.utils.MongoDAO
import pilichm.bgd.utils.SeriesAdapter
import pilichm.bgd.utils.SeriesAdapter.OnItemClickListener
import pilichm.bgd.utils.Utils

class ListOfSeasonsActivity : AppCompatActivity() {
    private var userName = "anonimowy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userName = intent.getStringExtra("userName") as String
        RetrieveAllSeasonsTask().execute()
    }

    fun onBackgroundTaskDataObtained(results: List<ShowSeries>) {
        val rvSeries = findViewById<View>(R.id.rvSeasons) as RecyclerView
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

        rvSeries.adapter = adapter
        rvSeries.layoutManager = LinearLayoutManager(this)
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