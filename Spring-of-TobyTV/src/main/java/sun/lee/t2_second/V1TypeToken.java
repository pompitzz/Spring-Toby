package sun.lee.t2_second;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stranger on 2020/02/21
 */
public class V1TypeToken {

    static class Generic<T>{
        T value;
    }

    static <T> T create(Class<T> clazz) throws Exception{
        return clazz.newInstance();
    }

    static class TypesafeMap{

        Map<Class<?>, Object> map = new HashMap<>();

        // Class의 타입과 일치하는 value를 넣기위해서는 이러면 안전하지 못하다.
        void putOld(Class<?> clazz, Object value){
            map.put(clazz, value);
        }

        <T> void put (Class<T> clazz, T value){
            map.put(clazz, value);
        }

        <T> T get(Class<T> clazz){
            // return (T) map.get(clazz); // Object이므로 타입 캐스팅을 해준다? 이는 타입 세이프한 방법이 아니다.
            return clazz.cast(map.get(clazz)); // Class 객체에 존재하는 cast를 통해 타입 캐스팅을 명시적으로 해준다.

            // 이로서 TypesafeMap은 **강제로 캐스팅** 하는게 없으므로 안정적인 코드를 만들 수 있다.

            /**
             * 이렇게 특정 타입의 클래스 정보를 넘겨서 타입 안정성을 보장하는 기법을 TYPE TOKEN이라고 한다.
             * - 현재 이방식에서는 List<String>, List<Integer> 둘은 List 하나로 보기 때문에 이 둘을 동시에 넣을 수 없게된다.
             * - 이럴 때 SUPER TYPE TOKEN을 사용하면 해결할 수 있다.
             */
        }

    }
    public static void main(String[] args) throws Exception{
        TypesafeMap typesafeMap = new TypesafeMap();
        typesafeMap.put(String.class, "String");
        System.out.println("typesafeMap.get(String.class) = " + typesafeMap.get(String.class));
        // typesafeMap.put(String.class, 1); // 불가

        typesafeMap.put(Integer.class, 1);
        System.out.println("typesafeMap.get(Integer.class) = " + typesafeMap.get(Integer.class));

        List<Integer> integerList = Arrays.asList(1, 2, 3, 4);
        typesafeMap.put(List.class, integerList);
        System.out.println("typesafeMap.get(List.class) = " + typesafeMap.get(List.class));

        List<String> stringList = Arrays.asList("H", "E", "L", "L", "O");
        typesafeMap.put(List.class, stringList); // 위를 덮어 쓴다.
        System.out.println("typesafeMap.get(List.class) = " + typesafeMap.get(List.class));

        // typesafeMap.put(List<String>.class, stringList); <String> 불가능!!
    }
}
