package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.StringTokenizer;

public class Main {

    public static void main(String[] args) {

        System.out.println("Starting Server");

        String clientMessage;

        LocalDateTime rawTimestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String timestamp = "Timestamp: " + rawTimestamp.format(formatter);

        String HTTP200 = "Server message: HTTP - 200 - OK";

        String HTTP404 = "Server message: HTTP - 404 - BAD REQUEST";

        String fileLength = "File Length: ";

        String mimeTypeMessage = "Mimetype: ";

        try {
            //Initialize Connection
            ServerSocket welcomeSocket = new ServerSocket(8080);
            System.out.println("\nSocket initialized");

            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Awaiting connection");

            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("\nConnection successful");

            //READ MESSAGE
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientMessage = inFromClient.readLine();
            System.out.println("\nClient Message: " + clientMessage);

            //Server Message
            outToClient.writeBytes("HTTP - 200 - OK\n");

            String ressourcePath = "C:/Users/frede/IdeaProjects/MandatoryHTTPServerAssignment/src/com/company/ressources/";
            StringTokenizer tokenizer = new StringTokenizer(clientMessage);

            String clientURLParameter;
            String mimeType;
            clientURLParameter = tokenizer.nextToken();
            clientURLParameter = tokenizer.nextToken();

            File returnFile = new File(ressourcePath + clientURLParameter);
            if (clientURLParameter.equals("/")) {
                returnFile = new File(ressourcePath + clientURLParameter + "index.html");
                System.out.println(HTTP200);

                System.out.println(timestamp);
                outToClient.writeBytes(timestamp);

                System.out.println(fileLength + returnFile.length());
                outToClient.writeBytes(fileLength + returnFile.length());

                mimeType = URLConnection.guessContentTypeFromName(returnFile.getName());
                System.out.println(mimeTypeMessage + mimeType);
                outToClient.writeBytes(mimeTypeMessage + mimeType);
            }
            if (!returnFile.exists()) {
                returnFile = new File(ressourcePath + "http400.html");
                System.out.println(HTTP404);

                System.out.println(timestamp);
                outToClient.writeBytes(timestamp);

                System.out.println(fileLength + returnFile.length());
                outToClient.writeBytes(fileLength + returnFile.length());

                mimeType = URLConnection.guessContentTypeFromName(returnFile.getName());
                System.out.println(mimeTypeMessage + mimeType);
                outToClient.writeBytes(mimeTypeMessage + mimeType);
            }

            FileInputStream convertReturnFileToBytes = new FileInputStream(returnFile);

            boolean whileCont = true;
            int fileFound = 0;
            byte bytearr[] = new byte[10];

            while (whileCont) {
                fileFound = convertReturnFileToBytes.read(bytearr);
                if (fileFound == -1) {
                    whileCont = false;
                }
                else {
                    outToClient.write(bytearr);
                }
            }

            connectionSocket.close();
            welcomeSocket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
