package ch6.step4;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/14
 */
public class Message {
    String text;

    private Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Message newMessage(String text){
        return new Message(text);
    }
}
