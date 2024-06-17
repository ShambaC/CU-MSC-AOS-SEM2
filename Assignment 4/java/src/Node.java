import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.Timer;

public class Node extends JButton implements Runnable {
    
    public String id;
    public Node parent;
    public Queue<Node> queue;
    public boolean isPrivileged;
    public boolean isInCS = false;
    public boolean isRequestingCS = false;

    private ActionListener nodeBtnListener = new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		if(!isRequestingCS && !isInCS) {
    			System.out.println("\nNode " + id + " is requesting for CS");
                isRequestingCS = true;
    		}
    	};
    };

    public Node(String id, int size) {
        super(id);

        this.id = id;
        this.parent = null;
        this.queue = new ArrayDeque<>(size);
        this.isPrivileged = false;

        this.addActionListener(nodeBtnListener);
    }

    public Node setParent(Node parent) {
        this.parent = parent;

        return this;
    }

    @Override
    public void run() {
        while(true) {
        	synchronized(this) {
        		if(isRequestingCS) {
        			if(isPrivileged) {
        				isRequestingCS = false;
        				isInCS = true;
        				startCS();
        			}
        			else {
        				this.queue.add(this);
        				Node parent = this.parent;
        				Node child = this;
        				while(parent != null) {
        					parent.queue.add(child);
        					child = parent;
        					parent = parent.parent;
        				}
        			}
        		}
        	}
        }
    }

    private void startCS() {
		Random random = new Random(System.currentTimeMillis());
		int randomDelay = random.nextInt(3,11);
		
		 System.out.println("\nNode " + id + " going into CS for " + randomDelay + "secons\n");
		 
		 Timer countdownTimer = new Timer(1000, new ActionListener() {
			int remainingTime = randomDelay;
			@Override
			public void actionPerformed(ActionEvent e) {
				remainingTime --;
				 
				setText(id + "," + remainingTime);
			}
		 });
		 
		 Timer timer = new Timer(randomDelay*1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				countdownTimer.stop();
				setText(id);
				stopCS();
			}
			 
		 });
		 
		 countdownTimer.setRepeats(true);
		 timer.setRepeats(false);
		 
		 countdownTimer.start();
		 timer.start();
	}

    private void stopCS() {
    	System.out.println("\nNode " + id + " is done with CS.");
        isInCS = false;
        
        afterCS(this);
         
    }

    private void afterCS(Node node) {
    	Node currentNode = node;
        Queue<Node> tempQueue = node.queue;
        Queue<Node> parentQueue = tempQueue;
        
		 while(!parentQueue.isEmpty()) {
			tempQueue = parentQueue; 
		    while(!tempQueue.element().isRequestingCS) {
		        
		        Node nextInQueue = tempQueue.poll();
		
		        System.out.println("Passing the token to the next node " + nextInQueue.id);
		        nextInQueue.isPrivileged = true;
		        currentNode.isPrivileged = false;
		        
		        tempQueue = nextInQueue.queue;
		        
		        currentNode = nextInQueue;
		    }
			
			tempQueue.poll().run();
			parentQueue = currentNode.parent.queue;
		}
         
		checkEmptyQueue(currentNode);
        
    }

    private void checkEmptyQueue(Node currentNode) {
        while(currentNode.parent != null) {
        	if(!currentNode.parent.queue.isEmpty()) {
        		afterCS(currentNode.parent);
        	}
        	else {
        		currentNode = currentNode.parent;
        	}
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)    return false;
        if (!(obj instanceof Node))   return false;

        Node oNode = (Node) obj;
        return oNode.id.equalsIgnoreCase(this.id);
    }

    @Override
    public String toString() {
        String outString = "";

        outString += "\n";
        outString += "\nid: " + id;
        outString += "\nQueue: {";

        Object[] queuArr = this.queue.toArray();
        for (int i = 0; i < queuArr.length; i++) {
            Node queueNode = (Node) queuArr[i];
            outString += "Node " + queueNode.id + ", ";
        }

        outString += "}\n";

        return outString;
    }
}
