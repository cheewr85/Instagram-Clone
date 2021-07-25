package techtown.org.outstagram

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    // 인터페이스는 사용할 HTTP CRUD 동작을 정의해 놓는 것임
    // 즉 서버에서 HTTP Method 방식으로 어떻게 처리할 것인지 이 인터페이스에 정의를 함
    // POST -> CRUD의 Create(생성) 방식, BODY에 전송할 데이터를 담아서 서버에 생성함
//    @POST("user/signup/") // baseUrl 기준으로 요청할 URL 주소
//    fun register(
//            // Body에 전송할 DTO 객체로 Register를 만듬, 해당 객체는 Serializable을 통해서 보낼 준비와 JSON 형태로 받을 준비 완료됨
//            // Body 어노테이션을 통해서 해당 메소드를 사용해서 보냄
//            @Body register : Register
//    ): Call<User> // 여기서 우리가 짠 서버 API에서는 Register인 username과 password1, password2를 보내면 username과 token을 응답해주는데 이 부분 역시 User라는 DTO를 만들어서 객체로 Call을 받음
//    // 그리고 그대로 이 객체가 CallBack으로 받음

    @POST("user/signup/")
    @FormUrlEncoded // form-urlencoded : 키-값 방식, &(구분자) 사용, Key-Value & Key-Value
    fun register(
        @Field("username")username : String,
        @Field("password1")password1 : String,
        @Field("password2")password2 : String
    ): Call<User>

    // POST 방식으로 key-value로 동일하게 넘김, signup과 유사한 방식임
    // 응답 받을 때 token을 받지만 User 타입을 그대로 받음, 선택적으로 받을 수 있음, User 타입이 다른게 있어도 token만 받을 것임
    @POST("user/login/")
    @FormUrlEncoded
    fun login(
        @Field("username")username : String,
        @Field("password")password : String
    ):Call<User>


    // GET 방식으로 해당 JSON 데이터를 다 받아옴
    // JSON으로 받을 데이터를 Post 객체로 넘겨서 받음
    // 해당 객체를 ArrayList에 담아서 Post 리스트로 쭉 받아서 나오게 함, 그러기 위한 함수
    @GET("instagram/post/list/all/")
    fun getAllPosts():Call<ArrayList<Post>>

    // 사진을 업로드하기 위한 API
    @Multipart // 파트가 여러개임을 나타냄
    @POST("instagram/post/")
    fun uploadPost(
        // 이미지와 글을 올릴 것이므로 아래와 같이 설정해서 함
        @Part image : MultipartBody.Part,
        @Part ("content")requestBody : RequestBody
    ):Call<Post> // Post에서 Content와 Image를 다루기 때문에 결과값으로 해당 객체를 받음

    @GET("post/list/")
    fun getUserPostList():Call<ArrayList<Post>>
}