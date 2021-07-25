package techtown.org.outstagram

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // register 즉 가입하기 버튼 누르면 가입하기 화면으로 넘어가서 처리하게끔 인텐트 처리를 함
        // 현재 Activity에서 -> EmailSignup에 이벤트 처리하게끔 넘어감
        register.setOnClickListener(){
            val intent = Intent(this, EmailSignupActivity::class.java)
            startActivity(intent)
        }

        // 로그인하기 버튼 눌렀을 때 로그인을 하게끔 처리함
        login.setOnClickListener{
            // user, password 입력 받은 것을 String으로 받음
            val username = username_inputbox.text.toString()
            val password = password_inputbox.text.toString()
            // RetrofitService 인터페이스에서 만든 login 함수를 적용시킴, String으로 받은 값을 파라미터로 넘김, 통신을 함
            (application as MasterApplication).service.login(
                    username, password
            ).enqueue(object : Callback<User>{
                override fun onFailure(call: Call<User>, t: Throwable) {

                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    // 성공을 했을 경우 통신에
                    if(response.isSuccessful){
                        val user = response.body() // 성공적으로 처리 받았을 때 User 객체의 값을 받음
                        val token = user!!.token!! // 그 중 token을 받음
                        saveUserToken(token, this@LoginActivity) // 현재 Activity에 token 값을 저장해줌(sharedPreference로)
                        (application as MasterApplication).createRetrofit() // header에 token을 추가해줘야 하기 때문에 Retrofit 사용
                        Toast.makeText(this@LoginActivity, "로그인 하셨습니다", Toast.LENGTH_LONG).show()
                        startActivity(
                            Intent(this@LoginActivity, OutStagramPostListActivity::class.java)
                        )
                    }

                }

            })
        }

    }

    // token 값을 저장함 login_sp로 받았음
    fun saveUserToken(token : String, activity : Activity) {
        val sp = activity.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("login_sp", token)
        editor.commit()
    }


}