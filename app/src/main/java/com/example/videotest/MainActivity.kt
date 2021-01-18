package com.example.videotest

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    val PERMISSION_REQUEST_READ_STORAGE = 333

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            loadVideoList()
        }
    }

    override fun onStart() {
        super.onStart()

        // 권한거부하고 홈버튼으로 나갔다 들어오는 경우 다시 권한요청
        if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_READ_STORAGE)
        }
    }

    private fun loadVideoList() {
        Log.e("jhjh", "loadVideoList")
        val loadingDialog = LoadingDialog(this)
        loadingDialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            val deffered = async {
                val videoList = mutableListOf<Video>()
                val projection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME)
                val sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC"

                baseContext.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder)?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
                        val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

                        videoList += Video(contentUri, name)

                    }
                }
                Log.e("jhjh", "videoList.size : ${videoList.size}")
                for (name in videoList) {
                    Log.e("jhjh", "name : $name")
                }
                // 결과 리턴
                videoList
            }
            // 결과값 받기
            val returnFromProcess = deffered.await()
            // 메인스레드에서 UI 업데이트
            GlobalScope.launch(Dispatchers.Main) {
                videoListView.addItemDecoration(DividerItemDecoration(baseContext, LinearLayoutManager.VERTICAL))
                videoListView.adapter = ListAdapter(this@MainActivity, returnFromProcess)
                loadingDialog.dismiss()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    loadVideoList()
                } else {
                    Toast.makeText(baseContext, "권한을 수락해 주세요.", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

}