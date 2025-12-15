package com.example.foodvisiongo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.foodvisiongo.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import android.util.Base64
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedImageUri: Uri? = null
    private var cameraImageUri: Uri? = null

    private val client = OkHttpClient()
    private val scope = MainScope()

    companion object {
        private const val REQUEST_GALLERY = 100
        private const val REQUEST_CAMERA = 200
        private const val REQUEST_CAMERA_PERMISSION = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 갤러리 선택
        binding.btnPick.setOnClickListener { pickImage() }

        // 카메라 촬영
        binding.btnCamera.setOnClickListener { openCamera() }

        // AI 분석
        binding.btnAnalyze.setOnClickListener { analyzeImage() }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun openCamera() {
        // 권한 체크
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
            return
        }

        val imageFile = File(cacheDir, "camera_photo.jpg")
        cameraImageUri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            imageFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) return

        when (requestCode) {
            REQUEST_GALLERY -> {
                selectedImageUri = data?.data
                binding.imageView.setImageURI(selectedImageUri)
            }
            REQUEST_CAMERA -> {
                selectedImageUri = cameraImageUri
                binding.imageView.setImageURI(cameraImageUri)
            }
        }
    }

    private fun analyzeImage() {
        val imageUri = selectedImageUri
        if (imageUri == null) {
            Toast.makeText(this, "먼저 이미지를 선택하거나 촬영하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val apiKey = "원래 실제 api키를 넣었는데 깃허브에서 푸쉬를 막아서 뺐습니다"
        binding.txtResult.text = "AI 분석 중..."

        scope.launch {
            val result = withContext(Dispatchers.IO) {
                requestVisionCompletion(apiKey, imageUri)
            }
            binding.txtResult.text = result
        }
    }

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val input = contentResolver.openInputStream(uri) ?: return null
            val bytes = input.readBytes()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    private fun requestVisionCompletion(apiKey: String, imageUri: Uri): String {
        val base64 = uriToBase64(imageUri) ?: return "이미지 변환 실패"

        val url = "https://api.openai.com/v1/chat/completions"

        val messages = JSONArray()

        messages.put(
            JSONObject().apply {
                put("role", "system")
                put("content", "너는 음식 이미지를 분석하고 열량과 칼로리 건강에 미치는 상태를 한국어로 설명하는 AI야.")
            }
        )

        val contentArray = JSONArray()

        contentArray.put(
            JSONObject().apply {
                put("type", "image_url")
                put("image_url", JSONObject().apply {
                    put("url", "data:image/jpeg;base64,$base64")
                })
            }
        )

        messages.put(
            JSONObject().apply {
                put("role", "user")
                put("content", contentArray)
            }
        )

        val json = JSONObject().apply {
            put("model", "gpt-4o")
            put("messages", messages)
            put("max_tokens", 300)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $apiKey")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return "API 오류: ${response.code}"

            val bodyStr = response.body?.string() ?: return "응답 없음"
            val root = JSONObject(bodyStr)
            val choices = root.getJSONArray("choices")

            return choices.getJSONObject(0).getJSONObject("message").getString("content")
        }
    }
}
