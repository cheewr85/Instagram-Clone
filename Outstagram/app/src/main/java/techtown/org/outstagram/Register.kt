package techtown.org.outstagram

import java.io.Serializable
// DTO(POJO) -> Data Transfer Object(Plain Old Java Object) 형태의 모델
// JSON 타입변환에 사용, 별다른 작업을 하진 않음 대신 가입하기 및 등록을 위한 데이터를 보내기 위해서 만듬
// JSON 형태로 받음
//class Register(
//        var username : String? = null,
//        var password1 : String? = null,
//        var password2 : String? = null
//):Serializable // Serializable인터페이스를 구현해서 다른 액티비티로 전달을 준비를 함, 이 인터페이스로 인해서 다른 Activity에서 해당 데이터를 마음껏 쓸 수 있음