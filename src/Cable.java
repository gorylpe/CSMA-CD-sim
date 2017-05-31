import java.util.ArrayList;
import java.util.Collections;

public class Cable {

    enum Signal{
        NONE,
        DATA,
        BROKEN_DATA,
        JAM
    }

    private Signal[] transmitLeft;
    private Signal[] transmitRight;

    private int slotTime;
    private int length;

    public Cable(int n){
        transmitLeft = new Signal[n];
        transmitRight = new Signal[n];
        for(int i = 0; i < n; ++i){
            transmitLeft[i] = Signal.NONE;
            transmitRight[i] = Signal.NONE;
        }

        slotTime = 2 * n;
        length = n;
    }

    public int getSlotTime(){
        return slotTime;
    }

    public int getLength(){
        return length;
    }

    public Signal getSignalAtPoint(int i){
         if(transmitLeft[i] == Signal.JAM || transmitRight[i] == Signal.JAM){
            return Signal.JAM;
        } else if(transmitLeft[i] == Signal.BROKEN_DATA || transmitRight[i] == Signal.BROKEN_DATA){
            return Signal.BROKEN_DATA;
        } else if(transmitLeft[i] == Signal.DATA || transmitRight[i] == Signal.DATA){
            return Signal.DATA;
        }
        return Signal.NONE;
    }

    public void sendDataAtPoint(int i){
        transmitLeft[i] = Signal.DATA;
        transmitRight[i] = Signal.DATA;
    }

    public void sendJamAtPoint(int i){
        transmitLeft[i] = Signal.JAM;
        transmitRight[i] = Signal.JAM;
    }

    public void propagate(){
        for(int i = 0; i < transmitLeft.length - 1; ++i){
            transmitLeft[i] = transmitLeft[i + 1];
        }
        transmitLeft[transmitLeft.length - 1] = Signal.NONE;

        //check for collisions on transmit left
        for(int i = 0; i < transmitLeft.length; ++i){
            if(transmitLeft[i] == Signal.DATA && (transmitRight[i] == Signal.DATA || transmitRight[i] == Signal.BROKEN_DATA)){
                transmitLeft[i] = Signal.BROKEN_DATA;
                transmitRight[i] = Signal.BROKEN_DATA;
            }
        }

        for(int i = transmitRight.length - 1; i > 0; --i){
            transmitRight[i] = transmitRight[i - 1];
        }
        transmitRight[0] = Signal.NONE;

        //check for collisions on transmit right
        for(int i = 0; i < transmitRight.length; ++i){
            if(transmitRight[i] == Signal.DATA && (transmitLeft[i] == Signal.DATA || transmitLeft[i] == Signal.BROKEN_DATA)){
                transmitLeft[i] = Signal.BROKEN_DATA;
                transmitRight[i] = Signal.BROKEN_DATA;
            }
        }
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < transmitRight.length; ++i){
            if(transmitLeft[i] == Signal.DATA || transmitRight[i] == Signal.DATA) {
                stringBuilder.append("[*]");
            } else if(transmitLeft[i] == Signal.JAM || transmitRight[i] == Signal.JAM){
                stringBuilder.append("[J]");
            } else if(transmitLeft[i] == Signal.BROKEN_DATA || transmitRight[i] == Signal.BROKEN_DATA){
                stringBuilder.append("[B]");
            } else {
                stringBuilder.append("[ ]");
            }
        }
        return stringBuilder.toString();
    }
}