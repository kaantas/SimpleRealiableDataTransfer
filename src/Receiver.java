import java.io.*;
import java.net.*;

public class Receiver {
    public static void main(String args[]) throws Exception {
        System.out.println("Waiting to receive...");
        DatagramSocket socket = new DatagramSocket(7777);
        int packetNumber = 0;
        int lastPacketNumber = 0;
        boolean isLastFlag = false;
        boolean isLastMessage = false;
        //1. Listen for packages
        //2. Receive packages 
        while (!isLastMessage) {
            byte[] m = new byte[1024]; //M=MESSAGE
            byte[] byteArray = new byte[1021];
            DatagramPacket receivedPacket = new DatagramPacket(m, m.length);
            socket.setSoTimeout(0);
            socket.receive(receivedPacket);
            m = receivedPacket.getData();
            InetAddress address = receivedPacket.getAddress();
            int port = receivedPacket.getPort();
            //RETRIEVE SEQ NUM
            packetNumber = ((m[0] & 0xff) << 8) + (m[1] & 0xff);
            //LAST MESSAGE?
            if ((m[2] & 0xff) == 0xff) isLastFlag = true;//***
            else isLastFlag = false;
            
            if (packetNumber == (lastPacketNumber + 1)) {
            	lastPacketNumber = packetNumber;
                //RETRIEVE DATA FROM MESSAGES
                for (int i=3; i < 1024; i++) byteArray[i-3] = m[i];
               
                System.out.println("Received Packet. Packet number is " + lastPacketNumber);
                //3. Send Acknowledgement message
                sendAck(lastPacketNumber, socket, address, port);
            } 
            else {
                System.out.println("DISCARDING. RESENDING ACK...");
                sendAck(lastPacketNumber, socket, address, port);
            }
            //LAST MESSAGE?
            if (isLastFlag) {
                isLastMessage = false;
                break;
            }
        }
        //6. Display “file has been received” after all parts are transferred.
        socket.close();
        System.out.println("File has been received.");
    }
    public static void sendAck(int lastPacketNumber, DatagramSocket socket,
    		InetAddress address, int port) throws IOException {
        byte[] ackArray = new byte[2];
        ackArray[0] = (byte)(lastPacketNumber >> 8);
        ackArray[1] = (byte)(lastPacketNumber);
        DatagramPacket ack = new DatagramPacket(ackArray, ackArray.length, address, port);
        socket.send(ack);
        System.out.println("SENT ACK.");
    }
}
