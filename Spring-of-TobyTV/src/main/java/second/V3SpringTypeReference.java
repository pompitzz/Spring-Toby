package second;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by Stranger on 2020/02/22
 */
public class V3SpringTypeReference {
    public static void main(String[] args) {
        // 스프링에서 SUPER TYPE TOKEN을 지원해준다.
        ParameterizedTypeReference<?> typeRef =
                new ParameterizedTypeReference<List<Map<Set<Integer>, String>>>() {};
        System.out.println("typeRef.getType() = " + typeRef.getType());


        // 스프링에서 컨트롤러단에서 List<User>를 반환한다고 하자.
        // RestTemplate를 사용하여 해당 url에 요청을하면 값을 반환해 줄 것이다.
        // 보통 이 값은 List<Map>로 받아서 get("name")을 따로 명시해야한다.
        // 이럴 때 SUPER TYPE TOKEN을 사용하면 타입 안전하게 받아올 수 있다.

        RestTemplate rt = new RestTemplate();
        // final User body = rt.getForEntity("http://localhost:8080", User.class).getBody();
//        List<User> listUsers = rt.exchange("http://localhost:8080", HttpMethod.GET, null
//                , new ParameterizedTypeReference<List<User>>() {}).getBody();
    }

    class User{
        String name;

        public String getName() {
            return name;
        }
    }
}
