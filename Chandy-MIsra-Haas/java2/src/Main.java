import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

public class Main {

    public static Node findInitiator(List<Node> nodeList) {
        int min = Integer.MAX_VALUE;
        Node minNode = null;

        for (Node node : nodeList) {
            if (node.blockingList.size() == 0) {
                return node;
            }
            else {
                if (node.blockingList.size() < min) {
                    min = node.blockingList.size();
                    minNode = node;
                }
            }
        }

        return minNode;
    }

    public static void deadlock(Node initiator, List<Node> nodeList) {

    }

    public static void main(String[] args) {
        List<Node> nodeList = new ArrayList<>();

        JFileChooser fc = new JFileChooser("./");
        int res = fc.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            String content;
            try {

                content = new String(Files.readAllBytes(fc.getSelectedFile().toPath()));
                String[] lines = content.split("\\n");

                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) {
                        continue;
                    }

                    String v1 = line.split(" ")[0].trim();
                    String v2 = line.split(" ")[1].trim();

                    Node node1 = new Node(v1);
                    Node node2 = new Node(v2);

                    if (nodeList.contains(node1)) {
                        node1 = nodeList.get(nodeList.indexOf(node1));
                    }
                    else {
                        nodeList.add(node1);
                    }

                    if (nodeList.contains(node2)) {
                        node2 = nodeList.get(nodeList.indexOf(node2));
                    }
                    else {
                        nodeList.add(node2);
                    }

                    node1.blockedByList.add(node2);
                    node2.blockingList.add(node1);
                }

                Node initiator = findInitiator(nodeList);
                for (Node block : initiator.blockedByList) {
                    initiator.engagingQuery = false;

                    initiator.sentMsgs.add(block);
                    block.query(initiator);
                }

                if (initiator.sentMsgs.isEmpty()) {
                    System.out.println("Deadlock Detected");
                }
                else {
                    System.out.println("No Deadlock detected");
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.exit(1);
        }
    }
}