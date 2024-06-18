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

	private boolean hasSentRequestToParent = false;

	private ActionListener nodeBtnListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (!isRequestingCS && !isInCS) {
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

	public void add(Node node) {
		System.out.println("\nNode " + id + " adding Node " + node.id + " to its queue.");
		this.queue.add(node);
	}

	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				if (isRequestingCS) {
					if (!this.queue.contains(this)) {
						this.add(this);
						if (this.parent != null && !hasSentRequestToParent) {
							this.parent.sendRequest(this);
							hasSentRequestToParent = true;
						}
					}
				}
				if (isPrivileged && !isInCS) {
					if (!this.queue.isEmpty()) {
						Node nextInQueue = this.queue.poll();
						nextInQueue.hasSentRequestToParent = false;
						System.out.println("\nPopped Node " + nextInQueue.id + " from the queue of Node " + id);

						if (nextInQueue.equals(this)) {
							System.out.println("\nPooped node is same as the current privilledged node, going into CS");
							isRequestingCS = false;
							isInCS = true;
							startCS();
						}
						else {
							if (!this.queue.isEmpty()) {
								nextInQueue.add(this);
							}
							System.out.println("\nPassing token from Node " + id + " to Node " + nextInQueue.id);
	
							nextInQueue.isPrivileged = true;
							this.isPrivileged = false;
							this.parent = nextInQueue;
							nextInQueue.parent = null;
						}
					}
				}
			}
		}
	}

	private void sendRequest(Node node) {
		System.out.println("\nNode " + id + " has recieved request from Node " + node.id);

		if (!this.queue.contains(node)) {
			this.add(node);
		}

		if (!hasSentRequestToParent && this.parent != null) {
			this.parent.sendRequest(this);
			hasSentRequestToParent = true;
		}
	}

	private void startCS() {
		Random random = new Random(System.currentTimeMillis());
		int randomDelay = random.nextInt(3, 11);

		System.out.println("\nNode " + id + " going into CS for " + randomDelay + "s\n");

		Timer countdownTimer = new Timer(1000, new ActionListener() {
			int remainingTime = randomDelay;

			@Override
			public void actionPerformed(ActionEvent e) {
				remainingTime--;

				setText(id + "," + remainingTime);
			}
		});

		Timer timer = new Timer(randomDelay * 1000, new ActionListener() {

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
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof Node))
			return false;

		Node oNode = (Node) obj;
		return oNode.id.equalsIgnoreCase(this.id);
	}

	@Override
	public String toString() {
		String outString = "";

		outString += "\n";
		outString += "\nid: " + id;
		outString += "\nisPrivilledged: " + isPrivileged;
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
