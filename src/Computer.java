import java.util.Random;

public class Computer {

    private Random random;

    private Cable cable;
    private int mountPoint;

    private int lastDataLength;
    private int dataLeftForSend;
    private int iterationsWaiting;
    private int resendAttemp;

    private String stateText;

    enum State{
        LISTENING,
        SENDING_DATA,
        WAITING_FOR_SEND,
        SENDING_JAM
    }

    private State state;

    public Computer(Cable cable, int mountPoint){
        random = new Random();

        this.cable = cable;
        this.mountPoint = mountPoint;
        lastDataLength = 0;
        dataLeftForSend = 0;
        iterationsWaiting = 0;
        resendAttemp = 0;
        state = State.LISTENING;
        stateText = "Listening";
    }

    public int getMountPointNumber(){
        return mountPoint;
    }

    public String getState() {
        return stateText;
    }

    public void sendLongData(){
        if (cable.getSignalAtPoint(mountPoint) == Cable.Signal.NONE) {
            lastDataLength = cable.getSlotTime();
            dataLeftForSend = cable.getSlotTime();

            state = State.WAITING_FOR_SEND;
            iterationsWaiting = 0;
            resendAttemp = 1;
        }
    }

    public void sendShortData(){
        if (cable.getSignalAtPoint(mountPoint) == Cable.Signal.NONE) {
            lastDataLength = cable.getSlotTime() / 4;
            dataLeftForSend = cable.getSlotTime() / 4;

            state = State.WAITING_FOR_SEND;
            iterationsWaiting = 0;
            resendAttemp = 1;
        }
    }

    private void sendData() {
        if(cable.getSignalAtPoint(mountPoint) == Cable.Signal.NONE){
            stateText = "Sending data";
            cable.sendDataAtPoint(mountPoint);
        } else {
            stateText = "Collision detected. Sending jam signal";
            state = State.SENDING_JAM;
            sendJam();
        }
    }

    private void goStandby(){
        state = State.LISTENING;
        stateText = "Listening";
    }

    private void sendJam() {
        cable.sendJamAtPoint(mountPoint);
    }

    private void setNextSendAttempt(){
        state = State.WAITING_FOR_SEND;
        //Exponential Backoff
        if(resendAttemp < 10)
            iterationsWaiting = random.nextInt((int)Math.ceil(Math.pow(2, resendAttemp))) * cable.getSlotTime();
        else{
            iterationsWaiting = random.nextInt((int)Math.ceil(Math.pow(2, 10))) * cable.getSlotTime();
        }
        if(resendAttemp > 16)
            state = State.LISTENING;
        stateText = "Chosen random " + Integer.toString(iterationsWaiting) + " time units waiting for next attempt";
        dataLeftForSend = lastDataLength;
        resendAttemp++;
    }

    private void waitToSend() {
        if (iterationsWaiting > 0) {
            stateText = "Waiting to send. Time units left: " + Integer.toString(iterationsWaiting);
            iterationsWaiting--;
        } else {
            if (cable.getSignalAtPoint(mountPoint) == Cable.Signal.NONE) {
                state = State.SENDING_DATA;
            } else {
                stateText = "Waiting for free cable to send";
            }
        }
    }

    private void listen(){
        if(cable.getSignalAtPoint(mountPoint) == Cable.Signal.DATA){
            stateText = "Received data";
        } else if(cable.getSignalAtPoint(mountPoint) == Cable.Signal.BROKEN_DATA){
            stateText = "Received broken data, but don't know its broken";
        } else if(cable.getSignalAtPoint(mountPoint) == Cable.Signal.JAM){
            stateText = "Received jam signal, discarding last data";
        } else {
            stateText = "Listening";
        }
    }

    public void iterate(){
        switch(state){
            case SENDING_DATA:{
                if(dataLeftForSend > 0) {
                    dataLeftForSend--;
                    sendData();
                } else {
                    goStandby();
                }
                break;
            }
            case SENDING_JAM:{
                if(dataLeftForSend > 0) {
                    dataLeftForSend--;
                    sendJam();
                } else {
                    setNextSendAttempt();
                }
                break;
            }
            case WAITING_FOR_SEND: {
                waitToSend();
                break;
            }
            case LISTENING:{
                listen();
                break;
            }
        }
    }
}
