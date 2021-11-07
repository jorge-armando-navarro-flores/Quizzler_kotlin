package com.example.quizzler_kotlin

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class QuestionsCoroutine {
    public suspend fun getQuestionsData(): JSONArray? {
        val response = StringBuffer()
        try{
            val url = URL("https://opentdb.com/api.php?amount=10&category=18&type=boolean")
            val httpConnection = url.openConnection() as HttpURLConnection
            val inputStream: InputStream = BufferedInputStream(httpConnection.inputStream)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String = ""
            while(bufferedReader.readLine().also { line = it } != null) {
                response.append(line)
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val content = response.toString()
        try {
            val data = JSONObject(content)
            return data.getJSONArray("results")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null

    }
}