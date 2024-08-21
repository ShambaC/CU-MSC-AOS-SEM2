import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * The message class that defines the data a message carries
 */
public class Message {

    /**
     * The type of the message: normal or marker
     */
    public MessageType type;
    /**
     * Source of the message
     */
    public Node source;
    public String messageID;

    /**
     * Time it takes for the message to reach a destination
     */
    public int timer = 3;

    public Message(String messageID, MessageType type, Node source) {
        this.messageID = messageID;
        this.type = type;
        this.source = source;

        Timer timeEvent = new Timer(timer * 1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                timer = 0;
            }
        });

        timeEvent.setRepeats(false);
        timeEvent.start();
    }
}
