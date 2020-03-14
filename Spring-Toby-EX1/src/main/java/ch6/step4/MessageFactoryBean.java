package ch6.step4;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/14
 */
public class MessageFactoryBean implements FactoryBean<Message> {

    String text;

    public MessageFactoryBean(String text) {
        this.text = text;
    }

    @Override
    public Message getObject() throws Exception {
        return Message.newMessage(text);
    }

    @Override
    public Class<?> getObjectType() {
        return Message.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
