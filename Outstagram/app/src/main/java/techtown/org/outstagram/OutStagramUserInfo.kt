package techtown.org.outstagram

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_out_stagram_user_info.*

class OutStagramUserInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_out_stagram_user_info)

        // 전체 버튼을 누르면 POSTList 액티비티로 넘어감
        all_list.setOnClickListener { startActivity(Intent(this, OutStagramPostListActivity::class.java))}
        // 업로드 버튼을 누르면 upload 액티비티로 넘어감
        upload.setOnClickListener { startActivity(Intent(this, OutStagramMyPostListActivity::class.java))}
        // 정보 버튼을 누르면 user_info 액티비티로 넘어가게 함
        user_info.setOnClickListener { startActivity(Intent(this, OutStagramUploadActivity::class.java))}

        // 로그아웃 버튼을 눌러서 로그아웃 처리 SharedPreference로 함
        logout.setOnClickListener {
            // Sharedpreference로 처리하고 Put을 해서 null로 넘겨버림, 그렇게 해서 로그아웃 처리를 함
            val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString("login_sp", "null")
            editor.commit()
            // Retrofit에서 header 인증 정보를 빼줘야 하기 때문에 Retrofit 생성함
            (application as MasterApplication).createRetrofit()
            // 로그아웃을 했으므로 현재 액티비티 종료함
            finish()
            // 로그인 액티비티로 넘어감, 로그아웃을 했으므로
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}