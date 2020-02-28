package first;

import java.util.List;

public class DispatchV4 {
    interface Post{
        void postOn(SNS sns);
    }

    static class Text implements Post{

        @Override
        public void postOn(SNS sns) {
            sns.post(this);
        }
    }

    static class Picture implements Post{

        @Override
        public void postOn(SNS sns) {
            sns.post(this);
        }
    }

    interface SNS{
        void post(Text post);

        void post(Picture post);
    }

    static class Facebook implements SNS{

        @Override
        public void post(Text post) {
            System.out.println("Facebook Text Post");
        }

        @Override
        public void post(Picture post) {
            System.out.println("Facebook Picture Post");
        }
    }

    static class Instagram implements SNS{

        @Override
        public void post(Text post) {
            System.out.println("Instagram Text Post");
        }

        @Override
        public void post(Picture post) {
            System.out.println("Instagram Picture Post");
        }
    }

    public static void main(String[] args) {
        List<SNS> snsList = List.of(new Facebook(), new Instagram());
        List<Post> postList = List.of(new Text(), new Picture());

        postList.forEach(p -> snsList.forEach(s -> p.postOn(s)));

    }
}
