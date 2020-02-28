package second;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Created by Stranger on 2020/02/22
 */
public class V2SuperTypeToken {

    public static void main(String[] args) throws NoSuchFieldException {
        step1();
        step2();
        step3();
    }

    private static void step1() throws NoSuchFieldException {
        System.out.println("\n ============================= Step 1 =============================");
        Sup<String> s = new Sup<>();

        Class<?> value = s.getClass().getDeclaredField("value").getType();

        System.out.println(value);
        // 이레이저로 인해 런타임 시 String 정보를 사라지고 Object가 되기 때문에 이는 Object가 출력된다.
    }

    public static void step2(){
        System.out.println("\n ============================= Step 2 =============================");
        SubString b = new SubString();
        Type t = b.getClass().getGenericSuperclass();
        ParameterizedType ptype = (ParameterizedType) t;

        System.out.println(ptype.getActualTypeArguments()[0]);
        // 리플렉션을 통해 런타임시 접근할 수 있도록 바이트 코드에 정보가 남아있다.
        // 이 타입은 ParameterizedType으로 받을 수 있다.

        ParameterizedType listPType = (ParameterizedType) new SubList().getClass().getGenericSuperclass();
        System.out.println(listPType.getActualTypeArguments()[0]);

        class SubTemp extends Sup<List<String>> {
        }
        // 지역 클래스로도 위와 같이 동작된다.

        System.out.println("\n------------- Using Anonymous Class 1 -------------");
        Sup b2 = new Sup<Map<String, Integer>>() {};
        // 임의의 익명 클래스를 만들 수 있다.
        ParameterizedType ptype2 = (ParameterizedType) b2.getClass().getGenericSuperclass();
        System.out.println(ptype2.getActualTypeArguments()[0]);


        System.out.println("\n------------- Using Anonymous Class 2 -------------");
        // 익명 클래스는 이렇게 한번에 사용할 수도 있다.
        // 지네릭 타입에 타입 파라미터를 주면서, 타입 파라미터를 런타임시에 가져올 수 있는 코드를 작성할 수 있다.
        ParameterizedType ptype3 = (ParameterizedType)
                                    (new Sup<List<Integer>>() {})
                                                        .getClass()
                                                        .getGenericSuperclass();
        System.out.println(ptype3.getActualTypeArguments()[0]);
    }

    public static void step3() {
        System.out.println("\n ============================= Step 3 =============================");
        // TypeReference<String> t = new TypeReference<>();
        // 런타임 예외 발생. <>에 넣은건 런타임에 남는 정보가 아니다!

        System.out.println(new TypeReference<List<List<List<Map<String,Integer>>>>>() {}.type);
        // 이렇게 익명 클래스를 통해 가져올 수 있다.
        // 이를 통해 TypesafeMapp을 만들 수 있다.

        TypesafeMap m = new TypesafeMap();

        final TypeReference<String> stringType = new TypeReference<String>() {};
        m.put(stringType, "HELLO");
        System.out.println("<String> : " + m.get(stringType));

        final TypeReference<List<String>> listStringType = new TypeReference<List<String>>() {};
        m.put(listStringType, List.of("HELLO", "WORLD"));
        System.out.println("List<String> : " + m.get(listStringType));

        final TypeReference<List<Integer>> listIntegerType = new TypeReference<List<Integer>>() {};
        m.put(listIntegerType, List.of(1, 2, 3));
        System.out.println("List<Integer> : " + m.get(listIntegerType));
    }

    static class Sup<T> {

        T value;
    }
    static class SubString extends Sup<String> {

    }
    static class SubList extends Sup<List<String>> {

    }
    static class TypeReference<T> { // same Sup
        Type type;

        public TypeReference() {
            Type stype = getClass().getGenericSuperclass();

            // 타입 정보가 들어있다.
            if (stype instanceof ParameterizedType) {
                this.type = ((ParameterizedType) stype).getActualTypeArguments()[0];
            } else {
                throw new RuntimeException();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            // 상속받은거니 superclass로 비교를 해야한다.
            if (o == null || getClass().getSuperclass() != o.getClass().getSuperclass()) return false;
            TypeReference<?> that = (TypeReference<?>) o;
            return type.equals(that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type);
        }
    }

    static class TypesafeMap {

        Map<TypeReference<?>, Object> map = new HashMap<>();

        <T> void put(TypeReference<T> tr, T value) {
            map.put(tr, value);
        }

        <T> T get(TypeReference<T> tr) {
            // TypeReference<String>은 받아준다. 하지만 이코드는 List<String>을 해결할 수 없다.
            //return ((Class<T>) tr.type).cast(map.get(tr));

            if (tr.type instanceof Class<?>)
                return ((Class<T>) tr.type).cast(map.get(tr));
            else
                return ((Class<T>) ((ParameterizedType) tr.type).getRawType()).cast(map.get(tr));
            // List<String>을 해결할 수 있다.
        }

    }
}
