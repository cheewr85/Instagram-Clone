package techtown.org.outstagram

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_out_stagram_upload.*
import kotlinx.android.synthetic.main.activity_out_stagram_upload.all_list
import kotlinx.android.synthetic.main.activity_out_stagram_upload.upload
import kotlinx.android.synthetic.main.activity_out_stagram_upload.user_info
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class OutStagramUploadActivity : AppCompatActivity() {

    lateinit var filePath : String // 업로드 버튼에서 바로 처리해주기 위해서 전역변수로 선언해서 사용함

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_out_stagram_upload)

        view_pictures.setOnClickListener{
            getPicture()
        }

        upload_post.setOnClickListener{
            uploadPost()
        }

        // 전체 버튼을 누르면 POSTList 액티비티로 넘어감
        all_list.setOnClickListener { startActivity(Intent(this, OutStagramPostListActivity::class.java))}
        // 업로드 버튼을 누르면 my_list 액티비티로 넘어감
        my_list.setOnClickListener { startActivity(Intent(this, OutStagramMyPostListActivity::class.java))}
        // 정보 버튼을 누르면 user_info 액티비티로 넘어가게 함
        user_info.setOnClickListener { startActivity(Intent(this, OutStagramUserInfo::class.java))}
    }

    fun getPicture() {
        // 사진을 앨범에서 선택하는 것은 현재 앱을 벗어나는 일을 처리하는 것이므로 인텐트 처리를 함
        val intent = Intent(Intent.ACTION_PICK)
        // 그 중에서 사진, 즉 앨범에 있는 사진에서 고르기 위해서 MediaStore를 처리해서 외부 저장소에서 선택하게 처리를 함
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // 디렉토리가 image이므로 그 이하에서 고를 수 있게 확인함
        intent.setType("image/*")
        // 어떤 이미지를 받았는지에 대한 결과를 받아야하기 때문에 ForResult로 받아야함
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) { // 앨범까지 들어가는 인텐트 처리를 했음, 여기서 ForResult로 처리해서 해당 데이터를 1000코드와 함께 담고 있어서 이렇게 처리를 함
            val uri : Uri = data!!.data!!
            filePath = getImageFilePath(uri) // 인텐트 처리해서 전역변수로 선언한 값에다가 FilePath를 바로 할당해줌
        }
    }

    fun getImageFilePath(contentUri : Uri) : String {
        var columnIndex = 0 // Index를 적기 위한 변수
        // Projection은 걸러내기 위한 툴임, MediaStore.Images.Media.DATA를 걸러낸다는 뜻(이미지만을 체크)
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        // cursor의 경우 리스트가 있을 때 그 중에서 하나하나 내려가는 것을 의미함, 1 <-(cursor), 이런식으로 인덱스든 리스트든 가리키고 있다는 커서, 가리키기 위해 있는 것
        // contentResolver, content를 관리하는 것 이것에 query를 요청함, contentUri와 projection을 넘겨줌, 여기서 위의 Uri와는 다름
        // contentUri와 contentResolver를 통해서 절대경로를 얻으려고 하는 것, 위의 uri는 상대경로임(실제 이미지 경로가 아님 다른 경로임)
        // 사진을 선택한 경로 Uri를 받고 그리고 Projection을 넘겨줘서 앨범에 있는 사진만을 체킹함
        val cursor = contentResolver.query(contentUri, projection, null, null, null)
        if(cursor!!.moveToFirst()) { // 커서를 첫번째로 이동시키고 column의 인덱스를 찾음
            // 여러가지 image중 MediaStore 구문 이하의 이미지를 찾음(실제 앱에 저장된 Storage에 접근해서 확인함)
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        // 그리고 cursor에서 이미지를 찾은뒤 getString을 하게 된다면 절대경로가 나옴
        return cursor.getString(columnIndex)
    }

    // 업로드를 위한 함수
    fun uploadPost() {
        val file = File(filePath) // File 객체의 이미지가 있는 Uri를 받아서 넘김
        // POST로 올릴때 이미지와 content를 올리므로 처리가 필요한거고 API에서도 RequestBody로 만들었기 때문에 해당 처리를 함, 서버에다가 POST할때 데이터를 저장하기 위한 형식 결정
        // 그리고 MediaType은 RequestBody를 만들고 타입처리를 하는 것임, 근데 Image이므로 image/*에다가 file 즉 넘겨받은 Uri(찾은 파일)로 해당 이미지로 RequestBody를 만들고 처리를 함
        val fileRequestBody = RequestBody.create(MediaType.parse("image/*"), file) // 넘겨받은 Uri와 파일 객체를 요청을 함, 절대경로 파일
        // 서버에게 보낼때 image로 보내야하므로 Multipart로 아래와 같이 만듬
        val part = MultipartBody.Part.createFormData("image", file.name, fileRequestBody) // 이미지를 받아서 처리함, 이미지를 보낼때 한 번에 딱 보내는게 아니라서 MultiPart 처리를 하고 위에서 작성한 file과 body로 보냄
        val content = RequestBody.create(MediaType.parse("text/plain"), getContent()) // 입력한 내용을 받아서 처리함, 추가적으로 서버에 올릴 Content도 처리를 해 줌

        (application as MasterApplication).service.uploadPost(
            part, content // 위에서 File로 받은 부분에 대해서 이미지 Uri와 이미지 데이터를 처리받고, 입력한 내용을 받음, POST하기 위해서
        ).enqueue(object : Callback<Post>{ // 앞서 했던 Retrofit 서버 통신 처리
            override fun onFailure(call: Call<Post>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if(response.isSuccessful){
                    finish() // 종료시키고 이동
                    my_list.setOnClickListener { startActivity(Intent(this@OutStagramUploadActivity, OutStagramMyPostListActivity::class.java))}
                }
            }

        })
    }

    // content 내용을 받는 함수
    fun getContent() : String {
        return content_input.text.toString()
    }
}