public class Message {
    
    public MessageType type;
    public Node source;
    public String messageID;

    public Message(String messageID, MessageType type, Node source) {
        this.messageID = messageID;
        this.type = type;
        this.source = source;
    }
}
