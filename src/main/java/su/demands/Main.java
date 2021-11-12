package su.demands;

import lombok.SneakyThrows;
import su.demands.asciiTable.AsciiTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;

public class Main {

    private static Controller controller = null;

    @SneakyThrows
    public static void main(String[] args) {
        JFrame form = new JFrame("HalsteadMetrix");

        form.setResizable(false);

        form.setSize(380, 240);
        form.setLayout(null);
        form.setLocation(300,200);
        form.getContentPane().setBackground(Color.decode("#EEEEEE"));

        form.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        form.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(form,
                        "Close?", "Exit", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(1);
                    form.dispose();
                }
            }
        });

        JLabel labelKey = new JLabel("File:");
        labelKey.setBounds(25,2,50, 20);
        form.getContentPane().add(labelKey);

        final JTextArea fileUrlTextArea = new JTextArea(10, 1);
        fileUrlTextArea.setBounds(50,2,280,20);
        form.getContentPane().add(fileUrlTextArea);

        JButton button = new JButton("...");
        button.setBounds(330,2,20, 20);

        button.addActionListener(e -> {
            JFileChooser fileopen = new JFileChooser();
            int ret = fileopen.showDialog(null, "Open file");
            if (ret == JFileChooser.APPROVE_OPTION) {
                String path = fileopen.getSelectedFile().toURI().getPath();
                fileUrlTextArea.setText(path);
            }
        });

        form.getContentPane().add(button);

        JLabel EText = new JLabel("Условие на разработку программы:");
        EText.setBounds(25,25,230, 20);
        form.getContentPane().add(EText);

        JLabel E = new JLabel("");
        E.setBounds(235,25,230, 20);
        form.getContentPane().add(E);

        JLabel VText = new JLabel("Объем программы:");
        VText.setBounds(25,40,130, 20);
        form.getContentPane().add(VText);

        JLabel V = new JLabel("");
        V.setBounds(140,40,130, 20);
        form.getContentPane().add(V);

        JLabel NText = new JLabel("Длина программы:");
        NText.setBounds(25,55,130, 20);
        form.getContentPane().add(NText);

        JLabel N = new JLabel("");
        N.setBounds(137,55,130, 20);
        form.getContentPane().add(N);

        JLabel nText = new JLabel("Словарь:");
        nText.setBounds(25,70,70, 20);
        form.getContentPane().add(nText);

        JLabel n = new JLabel("");
        n.setBounds(80,70,70, 20);
        form.getContentPane().add(n);

        JLabel LText = new JLabel("Уровень программы:");
        LText.setBounds(25,85,130, 20);
        form.getContentPane().add(LText);

        JLabel L = new JLabel("");
        L.setBounds(148,85,130, 20);
        form.getContentPane().add(L);

        JLabel DText = new JLabel("Сложность программы:");
        DText.setBounds(25,100,140, 20);
        form.getContentPane().add(DText);

        JLabel D = new JLabel("");
        D.setBounds(166,100,140, 20);
        form.getContentPane().add(D);

        JButton buttonCalc = new JButton("Calculate");
        buttonCalc.setBounds(140,130,100, 20);

        buttonCalc.addActionListener(e -> {
            if (fileUrlTextArea.getText().isEmpty()) {
                JOptionPane.showMessageDialog(form,
                        "You have not selected a file!","ERROR",
                        JOptionPane.ERROR_MESSAGE,null);
            } else {
                controller = new Controller(fileUrlTextArea.getText());
                E.setText(String.valueOf(controller.getConditionDevProgram()));
                V.setText(String.valueOf(controller.getProgramScope()));
                N.setText(String.valueOf(controller.getProgramLength()));
                n.setText(String.valueOf(controller.getProgramDictionary()));
                L.setText(String.valueOf(controller.getProgramLevel()));
                D.setText(String.valueOf(controller.getProgramDifficulties()));
            }
        });

        form.getContentPane().add(buttonCalc);

        JButton buttonDetails = new JButton("Details");
        buttonDetails.setBounds(145,160,90, 20);

        buttonDetails.addActionListener(e -> {
            if (controller == null) {
                JOptionPane.showMessageDialog(form,
                        "There is no data for additional information, use the calculation!","ERROR",
                        JOptionPane.ERROR_MESSAGE,null);
            } else {
                int confirm = JOptionPane.showOptionDialog(form,
                        String.format("""
                                        Общее число операторов: %d
                                        Число различающихся простых операторов: %d
                                        Общее число операндов: %d
                                        Число различающихся простых операндов: %d
                                        """,
                                controller.getCountOperators(), controller.getCountUniqueOperators(),
                                controller.getCountOperands(), controller.getCountUniqueOperands()),
                        "Details", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE,null, new String[] {"OK","more.."},null);
                if (confirm == JOptionPane.NO_OPTION) {

                    int lineCount = Math.max(controller.getFoundUniqueOperands().size(), controller.getFoundUniqueOperators().size());
                    String[][] lines = new String[lineCount][4];

                    for (int i = 0; i < lineCount; i++) {
                        if (controller.getFoundUniqueOperators().size() <= i) {
                            lines[i][0] = "";
                            lines[i][1] = "";
                        } else {
                            lines[i][0] = controller.getFoundUniqueOperators().get(i);
                            lines[i][1] = String.valueOf(Collections.frequency(controller.getFoundOperators(),controller.getFoundUniqueOperators().get(i)));
                        }
                        if (controller.getFoundUniqueOperands().size() <= i) {
                            lines[i][2] = "";
                            lines[i][3] = "";
                        } else {
                            lines[i][2] = controller.getFoundUniqueOperands().get(i);
                            lines[i][3] = String.valueOf(Collections.frequency(controller.getFoundOperands(),controller.getFoundUniqueOperands().get(i)));
                        }
                    }

                    JOptionPane.showMessageDialog(form,
                            AsciiTable.getTable(AsciiTable.BASIC_ASCII, new String[]{"Operators", "Count", "Operands", "Count"}, lines).replaceAll(" ","  ").replaceAll("--","---"),
                            "MORE details..",JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        form.getContentPane().add(buttonDetails);

        form.setVisible(true);
    }
}
