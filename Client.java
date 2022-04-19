import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {
    public static void main(String[] args) throws IOException {
        // creates an arrayList for the received packets
        ArrayList<String> packets = new ArrayList<>();

        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        //creates a client socket with input and output streams
        try (
                Socket client = new Socket(hostName, portNumber);
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()))
        ) {
            //prints that it connected to the server
            System.out.println("Hey! I'm connected.");
            //notifies the server that it is connected
            writer.println("hi");

            //determines how many packages the client should prepare for
            int packetNum = 0;
            while(packetNum <= 0){
                packetNum = Integer.parseInt(reader.readLine());
                writer.println("0");
                System.out.println("Packet Amount not Received.");
            }
            writer.println("1");
            System.out.println("Packet Amount Received.");

            //places a "#" as a placeholder for every packet it intends to receive
            for(int i = 0; i < packetNum; i++){
                packets.add("#");
            }

            //while there are still empty values in the arraylist, the client asks the server for packets,
            //retrieves the packets and places them at the proper index in the arrayList
            boolean notDone = true;
            while(notDone){
                // asks server for specific packets, based on which arrayList values are still "#"
                StringBuilder request = new StringBuilder("Asking Server for Packets: ");
                for(int i = 1; i <= packets.size(); i++){
                    if(packets.get(i - 1).equals("#")){
                        // ask server for this packet
                        writer.println(i);
                        request.append(i);
                        request.append(" ");
                    }
                }
                writer.println("doneReq");
                System.out.println(request);
                System.out.println("Finished Requesting Packets");

                //creates an arraylist to hold the packets received from the server
                ArrayList<String> received = new ArrayList<>();

                //reads which packets came through from the server
                StringBuilder pReceived = new StringBuilder("Received Packets: ");
                doubleloop:
                while(true){
                    String read = reader.readLine();
                    if(read.equals("sentAll")){
                        break doubleloop;
                    }
                    received.add(read);
                    if(read.length() == 3){
                        pReceived.append(read.charAt(0));
                        pReceived.append(" ");
                    }else if(read.length() == 4){
                        pReceived.append(read, 0, 2);
                        pReceived.append(" ");
                    }
                }
                System.out.println(pReceived);

                //places all the packets in the right spot in the packets arraylist
                String subs;
                int ind;
                for(String rec : received){
                    if(rec.length() == 3){
                        ind = Integer.parseInt(rec.substring(0,1));
                        subs = rec.substring(1);
                    }else{
                        ind = Integer.parseInt(rec.substring(0, 2));
                        subs = rec.substring(2);
                    }
                    packets.set(ind - 1, subs);
                }
                System.out.println("Placed the Received Packets in the Right Order");

                //checks whether there are still empty values in the array
                boolean hasHash = false;
                for(String packet : packets){
                    if (packet.equals("#")) {
                        hasHash = true;
                        break;
                    }
                }

                //if there are no more empty spots, sets the notDone boolean to false
                if(!hasHash){
                    notDone = false;
                    writer.println("done");
                    System.out.println("Received All Packets!");
                }else{
                    writer.println("notDone");
                    System.out.println("Still Missing Packets!");
                }
            }

            //assembles the total message and prints it
            StringBuilder totalMessage = new StringBuilder("The Message is: ");
            for (String packet : packets) {
                totalMessage.append(packet);
            }
            System.out.println(totalMessage);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
