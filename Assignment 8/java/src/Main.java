import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    
    private JTextArea tArea;
    private List<Node> nodeList = new ArrayList<>();
    private Controller controller;

    private int numNodes;

    public Main() {
        setTitle("Weight throwing");
        setSize(1200, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        numNodes = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of nodes: "));

        init();
    }

    private void init() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));

        JPanel nodePanel = new JPanel(new GridLayout(numNodes, 1, 0, 10));

        tArea = new JTextArea();
        tArea.setMargin(new Insets(5, 10, 10, 0));
        tArea.setFont(new Font("Helvetica", Font.PLAIN, 14));
        DefaultCaret caret = (DefaultCaret) tArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scroll = new JScrollPane(tArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        controller = new Controller(tArea);

        for (int i = 0; i < numNodes; i++) {
            Node node = new Node(Integer.toString(i), controller, tArea);
            nodeList.add(node);
            nodePanel.add(node);
        }
        nodePanel.add(controller);

        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            node.setNodeList(nodeList);
        }
        controller.setNodeList(nodeList);

        mainPanel.add(nodePanel);
        mainPanel.add(scroll);
        add(mainPanel);

        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            node.setNodeList(nodeList);

            Thread t = new Thread(node);
            t.start();
        }
        Thread controllerThread = new Thread(controller);
        controllerThread.start();
    }

    public static void main(String[] args) {

        Main UI = new Main();
        UI.setVisible(true);

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UI.repaint();
            }
        });

        timer.setRepeats(true);
        timer.start();
    }
}