package techtown.org.outstagram

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MasterApplication: Application() {

    // Retrofit Interface 사용
    lateinit var service : RetrofitService

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this) // Stetho를 Init을 함
        createRetrofit() // 앱 시작하자마자 돌림
    }

    // Retrofit을 만드는 함수
    fun createRetrofit() {
        // Interceptor로 통신 가로챔
        val header = Interceptor {
            val original = it.request() // it 키워드 활용해서 Interceptor를 씀

            // 로그인 유무 확인
            if(checkIsLogin()){
                // token이 있다면 let 블럭을 함, let을 할 경우 받은 token을 저장함(블럭의 결과값을 리턴함)
                getUserToken()?.let{token ->
                    val request = original.newBuilder()
                            .header("Authorization", "token " + token)
                            .build()
                    it.proceed(request)
                }


            }else{ // 로그인이 안되어 있다면 original을 그냥 내보냄
                it.proceed(original)
            }


        }

        val client = OkHttpClient.Builder()
                .addInterceptor(header)
                .addNetworkInterceptor(StethoInterceptor())
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("http://mellowcode.org/")
                .addConverterFactory(GsonConverterFactory.create()) // JSON형태로 받아서 넘겼기 때문에 이를 GSON으로 변환해야 하지만 이를 Retrofit2를 활용하여서 처리를 함
                .client(client)
                .build()
        service = retrofit.create(RetrofitService::class.java) // 인스턴스로 서비스 구현함
    }

    // sharepreference로 통신을 했을 때 call로 구현을 했기 때문에 emailsignup activity에서 sharepreference로 저장을 하고 이 값을 받아서 확인할 수 있음, 아래의 함수를 통해서
    // 이 token을 활용해서 로그인 유무와 토큰 유무를 확인하는 메인 통신을 만든 것임, sharedpreference에서 키 값을 login_sp로 뒀기 때문에 어디서든 활용 가능한 것임
    // token 유무
    fun checkIsLogin(): Boolean {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = sp.getString("login_sp","null")
        if (token != "null") return true
        else return false
    }

    // token 값을 받음
    fun getUserToken(): String? {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = sp.getString("login_sp","null")
        if(token == "null") return null // null이면 null을 내보내고
        else return token // 아니면 token을 내보냄
    }


}