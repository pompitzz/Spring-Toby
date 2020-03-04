package third;

import java.util.*;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/05
 */
public class GenericsEx4 {

    public static void main(String[] args) {
        Integer i = 10;
        Number n = i;

        List<Integer> intList = new ArrayList<>();
        // List<Number> numberList = intList // 불가!
        // type parameter는 상속관계에 영향을 주지 않는다!

        ArrayList<Integer> arrList = new ArrayList<>();
        List<Integer> list = arrList;

        // Type parameter의 수가 달라도 다형성이 적용된다.
        List<String> s1 = new MyList<String, Integer>();

        // 알아서 타입 추론을 해주기 때문에 문제가 발생하지 않음.
        GenericsEx4.method(1, Arrays.asList(1, 2, 3));

        // 하위버전에서는 제네릭스가 복잡해지면 에러뜰 수도있어 명시적으로 알려줄 수 있다.
        GenericsEx4.<Integer>method(1, Arrays.asList(1, 2, 3));


        // 이것도 타입 추론
        List<String> str1 = new ArrayList<String>();
        List<String> str2 = new ArrayList<>();

        // emptyList 메서드에 명시된 타입 파라미터가 없더라도 타입 추론을 해준다.
        final List<String> list2 = Collections.emptyList();

    }

    static class MyList<E, P> implements List<E>{

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<E> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(E e) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public E get(int index) {
            return null;
        }

        @Override
        public E set(int index, E element) {
            return null;
        }

        @Override
        public void add(int index, E element) {

        }

        @Override
        public E remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @Override
        public ListIterator<E> listIterator() {
            return null;
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return null;
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return null;
        }
    }

    static <T> void method(T t, List<T> list){

    }
}
