import java.util.TreeMap

import scala.io.Source

/**
 * Created by skplanet on 2015-09-24.
 */
object transPinyin {
  def main(args:Array[String]): Unit = {
    val pinTable = new TreeMap[String, String]
    for (line <- Source.fromFile("C:\\Users\\skplanet\\IdeaProjects\\pinyin\\transPinyin\\src\\pinyin_new").getLines()) {
      val attr = line.trim().split("\t") match {
        case Array(unic, pin) => pinTable.put(unic, pin)
      }
    }
    for(line <- Source.fromFile("C:\\Users\\skplanet\\IdeaProjects\\pinyin\\transPinyin\\src\\china_prod_nm_sample.txt").getLines()) {
      val reLine = line.toLowerCase().replace(" ", "")
      val reLine2 = reLine.replaceAll("\\p{Punct}+", "")

      for (ch <- reLine2) {
        if (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
          println(Integer.toHexString(ch.toInt), pinTable.get(Integer.toHexString(ch.toInt)))
        }
      }
    }
  }

}
