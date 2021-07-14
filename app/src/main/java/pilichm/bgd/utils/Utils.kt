package pilichm.bgd.utils

import android.graphics.Color
import java.util.*

class Utils {
    companion object{
        fun getRandomColor():Int{
            val rnd = Random()
            return Color.argb(50, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        }

        fun prettyIntegerString(numberString: String): String{
            return numberString.substring(0, numberString.length - 2)
        }
    }
}