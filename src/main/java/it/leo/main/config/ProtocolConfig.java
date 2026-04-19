package it.leo.main.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import it.leo.main.config.enums.CommandType;
import it.leo.main.config.enums.OpCodeType;

public class ProtocolConfig {
    private static final Map<Byte, CommandType> COMMAND_TYPE = new HashMap<>();
    private static final Map<Byte, OpCodeType> OPCODE_TYPE = new HashMap<>();

    public static Byte getCommandByte(CommandType commandType) {
        COMMAND_TYPE.put((byte) 0x01, CommandType.CONNECT);
        COMMAND_TYPE.put((byte) 0x02, CommandType.QUERY);
        return COMMAND_TYPE.entrySet()
              .stream()
              .filter(entry -> Objects.equals(entry.getValue(), commandType))
              .map(Map.Entry::getKey)
              .collect(Collectors.toList()).getFirst();
    }

    public static CommandType getCommandType(Byte bytec) {
        COMMAND_TYPE.put((byte) 0x01, CommandType.CONNECT);
        COMMAND_TYPE.put((byte) 0x02, CommandType.QUERY);
        return COMMAND_TYPE.get(bytec);
    }

    public static Byte getOpCodeByte(OpCodeType opCodeType) {
        OPCODE_TYPE.put((byte) 0x01, OpCodeType.REQUEST);
        OPCODE_TYPE.put((byte) 0x02, OpCodeType.RESPONSE);
        return OPCODE_TYPE.entrySet()
              .stream()
              .filter(entry -> Objects.equals(entry.getValue(), opCodeType))
              .map(Map.Entry::getKey)
              .collect(Collectors.toList()).getFirst();
    }

    public static OpCodeType getOpCodeType(Byte bytec) {
        OPCODE_TYPE.put((byte) 0x01, OpCodeType.REQUEST);
        OPCODE_TYPE.put((byte) 0x02, OpCodeType.RESPONSE);
        return OPCODE_TYPE.get(bytec);
    }
}