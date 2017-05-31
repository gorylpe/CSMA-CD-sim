import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Piotr on 23.05.2017.
 */
public class ComputerComponent extends JComponent {

    private Computer computer;

    private JTextArea computerState;

    public ComputerComponent(Computer computer, int width){

        if(width < 100)
            width = 100;

        setPreferredSize(new Dimension(width, width));
        setMinimumSize(new Dimension(100, 100));

        this.computer = computer;

        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                null));

        JButton sendLongDataButton = new JButton("Send long data");
        sendLongDataButton.setBounds(0, 0, width, 30);
        sendLongDataButton.setMargin(new Insets(0,0,0,0));
        sendLongDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                computer.sendLongData();
            }
        });
        add(sendLongDataButton);
        sendLongDataButton.setVisible(true);

        JButton sendShortDataButton = new JButton("Send short data");
        sendShortDataButton.setBounds(0, 30, width, 30);
        sendShortDataButton.setMargin(new Insets(0,0,0,0));
        sendShortDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                computer.sendShortData();
            }
        });
        add(sendShortDataButton);
        sendShortDataButton.setVisible(true);

        computerState = new JTextArea();
        computerState.setBounds(0, 60, width, width - 60);
        computerState.setText("Initialized");
        computerState.setEditable(false);
        computerState.setLineWrap(true);
        computerState.setWrapStyleWord(true);
        add(computerState);
        computerState.setVisible(true);
    }

    public void iterate(){
        computer.iterate();
    }

    public int getMountPointNumber(){
        return computer.getMountPointNumber();
    }

    public Point getMountPoint(){
        Point location = getLocation();
        return new Point(location.x + getWidth() / 2, location.y + getHeight() - getWidth() / 12);
    }

    public void updateComputerState() {
        computerState.setText(computer.getState());
        computerState.repaint();
    }
}
