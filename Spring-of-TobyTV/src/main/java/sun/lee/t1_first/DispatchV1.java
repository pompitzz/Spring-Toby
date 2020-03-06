package sun.lee.t1_first;

import java.util.Arrays;
import java.util.List;

public class DispatchV1 {
    interface Post{
        void postOn(SNS sns);
    }

    static class Text implements Post{

        @Override
        public void postOn(SNS sns) {
            System.out.println("Text -> " + sns.getClass().getSimpleName());
        }
    }

    static class Picture implements Post{

        @Override
        public void postOn(SNS sns) {
            System.out.println("picture -> " + sns.getClass().getSimpleName());
        }
    }

    interface SNS{
    }

    static class Facebook implements SNS{
    }

    static class Instagram implements SNS{
    }

    public static void main(String[] args) {
        List<SNS> snsList = Arrays.asList(new Facebook(), new Instagram());
        List<Post> postList = Arrays.asList(new Text(), new Picture());

        postList.forEach(p -> snsList.forEach(s -> p.postOn(s)));

    }
}
