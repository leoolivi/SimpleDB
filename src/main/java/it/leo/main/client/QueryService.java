package it.leo.main.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import it.leo.main.config.ProtocolConfig;
import it.leo.main.config.enums.CommandType;
import it.leo.main.config.enums.OpCodeType;
import it.leo.main.protocol.DbConnection;
import it.leo.main.protocol.Packet;
import it.leo.main.protocol.QueryCommandFactory;
import it.leo.main.protocol.data.request.GetRequest;
import it.leo.main.protocol.utils.SerializerUtil;
import it.leo.main.server.DBResponse;

public class QueryService {
    private final Scanner scanner;
    private final DbConnection connection;

    public QueryService(DbConnection connection) {
        this.scanner = new Scanner(System.in);
        this.connection = connection;
    }

    private void showHelp() {
        System.out.println("---------- Help message ----------\n");
        QueryCommandFactory.ALL.forEach(command -> {
            System.out.println(String.format("- %s: %s", command.getName(), command.getDescription()));
        });
        System.out.print("\n\n");
    }

    private void serializeAndSend(String query, DataOutputStream out, DataInputStream in) throws IOException, ClassNotFoundException {
        if (out == null) {
            System.err.println("Connection Client socket is null");
            return;
        }

        var chunks = Arrays.stream(query.split("[\s]")).toList();
        switch (chunks.getFirst().toLowerCase()) {
            case "get" -> {
                var req = new GetRequest(chunks.get(1));
                var packet = Packet.builder()
                        .command(ProtocolConfig.getCommandByte(CommandType.QUERY))
                        .opcode(ProtocolConfig.getOpCodeByte(OpCodeType.REQUEST))
                        .payload(SerializerUtil.convertObjectToBytes(req))
                        .build();
                packet.writeTo(out);
            }
            default -> System.err.println("Unexpected command "+chunks.getFirst().toLowerCase());
            
        }
        while (true) {
            if (in.available() > 0) {
                //byte command = in.readByte();
                //byte opcode = in.readByte();
                int len = in.readInt();
                byte[] payloadBytes = in.readNBytes(len);

                System.out.println((DBResponse<String, String>) SerializerUtil.convertBytesToObject(payloadBytes));
                break;
            }
        }
        
    }

    @Deprecated
    private void sendPacket(String text) throws IOException {
        if (this.connection.getClientSocket() != null) {
            connection.getPrintWriter().println(text);
            connection.getPrintWriter().flush();
            String line;
            while(true) {
                line = connection.getBufferedReader().readLine();
                if (line.equals("EOF")) break;
                System.out.println(line);
            }
        } else {
            System.err.println("Connection Client socket is null");
        }
    }

    public void start() throws IOException, ClassNotFoundException {
        // Initializing variables
        String input;

        // Showing first help message
        showHelp();
        while(true) {
            System.out.print("> (help to show command list): ");
            input = scanner.nextLine();
            List<String> chunks = Arrays.stream(input.split("[\s]")).toList();

            switch (chunks.getFirst().toLowerCase()) {
                case "help" -> showHelp();
                case "exit" -> System.exit(-1);
                default -> serializeAndSend(input, connection.getOutputStream(), connection.getInputStream());
            }
        }
    }
}
