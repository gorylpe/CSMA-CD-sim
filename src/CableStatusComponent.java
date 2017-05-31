import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;


public class CableStatusComponent extends JComponent {

    private Cable cable;
    private int unitHeight;

    public CableStatusComponent(Cable cable, int unitHeight){
        setPreferredSize(new Dimension(cable.getLength() * unitHeight + 2 * unitHeight, 5 * unitHeight));
        this.cable = cable;
        this.unitHeight = unitHeight;
    }

    public void iterate(){
        cable.propagate();
    }

    public Point getMountPoint(int i){
        Point point = getLocation();
        return new Point(point.x + (i + 1) * unitHeight + unitHeight / 2, point.y + unitHeight * 2);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        final BasicStroke stroke = new BasicStroke(2.0f);
        g2d.setStroke(stroke);
        Rectangle2D rectangle2D;
        for(int i = 0; i < cable.getLength(); ++i){
            rectangle2D = new Rectangle2D.Double((i + 1) * unitHeight, 2 * unitHeight, unitHeight, unitHeight);
            switch(cable.getSignalAtPoint(i)){
                case DATA:
                    g2d.setColor(Color.GREEN);
                    break;
                case BROKEN_DATA:
                    g2d.setColor(Color.YELLOW);
                    break;
                case JAM:
                    g2d.setColor(Color.RED);
                    break;
                case NONE:
                    g2d.setColor(Color.WHITE);
                    break;
            }
            g2d.fill(rectangle2D);
            g2d.setColor(Color.BLACK);
            g2d.draw(rectangle2D);
        }
    }

    public Cable getCable() {
        return cable;
    }
}
