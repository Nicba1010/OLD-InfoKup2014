package infoKupProcess;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class GUI extends JFrame {

    private JPanel jpAcc = new JPanel();
    private JList<String> checkBoxesJList;

    GUI() {
        jpAcc.setLayout(new BorderLayout());
        String labels[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
        checkBoxesJList = new JList<String>(labels);

        checkBoxesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(checkBoxesJList);

        jpAcc.add(scrollPane);

        getContentPane().add(jpAcc);
        pack();
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                GUI cbl = new GUI();
                cbl.setVisible(true);
            }
        });
    }
}