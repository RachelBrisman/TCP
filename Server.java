import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Server {
    public static void main(String[] args) throws IOException {

        //creates a message and an arrayList that will hold the packets
        String message = "Today is a great day for a great day! :)";
        ArrayList<String> packets = new ArrayList<>();

        //divides the message into packets
        int counter = 0;
        for(int i = 0; i < message.length() ; i+=2){
            String packet = String.valueOf(counter + 1);
            packets.add(counter,packet + message.substring(i,i+2));
            counter++;
        }

        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        //creates server socket with input and output streams
        try (
                ServerSocket server = new ServerSocket(portNumber);
                Socket clientSocket = server.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {

            //sends the client how many packets to expect with 80% probability
            //the client keeps waiting until it receives a packet that can be parsed into an int
            loops:
            while(true){
                String inputLine = reader.readLine();
                if(inputLine.equals("1")){
                    break loops;
                }else{
                    double rand = Math.random() * 10;
                    if(rand < 8){
                        writer.println(packets.size());
                    }
                    System.out.println("Sending Packet Amount...");
                }
            }
            System.out.println("Client Received Packet Amount");

            //this while loop runs until the client notifies the server that all packets have been received
            boolean stillMissing = true;
            while(stillMissing){

                //this arrayList will store which packet numbers the client is still missing
                ArrayList<Integer> missing = new ArrayList<>();

                //this loop retrieves which packets the client still want
                //it runs until the client notifies the server that it is done requesting
                outerloop:
                while(true){
                    String reqs = reader.readLine();
                    if(reqs.equals("doneReq")){
                        break outerloop;
                    }else{
                        missing.add(Integer.parseInt(reqs));
                    }
                }
                System.out.println("Got Client's Packet Requests");

                //make an array of packets to resend, shuffles it
                ArrayList<String> resend = new ArrayList<>();
                //this loop adds the packets corresponding to the packet numbers that the client requested
                for(Integer num: missing){
                    resend.add(packets.get(num - 1));
                }
                Collections.shuffle(resend);
                if(resend.size() != packets.size()) {
                    System.out.println("Shuffled All Remaining Packets");
                }

                //send the remaining packets with 80% probability
                StringBuilder sending = new StringBuilder("Sent the Client Packets: ");
                for (String s : resend) {
                    double rand = Math.random() * 10;
                    if (rand < 8) {
                        writer.println(s);
                    }
                    if(s.length() == 3){
                        sending.append(s.charAt(0));
                        sending.append(" ");
                    }else if(s.length() == 4){
                        sending.append(s, 0, 2);
                        sending.append(" ");
                    }
                }
                //notifies the client that all packets were sent.
                writer.println("sentAll");
                System.out.println(sending);

                //determines whether the client received all the packets
                if(reader.readLine().equals("done")){
                    stillMissing = false;
                }
            }

            //Prints when the client confirms that it received all the packages
            System.out.println("Client Received All the Packets Successfully!");

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}