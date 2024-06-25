public class Message {
    
    public MessageType type;
    public Node source;

    public Message(MessageType type, Node source) {
        this.type = type;
        this.source = source;
    }
}
