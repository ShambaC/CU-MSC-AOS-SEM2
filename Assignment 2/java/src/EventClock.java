public class EventClock {
    public int counter;
    public int siteId;

    public EventClock(int counter, int siteId) {
        this.counter = counter;
        this.siteId = siteId;
    }

    @Override
    public String toString() {
        String outString = "<Counter = " + counter + ", siteId = " + siteId + ">";
        return outString;
    }
}