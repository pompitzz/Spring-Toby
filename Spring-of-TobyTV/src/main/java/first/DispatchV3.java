package first;

import java.util.List;

public class DispatchV3 {
    interface Post{
        void postOn(Facebook sns);
        void postOn(Instagram sns);
    }

    static class Text implements Post{

        @Override
        public void postOn(Facebook sns) {
            System.out.println("Text-Facebook -> " + sns.getClass().getSimpleName());
        }

        @Override
        public void postOn(Instagram sns) {
            System.out.println("Text-Instagram -> " + sns.getClass().getSimpleName());
        }
    }

    static class Picture implements Post{

        @Override
        public void postOn(Facebook sns) {
            System.out.println("Picture-Facebook -> " + sns.getClass().getSimpleName());
        }

        @Override
        public void postOn(Instagram sns) {
            System.out.println("Picture-Instagram -> " + sns.getClass().getSimpleName());
        }
    }

    interface SNS{
    }

    static class Facebook implements SNS{
    }

    static class Instagram implements SNS{
    }

    public static void main(String[] args) {
        List<SNS> snsList = List.of(new Facebook(), new Instagram());
        List<Post> postList = List.of(new Text(), new Picture());

        // postList.forEach(p -> snsList.forEach(s -> p.postOn(s)));
        // Error 발생

    }
}
