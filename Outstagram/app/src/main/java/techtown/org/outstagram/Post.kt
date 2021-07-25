package techtown.org.outstagram

import java.io.Serializable

class Post(
        // Post 리스트에 필요한 owner, content, image Url을 받아서 옴
        // 거기서 받아오는 데이터에 대해서 String으로 받음 User 데이터 객체를 받는것과 동일함
        // Serializable, 액티비티, 즉 PostList에 보낼 준비가 됨
        // 해당 클래스를 매개로 Post에 데이터를 보이게 함
    val owner : String? = null,
    val content : String? = null,
    val image : String? = null
): Serializable