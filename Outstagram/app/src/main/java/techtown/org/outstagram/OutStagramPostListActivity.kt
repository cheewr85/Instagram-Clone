package techtown.org.outstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.activity_out_stagram_post_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OutStagramPostListActivity : AppCompatActivity() {

    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_out_stagram_post_list)

        glide = Glide.with(this) // 현재 Activity로부터 View를 가져옴

        // Retrofit Interface에서 GET 어노테이션으로 Post의 데이터를 POST 클래스의 Callback 하게끔 설정한 함수를 실행함
        (application as MasterApplication).service.getAllPosts().enqueue(
            object : Callback<ArrayList<Post>>{ // Post 객체를 Callback 받아서 ArrayList로 받음
                override fun onFailure(call: Call<ArrayList<Post>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<ArrayList<Post>>,
                    response: Response<ArrayList<Post>>
                ) {
                    // 성공적으로 응답을 받았다면
                    if(response.isSuccessful){
                        val postList = response.body() // 필요한 데이터를 모두 postList에 담음, owner, content, image를 POST 객체로 담았으므로 리턴받은 값으로 연결지음
                        val adapter = PostAdapter(
                            postList!!, // PostAdapter 활용, 여기서 매개변수 postList를 받으면 이 값을 ArrayList로 return을 받기 때문에 이 값이 그대로 PostAdapter ArrayList로 자연스럽게 담기면서 저장되고 넘김
                            LayoutInflater.from(this@OutStagramPostListActivity), // 현재 레이아웃에서 사용함
                            glide // 사진 처리를 위한 Glide 라이브러리 활용함, 현재 Activity에서 시작
                        )
                        post_recyclerview.adapter = adapter // 위에서만든 adapter를 적용함
                        post_recyclerview.layoutManager = LinearLayoutManager(this@OutStagramPostListActivity) // 현재 xml에서 정의한 Recyclerview에 대해서 LinearLayout을 적용시킴
                    }
                }

            }
        )
        // 본인 버튼을 누르면 MyList 액티비티로 넘어감
        my_list.setOnClickListener { startActivity(Intent(this, OutStagramUserInfo::class.java))}
        // 업로드 버튼을 누르면 upload 액티비티로 넘어감
        upload.setOnClickListener { startActivity(Intent(this, OutStagramMyPostListActivity::class.java))}
        // 정보 버튼을 누르면 user_info 액티비티로 넘어가게 함
        user_info.setOnClickListener { startActivity(Intent(this, OutStagramUploadActivity::class.java))}

    }


}

class PostAdapter(
    var postList: ArrayList<Post>, // Post 리스트로 그릴 데이터 클래스를 넘김 ArrayList에 생성함, 데이터를 받음
    val inflater: LayoutInflater, // LayoutInflater -> XML 즉, item_view를 연결하기 위해주는 역할을 함
    val glide : RequestManager // 이미지를 연결해주기 위해서 Glide 라이브러리 활용
): RecyclerView.Adapter<PostAdapter.ViewHolder>() { // 리사이클러뷰에 표시될 item_view를 생성하기 위해서 연결하기 위해서 상속받은 Adapter 클래스, ViewHolder 뷰와 연결하기 위한 Adapter
    // Adapter를 상속받아서 PostAdapter 클래스의 ViewHolder를 타입 파라미터로 넘겨받음(그래서 adapter를 만들시 같이 꼭 만들어줘야함), 그래서 자연스럽게 inner class로 정의한 ViewHolder를 사용할 수 있음
    // ViewHolder를 통해서 Post 객체로 받은 데이터에 연결할 View들을 정의하고 초기화함
    // POST 객체로 받을 데이터를 그에 맞는 View에 넣기 위해서 만든 것
    inner class ViewHolder(itemView : View):RecyclerView.ViewHolder(itemView) {
        // inner class를 사용, RecyclerView의 ViewHolder를 상속받음, POST 객체 데이터에 필요한 내용은 owner, image, content의 뷰를 보관함
        // ViweHolder를 상속받아서 필요한 메소드를 활용함
        val postOwner : TextView
        val postImage : ImageView
        val postContent : TextView
        init {
            postOwner = itemView.findViewById(R.id.post_owner)
            postImage = itemView.findViewById(R.id.post_img)
            postContent = itemView.findViewById(R.id.post_content)
        }
    }

    // ViewHolder를 상속 받아서 구현을 해야할 메소드를 아래와 같이 구현해 놓음
    // 이 메소드를 통해서 직접적으로 itemView에 데이터를 연결하는 역할을 함
    // 위에서 정의한 ViewHolder 클래스를 상속받음, 그를 통해서 item_view를 inflater를 통해서 연결을 해주고 이를 통해서 개별적으로 만들어둔 item_view를 생성하고 각각 owner,image,content 요소들을 연결지음
    // 그러면서 마지막에 item_view xml을 활용한 것을 바탕으로 해당 view를 리턴시킴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // itemView로 쓸 레이아웃을 ViewHolder를 통해서 연결시킴
        val view = inflater.inflate(R.layout.outstagram_item_view,
        parent, false)
        return ViewHolder(view)
    }

    // Post 클래스는 서버와 통신하여서 JSON 형태로 데이터를 계속해서 받고 그 받은 데이터는 postList라는 ArrayList에 POST 객체로써 데이터가 추가됨, 통신시 그렇게 보냄
    // 그리고 그런식으로 POST 객체 형태로 ArrayList에 받은 데이터만큼 쌓이게 됨
    override fun getItemCount(): Int {
        // 거기서 총 리스트 개수 리턴함
        return postList.size
    }

    // 위에서 View를 그리고 ViewHolder로 찾은 뒤 변수 초기화를 하였고, 그리고 데이터를 받아오는 것까지 함
    // 이제 이 메소드를 통해서 내부 클래스 ViewHolder에 각각 View들을 매개변수로 받은 뒤, 필요로 한 데이터를 연결해주면 됨
    // ArrayList를 통해서 position을 각각 찾음, 그리고 각각 View에 해당하는 데이터를 연결시켜서 나타나게 해 줌
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // holder로 잡고 있는 View에서 postList 즉 ArrayList에 저장된 각각 저장된 위치(position)를 받고 그 위치에서 post 객체에서 owner, content를 받음
        holder.postOwner.setText(postList.get(position).owner)
        holder.postContent.setText(postList.get(position).content)
        glide.load(postList.get(position).image).into(holder.postImage) // Glide를 위에서 정의해서 현재 Activity로 나타낼 것인데 여기서 postImage로 받은 Url로 View를 지정하고 로드를 함
    }
}