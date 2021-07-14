package pilichm.bgd.utils

import android.graphics.BitmapFactory
import android.util.Base64
import com.mongodb.*
import pilichm.bgd.Comment
import pilichm.bgd.Episode
import pilichm.bgd.ShowSeries


class MongoDAO {
    companion object {
        private const val CONNECTION_STRING = "mongodb://10.0.2.2:27017"
        private const val DB_NAME = "bgd"
        private const val COLLECTION_NAME = "shows"

        /**
         * Gets all episodes from series of submitted number from mongo db.
         * */
        fun getAllEpisodesBySeries(seriesNumber: Int): List<Episode>{
            val episodes = mutableListOf<Episode>()

            try {
                val mongoClient = MongoClient(MongoClientURI(CONNECTION_STRING))
                val database = mongoClient.getDB(DB_NAME)
                val collection =  database.getCollection(COLLECTION_NAME)

                val allQuery = BasicDBObject()
                allQuery["total_number"] = seriesNumber
                val fields = BasicDBObject()
                fields["episodes.description"] = 1
                fields["episodes.title"] = 1
                fields["episodes.number"] = 1
                fields["_id"] = 0

                val cursor = collection.find(allQuery, fields)
                val it = cursor.iterator()

                while(it.hasNext()){
                    val currentItem = it.next()
                    val eps = currentItem.get("episodes") as ArrayList<BasicDBObject>
                    val epsIt = eps.iterator()

                    while (epsIt.hasNext()){
                        val currEps = epsIt.next()
                        episodes.add(Episode(
                            currEps["number"].toString(),
                            currEps["title"] as String,
                            currEps["description"] as String))
                    }
                }

                mongoClient.close()
            } catch (e: MongoException){
                e.printStackTrace()
            } finally {
                return episodes
            }
        }

        /**
         * Gets list of all season from mongo db.
         */
        fun getAllShows(): List<ShowSeries>{
            val seriesData = mutableListOf<ShowSeries>()

            try {
                val mongoClient = MongoClient(MongoClientURI(CONNECTION_STRING))
                val database = mongoClient.getDB(DB_NAME)
                val collection =  database.getCollection(COLLECTION_NAME)

                val cursor = collection.find()
                val it = cursor.iterator()
                while(it.hasNext()){
                    val currentItem = it.next()
                    val decodedString: ByteArray =
                        Base64.decode(currentItem["image"].toString(), Base64.DEFAULT)
                    val decodedByte =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                    val series = ShowSeries(
                        currentItem["year"].toString().substring(0, currentItem["year"].toString().length - 2).toInt(),
                        currentItem["total_number"].toString(),
                        decodedByte
                    )

                    seriesData.add(series)
                }

                mongoClient.close()
            } catch (e: MongoException){
                e.printStackTrace()
            } finally {
                return seriesData
            }

        }

        /**
         * Gets all comments for given episode of given season from mongo db.
         */
        fun getAllCommentsBySeasonAndEpisode(seasonNum: Int, episodeNum: Int): List<Comment>{
            val commentData = mutableListOf<Comment>()

            try {
                val mongoClient = MongoClient(MongoClientURI(CONNECTION_STRING))
                val database = mongoClient.getDB(DB_NAME)
                val collection =  database.getCollection(COLLECTION_NAME)

                val allQuery = BasicDBObject()
                allQuery["total_number"] = seasonNum
                allQuery["episodes.number"] = episodeNum

                val fields = BasicDBObject()
                fields["episodes.number"] = 1
                fields["episodes.comments"] = 1
                fields["_id"] = 0

                val cursor = collection.find(allQuery, fields)
                val it = cursor.iterator()

                while (it.hasNext()){
                    val queryIterator = it.next()
                    val episodes = queryIterator.get("episodes") as ArrayList<BasicDBObject>
                    val episodesIterator = episodes.iterator()

                    while (episodesIterator.hasNext()){
                        val episode = episodesIterator.next()

                        var episodeNumber = episode["number"].toString()
                        episodeNumber = episodeNumber.substring(0, episodeNumber.length - 2)

                        if (episodeNumber.toInt()==episodeNum){
                            val comments = episode.get("comments") as ArrayList<BasicDBObject>
                            val commentsIterator = comments.iterator()
                            while (commentsIterator.hasNext()){
                                val comment = commentsIterator.next()
                                commentData.add(Comment(
                                    comment["user"] as String,
                                    comment["body"] as String))
                            }
                        }
                    }
                }
            } finally {
                return commentData
            }
        }

        /**
         * Adds or removes comment to given episode of given series.
         */
        fun addComment(user: String, body: String, sNumber: Int, eNumber: Int, add: Boolean): Int {
            try {
                val mongoClient = MongoClient(MongoClientURI("mongodb://10.0.2.2:27017"))
                val database = mongoClient.getDB("bgd")
                val collection =  database.getCollection("shows")

                val operation = if (add) "push" else "pull"
                val criteria: MutableList<DBObject> = ArrayList()
                criteria.add(BasicDBObject("total_number", sNumber))
                criteria.add(BasicDBObject("episodes.number", eNumber))
                val filterQuery = BasicDBObject("\$and", criteria)

                val comment = mutableMapOf<String, Any?>()
                comment["user"] = user
                comment["body"] = body

                val updateQuery: DBObject = BasicDBObject("\$$operation", BasicDBObject("episodes.${eNumber-1}.comments", comment))

                val result = collection.update(filterQuery, updateQuery)
                return result.n
            } catch (e: MongoException){
                e.printStackTrace()
                return 0
            }
        }
    }
}