import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;

/**
 * Created by Piotr on 22.05.2017.
 */
public class MyFrame extends JFrame implements Runnable {

    private CableStatusComponent cableStatusComponent;
    private ComputerComponent[] computerComponents;
    private ComputerConnectsGlassPane computerConnectsGlassPane;
    private ControlPanel controlPanel;

    private final int cableUnitHeight = 30;
    private final int computerWidth = 150;

    private long loopTimeInMs;

    private long iteration;

    public MyFrame(int cableLength, int[] mountPoints){
        super("CSMACD");

        loopTimeInMs = 128;
        iteration = 0;

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();


        Cable cable = new Cable(cableLength);
        cableStatusComponent = new CableStatusComponent(cable, cableUnitHeight);
        c.fill = GridBagConstraints.CENTER;
        c.weightx = 0.0;
        c.gridwidth = mountPoints.length;
        c.gridx = 0;
        c.gridy = 1;
        add(cableStatusComponent, c);

        computerComponents = new ComputerComponent[mountPoints.length];
        for(int i = 0; i < mountPoints.length; ++i){
            computerComponents[i] = new ComputerComponent(new Computer(cable, mountPoints[i]), computerWidth);
            c.fill = GridBagConstraints.CENTER;
            c.gridwidth = 1;
            c.weightx = 0.5;
            c.gridx = i;
            c.gridy = 0;
            add(computerComponents[i], c);
        }

        computerConnectsGlassPane = new ComputerConnectsGlassPane();
        setGlassPane(computerConnectsGlassPane);
        computerConnectsGlassPane.setVisible(true);

        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.CENTER;
        c.weightx = 0.0;
        c.gridwidth = mountPoints.length;
        c.gridx = 0;
        c.gridy = 2;
        controlPanel = new ControlPanel();
        controlPanel.setVisible(true);
        add(controlPanel, c);

        pack();
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void run() {
        while(true){
            long lastTime = System.currentTimeMillis();
            cableStatusComponent.iterate();

            for(int i = 0; i < computerComponents.length; ++i){
                computerComponents[i].iterate();
            }

            cableStatusComponent.repaint();
            for(int i = 0; i < computerComponents.length; ++i){
                computerComponents[i].updateComputerState();
            }

            iteration++;

            controlPanel.updateIterationsLabel();

            System.out.println(cableStatusComponent.getCable());

            try{
                Thread.sleep(loopTimeInMs - (System.currentTimeMillis() - lastTime));
            } catch(InterruptedException | IllegalArgumentException e){}
        }
    }

    class ComputerConnectsGlassPane extends JComponent{

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g.create();

            if(cableStatusComponent != null && computerComponents != null){
                for(int i = 0; i < computerComponents.length; ++i){
                    Point mountPoint = cableStatusComponent.getMountPoint(computerComponents[i].getMountPointNumber());
                    Point computerMid = computerComponents[i].getMountPoint();
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(2.0f));
                    g2d.draw(new Line2D.Double(mountPoint, computerMid));
                }
            }

            g.dispose();
        }
    }

    class ControlPanel extends JPanel{

        JLabel frameTimeLabel;
        JLabel iterationsLabel;

        public ControlPanel(){
            frameTimeLabel = new JLabel("");
            frameTimeLabel.setBounds(60, 0, 20, 20);
            frameTimeLabel.setText(Long.toString(loopTimeInMs) + "ms");

            JButton slowDownButton = new JButton("<");
            slowDownButton.setBounds(0, 0, 20, 20);
            slowDownButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(loopTimeInMs > 1){
                        loopTimeInMs /= 2;
                    }
                    frameTimeLabel.setText(Long.toString(loopTimeInMs) + "ms");
                    frameTimeLabel.repaint();
                }
            });
            slowDownButton.setVisible(true);
            add(slowDownButton);

            JButton speedUpButton = new JButton(">");
            speedUpButton.setBounds(20, 0, 20, 20);
            speedUpButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(loopTimeInMs < 1024){
                        loopTimeInMs *= 2;
                    }
                    frameTimeLabel.setText(Long.toString(loopTimeInMs) + "ms");
                    frameTimeLabel.repaint();
                }
            });
            speedUpButton.setVisible(true);
            add(speedUpButton);

            JLabel frameTimeTextLabel = new JLabel("Frame time:");
            frameTimeTextLabel.setBounds(40, 0, 20, 20);
            frameTimeTextLabel.setVisible(true);
            add(frameTimeTextLabel);

            frameTimeLabel.setVisible(true);
            add(frameTimeLabel);

            JLabel iterationsTextLabel = new JLabel("Iterations:");
            iterationsTextLabel.setBounds(80, 0, 20, 20);
            iterationsTextLabel.setVisible(true);
            add(iterationsTextLabel);

            iterationsLabel = new JLabel("");
            iterationsLabel.setBounds(100, 0, 20, 20);
            iterationsLabel.setText(Long.toString(iteration));
            iterationsLabel.setVisible(true);
            add(iterationsLabel);
        }

        public void updateIterationsLabel(){
            iterationsLabel.setText(Long.toString(iteration));
            iterationsLabel.repaint();
        }
    }
}
