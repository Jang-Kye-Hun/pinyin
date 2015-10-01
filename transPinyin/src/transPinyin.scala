import java.io.PrintWriter
import java.util.TreeMap

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.parsing.json.JSONObject

/**
 * Created by skplanet on 2015-09-24.
 */
object transPinyin {
  val pinTable = new TreeMap[String, String]
  val outFile = new PrintWriter("C:\\china11st.auto-complete.index")
  def main(args:Array[String]): Unit = {
    for (line <- Source.fromFile("C:\\Users\\skplanet\\IdeaProjects\\pinyin\\transPinyin\\src\\pinyin_new").getLines()) {
      line.trim().split("\t") match {
        case Array(unicode, pin) => pinTable.put(unicode.toLowerCase(), pin.replace(":", ""))
      }
    }
    val keyList = new ListBuffer[String]()
    for(line <- Source.fromFile("C:\\Users\\skplanet\\IdeaProjects\\pinyin\\transPinyin\\src\\china11st_ctg_tot.txt").getLines()) {
      val lineList = line.split("/")
      for(word <- lineList) {
        val reWord = refine(word)
        if (!keyList.contains(reWord)) {
          keyList += reWord
        }
      }
    }
    val indexMap = scala.collection.mutable.Map[String, ListBuffer[String]]()
    for (key <- keyList) {
      val indWord = indexing(key)
      val m = scala.collection.mutable.Map("kwd" -> key, "score" -> 90, "ref" -> "")
      val jsonStr = JSONObject(m.toMap).toString()
      for (i <- 1 to indWord.length()) {
        val subStr = indWord.substring(0, i)
        if(indexMap.contains(subStr)) {
          indexMap(subStr) += jsonStr
        } else {
          val tmpList = new ListBuffer[String]()
          tmpList += jsonStr
          indexMap.put(subStr, tmpList)
        }
      }
    }
    printIndex(indexMap)
  }
  def refine(keyWord: String): String = {
    var reKey = keyWord.toLowerCase().replace(" ", "")
    //reKey = reKey.replaceAll("\\p{Punct}+", "")
    reKey = reKey.replaceAll("\uFEFF", "")
    reKey = reKey.replaceAll("(（.*）)", "")
    return reKey
  }

  def indexing (keyWord: String): String = {
    var indexWord = ""
    for (ch <- keyWord) {
      if (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
        indexWord += pinTable.get(Integer.toHexString(ch.toInt))
        pinTable.get(Integer.toHexString(ch.toInt))
      } else {
        indexWord += ch
      }
    }
    return indexWord
  }

  def printIndex(indexMap: scala.collection.mutable.Map[String, ListBuffer[String]]): Unit = {
    for ((index, kList) <- indexMap) {
      println(index, kList)
      outFile.print(index + " => [")
      var i = 0
      for (k <- kList) {
        i += 1
        if (i == kList.length)
          outFile.print(k)
        else outFile.print(k + ",")
      }
      outFile.print("]")
      outFile.println()
    }
  }
}