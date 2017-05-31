import java.awt.*;

public class Main {

    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Arguments: cableLength mountPoint1 mountPoint2...");
            System.exit(0);
        }
        int cableLength = Integer.parseInt(args[0]);
        int[] mountPoints = new int[args.length - 1];
        for(int i = 0; i < args.length - 1; ++i){
            mountPoints[i] = Integer.parseInt(args[i+1]);
        }
        EventQueue.invokeLater(() -> new Thread(new MyFrame(cableLength, mountPoints)).start());
    }
}
