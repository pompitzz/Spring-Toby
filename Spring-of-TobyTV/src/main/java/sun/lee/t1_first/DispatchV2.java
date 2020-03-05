package sun.lee.t1_first;

import java.util.List;

public class DispatchV2 {
    interface Post{
        void postOn(SNS sns);
    }

    static class Text implements Post{

        @Override
        public void postOn(SNS sns)
        {
            if(sns instanceof Facebook){
                System.out.println("Text-Facebook -> " + sns.getClass().getSimpleName());
            }

            if (sns instanceof Instagram){
                System.out.println("Text-Instagram -> " + sns.getClass().getSimpleName());
            }
        }
    }

    static class Picture implements Post{

        @Override
        public void postOn(SNS sns) {
            if(sns instanceof Facebook){
                System.out.println("Picture-Facebook -> " + sns.getClass().getSimpleName());
            }

            if (sns instanceof Instagram){
                System.out.println("Picture-Instagram -> " + sns.getClass().getSimpleName());
            }
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

        postList.forEach(p -> snsList.forEach(s -> p.postOn(s)));

    }
}
