package com.example.insta

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.insta.RetrofitClient.retrofitService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PostFragment : Fragment() {
    var imageUri: Uri? = null
    var contentInput: String = ""

    private val retrofitService: RetrofitService by lazy {
        RetrofitClient.retrofitService
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        원하는 fragment return
        return inflater.inflate(R.layout.post_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedImageView = view.findViewById<ImageView>(R.id.selected_img)
        val glide = Glide.with(activity as HomeActivity)


        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                사용자가 어떤 이미지를 선택했는지?
                imageUri = it.data?.data
                glide.load(imageUri).into(selectedImageView)
            }
        imagePickerLauncher.launch(
            Intent(Intent.ACTION_PICK).apply {
                this.type = MediaStore.Images.Media.CONTENT_TYPE
            }
        )

        view.findViewById<EditText>(R.id.selected_content).doAfterTextChanged {
            contentInput = it.toString()
        }

        view.findViewById<TextView>(R.id.upload).setOnClickListener {
            val contentResolver = (activity as HomeActivity).contentResolver
            val mimeType = contentResolver.getType(imageUri!!)?.toMediaTypeOrNull()
            val file = getRealFile(imageUri!!)
            if (file != null && mimeType != null) {
                val requestFile = file.asRequestBody(mimeType)
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                val content = contentInput.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val header = HashMap<String, String>()
                val sp = (activity as HomeActivity).getSharedPreferences("user_info", Context.MODE_PRIVATE)
                val token = sp.getString("token", "")
                header["Authorization"] = "token $token"

                retrofitService.uploadPost(header, body, content).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            // 성공적으로 업로드
                            Log.d("PostFragment", header.toString())
                            Log.d("PostFragment", content.toString())
                            Log.d("PostFragment", body.toString())
                        } else {
                            // 응답 실패
                            Log.d("PostFragment", "실패")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("PostFragment", "Failure: ${t.message}")
                    }
                })
            } else {
                Log.d("PostFragment", "File or MIME type is null")
            }
        }


    }

    //    파일 가져오는 함수
    private fun getRealFile(uri: Uri): File? {
        var uri: Uri? = uri
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        var cursor: Cursor? = (activity as HomeActivity).getContentResolver().query(
            uri!!,
            projection,
            null,
            null,
            MediaStore.Images.Media.DATE_MODIFIED + " desc"
        )
        if (cursor == null || cursor.getColumnCount() < 1) {
            return null
        }
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val path: String = cursor.getString(column_index)
        if (cursor != null) {
            cursor.close()
            cursor = null
        }
        return File(path)
    }
}