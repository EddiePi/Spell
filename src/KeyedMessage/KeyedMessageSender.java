package KeyedMessage;

import TSDB.MetricSender;

import java.util.List;

/**
 * Created by Eddie on 2018/3/5.
 */
public class KeyedMessageSender {
    MetricSender sender = new MetricSender();

    public void sendMessage(KeyedMessage message) {
        sender.addMessage(message.timeStamp, message.key, message.value, message.tags);
        sender.sendMessage();
    }

    public void sendMessages(List<KeyedMessage> keyedMessages) {
        for(KeyedMessage message: keyedMessages) {
            sendMessage(message);
        }
    }


}
