import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class Main extends JFrame {
    private List<Node> nodeList = new ArrayList<>();

    public Main() {
        setTitle("Raymond algorithm");
        setSize(900, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
    }

    private void init() {
        JFileChooser fc = new JFileChooser();
        int res = fc.showOpenDialog(this);

        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                String content = new String(Files.readAllBytes(fc.getSelectedFile().toPath()));

                // TODO LOGIC
            }
            catch (IOException err) {
                err.printStackTrace();
            }
        }
    }
}