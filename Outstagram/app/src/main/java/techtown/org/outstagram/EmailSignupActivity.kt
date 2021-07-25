package techtown.org.outstagram

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailSignupActivity : AppCompatActivity() {

    // 입력 받은 아이디, 비밀번호를 받기 위해서 EditText 객체를 선언함, 즉 EditText라는 요소는 이미 만들어져 있으므로 이를 사용하기 위해서 선언한 것임
    // 그런데 여기서 내가 받을 것은 아이디 비밀번호 이므로 해당 변수명에 맞게 이름을 짓고 이에 맞는 EditText를 선언함
    // lateinit -> 코틀린 상에서는 객체를 선언할 때 속성을 초기화도 같이 해줘야 하는데 함수를 통해서 onCreate에서 View를 찾아서 연결해줄 것이기 때문에 lateinit을 통해서 초기화를 늦춤
    lateinit var usernameView: EditText
    lateinit var userPassword1View: EditText
    lateinit var userPassword2View: EditText
    lateinit var registerBtn : TextView
    lateinit var loginBtn : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 로그인이 되어 있으면 바로 POSTList 액티비티로 넘어가게 함, MasterApplication에 있는 함수를 통해서 로그인 확인함
        if((application as MasterApplication).checkIsLogin()){
            // POSTList로 넘어가게끔 인텐트 처리함
            finish() // 시작은 런처 액티비티로 시작하니깐 로그인이 확인됐다면 런처 액티비티는 볼 필요가 없으니 종료시키고 바로 넘어감
            startActivity(Intent(this, OutStagramPostListActivity::class.java))
        } else { // 로그인이 되어 있지 않다면 현재 액티비티를 그려주고 회원가입 혹은 로그인 하게끔 처리를 함
            setContentView(R.layout.activity_email_signup)
            initView(this@EmailSignupActivity) // 켜자마자 첫 화면으로 나오고 해당 View들과 상호작용을 해서 넘어갔을 경우 처리를 하기 위해서 onCreate에 바로 시작해서 선언
            setupListener(this)
        }
    }

    // lateinit을 통해서 초기화를 늦춤, 이를 통해 이 함수에서 초기화할 수 있음
    // xml로 그린 View는 단순히 View만의 역할을 하는데 여기서 아이디와 비밀번호를 입력한 것을 받아서 서버와 통신을 하는 이벤트를 처리해야하기 때문에 findViewById로 해당 뷰를 찾고 연결함
    // View 자체는 껍데기에 불과함, 이를 연결해서 이벤트 처리하고 상호작용하기 위해서 소스코드 파일에 연결해서 구현시켜줘야 함
    fun initView(activity : Activity){
        usernameView = activity.findViewById(R.id.username_inputbox)
        userPassword1View = activity.findViewById(R.id.password1_inputbox)
        userPassword2View = activity.findViewById(R.id.password2_inputbox)
        registerBtn = activity.findViewById(R.id.register)
        loginBtn = activity.findViewById(R.id.login)
    }

    fun setupListener(activity : Activity){
        registerBtn.setOnClickListener {
            register(this@EmailSignupActivity)
        }
        loginBtn.setOnClickListener {
            startActivity(
                Intent(this@EmailSignupActivity, LoginActivity::class.java)
            )
        }
    }

    fun register(activity: Activity) {
        val username = getUserName()
        val password1 = getUserPassword1()
        val password2 = getUserPassword2()

        // register라는 객체를 만들어서 입력받은 데이터를 보내기 위해서 위에서 해당 입력값을 받고 Register 객체를 만들어서 파라미터로 넘겨준뒤
//        val register = Register(username, password1, password2)

        // RetrofitService에서 사용하는 register함수를 이용 POST 요청을 하기 위해서 위에서 객체로 만들고 넘겨받은 인풋값을 가지고 요청을 보냄
        // 그리고 요청하고 보낸 뒤 받는 값은 User이기 때문에 이 값을 Callback으로 받음
        // application 단으로 선언했기 때문에 여기서 이 application을 MasterApplication으로 타입 캐스팅을 함
        // 그 이후 MasterApplication에 있는 Retrofit service interface를 이용해서 함수를 쓸 것이므로 선언함
        // 그리고 여기에 있는 service에서 register에서 객체 DTO로 이 함수에 있는 register를 매개변수로 받은 변수를 넘겨준다음 , enqueue 비동기로 실행을 함
        // 비동기 실행 즉 요청과 응답을 별개로 진행시킴
        // 그리고 통신을 register로 보내뒤 post 요청을 해버린 다음, 여기서 Interface에서 응답을 User로 받기로 했으므로 통신종료 후 이벤트 처리를 위해 CallBack을 등록함
        (application as MasterApplication).service.register(
                username, password1, password2
        ).enqueue(object : Callback<User>{
            // 2개를 implements 받음
            // 등록받은 CallBack User에서 onResponse는 성공 onFailure는 실패임, 이를 아래와 같이 메인스레드에서 처리함, 토스트 메시지 띄움
            override fun onFailure(call: Call<User>, t: Throwable) {
                // Callback이 실패했을 경우, Toast 메시지를 띄움
                Toast.makeText(activity, "가입에 실패 하였습니다.", Toast.LENGTH_LONG).show()

            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    // 정상적으로 응답을 받았을 경우
                    Toast.makeText(activity, "가입에 성공하였습니다.", Toast.LENGTH_LONG).show()
                    // 성공 결과로 token을 받음, 그전에 user를 먼저 받음
                    val user = response.body()
                    val token = user!!.token!! // 여기서 응답받은 body는 token을 다시 주기 때문에 해당 값을 token으로 가져옴(null 일 수 있음, 서버상 완벽히 구현이 안되어 있으므로)
                    saveUserToken(token, activity) // 그리고 받은 token과 현재 activity를 넘겨줌
                    // sharedPreference에 token 값이 없을 것이므로 다시 호출해서 header에 붙을 수 있게끔 설정을 별도로 함
                    (application as MasterApplication).createRetrofit()
                    activity.startActivity(
                        Intent(activity, OutStagramPostListActivity::class.java)
                    )
                }
            }

        })
    }

    fun saveUserToken(token: String, activity: Activity) {
        // onResponse에서 사용함 login_sp가 key이고 token이 value임 해당 값을 받아서 sharepreference로 저장함
        // sharedpreference와 token의 key 값 모두 login_sp로 설정했음
        // token을 받아서 sharedpreference에 저장을 함
        // application에서만 사용 가능, 즉 현재 화면에서 사용하기 위함
        // login_spㄹ라는 key값을 가지고 token을 넣어줌
        val sp = activity.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val editor = sp.edit() // 에디터를 통해서 넣어줌
        editor.putString("login_sp", token) // 에디터를 통해서 login_sp에 token 값을 넘겨줌
        editor.commit() // 데이터를 넣음 commit
    }

    // initView 메소드를 통해서 선언한 EditText 인스턴스들을 findViewById로 연결을 한 뒤 거기서 입력한 데이터를 받기 위한 함수를 아래와 같이 만듬
    // 각각 아이디 비밀번호 등을 받아올 수 있음
    fun getUserName(): String{
        return usernameView.text.toString()
    }

    fun getUserPassword1(): String{
        return userPassword1View.text.toString()
    }

    fun getUserPassword2(): String{
        return userPassword2View.text.toString()
    }
}