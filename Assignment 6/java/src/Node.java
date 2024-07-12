import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

public class Node extends JButton implements Runnable {
    
    public String id;
    public int u, v;

    public boolean isInCS = false;
    public boolean isInDeadlock = false;

    public Node connNode;

    public Node(String id, int init) {
        super(init + " / " + init);

        this.id = id;
        this.u = this.v = init;
    }

    @Override
    public void run() {
        while (true) {
            synchronized(this) {
                if (!isInCS) {
                    // TODO: Randomly decide if going into CS, if yes. Randomly decide if it depends on a node, if yes then add that node to the connList
                    // TODO: while adding perform transmit, block, detect
                    // TODO: if results in deadlock, set the deadlock boolean
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isInCS) {
            setBackground(Color.red);
            setForeground(Color.white);
        }
        else {
            setBackground(Color.cyan);
            setForeground(Color.black);
        }
    }
}
