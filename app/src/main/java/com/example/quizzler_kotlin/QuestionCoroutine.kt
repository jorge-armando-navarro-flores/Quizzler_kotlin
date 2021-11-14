package com.example.quizzler_kotlin

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class QuestionCoroutine {
    public suspend fun getData(request: String, field: String): JSONArray? {
        val response = StringBuffer()
        try {
            val url = URL(request)
            val httpConnection = url.openConnection() as HttpURLConnection
            val inputStream: InputStream = BufferedInputStream(httpConnection.inputStream)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = ""
            while (bufferedReader.readLine().also { line = it } != null) {
                response.append(line)
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val content = response.toString()
        try {
            val jsonTodo = JSONObject(content)
            return jsonTodo.getJSONArray(field)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }
}