import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

    private List<Node> graph = new ArrayList<>();
    private int size;

    public Main() {

        init();
    }

    private void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFileChooser fc = new JFileChooser("./");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        fc.setFileFilter(filter);
        int res = fc.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                String content = new String(Files.readAllBytes(fc.getSelectedFile().toPath()));
                String[] lines = content.split("\\n");

                size = Integer.parseInt(lines[0]);

                for (int i = 1; i < lines.length; i++) {
                    String[] nodeIDs = lines[i].split(" ");

                    Node nodeA = new Node(nodeIDs[0]);
                    Node nodeB = new Node(nodeIDs[1]);

                    if (graph.contains(nodeA)) {
                        nodeA = graph.get(graph.indexOf(nodeA));
                    }
                    else {
                        graph.add(nodeA);
                    }

                    if (graph.contains(nodeB)) {
                        nodeB = graph.get(graph.indexOf(nodeB));
                    }
                    else {
                        graph.add(nodeB);
                    }

                    Channel channel = new Channel(nodeA, nodeB);
                    nodeA.outgoingChannels.add(channel);
                    nodeB.incomingChannels.add(channel);
                }

                // --------GRAPH DONE ---------------

                Node startNode = ParseGraph.findStarter(graph);

            }
            catch (IOException err) {
                err.printStackTrace();
            }
        }
        else {
            System.exit(0);
        }
    }
}