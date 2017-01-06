import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Sender {
    public static void main(String args[]) throws Exception {
        Scanner scanner = new Scanner(System.in);
        //1. Ask a file from user
		System.out.print("Please enter a path of file: ");
		String fileName = scanner.nextLine();
		System.out.println("Sending the file...");
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName("127.0.0.1");
        File f = new File(fileName);
        int flength = (int)f.length();
        int packetNumber = 0;
        int ackPacketNum = 0;
        boolean isLastFlag = false;
        //2. Convert file into byte array
        InputStream input = new FileInputStream(f);
        byte[] byteArray = new byte[flength];
        input.read(byteArray);
        
        for (int offset=0; offset<byteArray.length; offset+=1021) {
        	packetNumber ++;
            //3. Split byte array as 1024 bytes pieces
            byte[] m = new byte[1024]; //M=MESSAGE
            m[0] = (byte)(packetNumber >> 8); //FIRST BYTE OF THE MESSAGE IS SEQ NUM
            m[1] = (byte)(packetNumber); //SECOND BYTE OF THE MESSAGE IS SEQ NUM
            //LAST PACKET?
            if ((offset+1021) >= byteArray.length) {
            	isLastFlag = true;
                m[2] = (byte) 0xff;
            } 
            else {
            	isLastFlag = false;
                m[2] = (byte) 0x00;
            }

            if (!isLastFlag) {
                for (int index=0; index<=1020; index++) 
                    m[index+3] = byteArray[offset+index];
            }
            else if (isLastFlag) { //LAST MESSAGE
                for (int index=0;  index<(byteArray.length-offset); index++)
                    m[index+3] = byteArray[offset+index];			
            }
            //4. Set every pieces as datagram package
            DatagramPacket sendPacket = new DatagramPacket(m, m.length, address, 7777);
            //5. Send a package 
            socket.send(sendPacket); 
            System.out.println("Sending Packet... Packet number is " + packetNumber);
            boolean isAckCorrect = false;
            boolean isAckReceived = false;
            while (!isAckCorrect)
                if(controlAck(packetNumber, isAckCorrect, 
                		isAckReceived, ackPacketNum, socket, sendPacket)==-1) break;
        }
        //8. Display “file has been sent messages” after all parts are transferred.
        socket.close();
        System.out.println("File has been sent.");
    }
    public static int controlAck(int packetNumber, boolean isAckCorrect, boolean isAckReceived,
    		int ackPacketNum, DatagramSocket socket, DatagramPacket sendPacket) throws IOException{
    	byte[] ackArray = new byte[2];
        DatagramPacket ack = new DatagramPacket(ackArray, ackArray.length);
        socket.receive(ack);
        ackPacketNum = ((ackArray[0] & 0xff) << 8) + (ackArray[1] & 0xff);
        isAckReceived = true;
        
        //6. Wait for Acknowledgement message
        // THERE IS ACK >> NEXT PACKET ARE SENT
        if ((ackPacketNum == packetNumber) && (isAckReceived)) {	
        	isAckCorrect = true;
            System.out.println("ACK RECEIVED.");
            return -1;
        } 
        // RESEND PACKET
        else {
            socket.send(sendPacket);
            System.out.println("***************Resending Packet... Packet number is " + packetNumber);
        }
		return 0;
    }
}
