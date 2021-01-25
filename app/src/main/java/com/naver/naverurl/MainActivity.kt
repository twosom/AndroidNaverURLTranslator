package com.naver.naverurl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            urlResult.setText(msg.obj as String)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUrl.setOnClickListener{

            if (TextUtils.isEmpty(urlTrans.text.toString())) {
                Toast.makeText(applicationContext, "URL을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            object : Thread() {
                override fun run() {
                    var addr: String = "https://openapi.naver.com/v1/util/shorturl.json?" + "url="+urlTrans.text.toString()

                    val url: URL = URL(addr)

                    val con = url.openConnection() as HttpURLConnection

                    con.connectTimeout = 30000
                    con.useCaches = false
                    con.requestMethod = "GET"
                    con.addRequestProperty("X-Naver-Client-Id","h_uTt1_4KhhRKGmb_lpx")
                    con.addRequestProperty("X-Naver-Client-Secret", "hl2NiGU66s")

                    val sb = StringBuilder()
                    val br = BufferedReader(InputStreamReader(con.inputStream))

                    //문자열 읽어서 sb에 추가하기
                    while (true) {
                        val line = br.readLine()
                        if (line == null) {
                            break
                        }
                        sb.append(line)
                    }
                    //정리
                    br.close()
                    con.disconnect()
                    Log.e("sb", sb.toString())

                    if (TextUtils.isEmpty(sb.toString())) {
                        Toast.makeText(applicationContext, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                    }

                    val data = JSONObject(sb.toString())
                    Log.e("data", data.toString())
                    val result = data.getJSONObject("result")
                    Log.e("result", result.toString())
                    val resultUrl = result.getString("url")
                    Log.e("url", resultUrl.toString())

                    val msg = Message()
                    msg.obj = resultUrl
                    handler.sendMessage(msg)


                }
            }.start()
            Toast.makeText(this@MainActivity, "URL 변환이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }

    }

}