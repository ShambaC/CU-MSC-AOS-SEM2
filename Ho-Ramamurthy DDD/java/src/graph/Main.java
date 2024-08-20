package graph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.Timer;

public class Main {
	
	public static List<Site> siteList = new ArrayList<>();
	
	public static void main(String[] args) {
		// Log the output to a file
        try {
            PrintStream fileStream = new PrintStream("outputLog.txt");
            System.setOut(fileStream);
        }
        catch (FileNotFoundException err) {
            err.printStackTrace();
        }

		Random random = new Random();

		int no_of_sites = random.nextInt(2, 3);
		System.out.println("\nNumber of Sites: " + no_of_sites);

		int resource_list_len, process_list_len;

		for (int i = 1; i <= no_of_sites; i++) {
			Site site = new Site(i);

			resource_list_len = random.nextInt(2, 4);
			System.out.println("\nNumber of resources in Site " + i + ": " + resource_list_len);
			for (int j = 1; j <= resource_list_len; j++) {
				Resource resource = new Resource(j, i);
				site.resourceList.add(resource);
			}

			process_list_len = random.nextInt(2, 4);
			System.out.println("\nNumber of processes in Site " + i + ": " + process_list_len);
			for (int j = 1; j <= process_list_len; j++) {
				Node node = new Node(j, i);
				site.nodeList.add(node);
			}
			
			siteList.add(site);
		}
		
		ControlSite cs = new ControlSite(siteList);
		
		for (Site site : siteList) {
            Thread t = new Thread(site);
            t.start();
        }

		Timer timer = new Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cs.createLWFG();
				cs.createGWFG();

				boolean isDeadlock = cs.isInDeadlock();
				System.out.println("Checking for deadlock. Deadlock detected: " + isDeadlock);

				if (isDeadlock) {
					JOptionPane.showMessageDialog(null, "DeadLock", "Information", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		});

		timer.setRepeats(true);
		timer.start();

	}
}