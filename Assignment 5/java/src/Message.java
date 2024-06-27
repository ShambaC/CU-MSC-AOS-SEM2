import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Message {

    public MessageType type;
    public Node source;
    public String messageID;

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
