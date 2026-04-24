package it.leo.main.config;

import it.leo.main.config.enums.CommandType;
import it.leo.main.config.enums.OpCodeType;
import it.leo.main.config.enums.QueryType;
import it.leo.main.config.enums.StatusCode;

public class ProtocolConfig {
    private static final CommandType[] COMMAND_TYPE = new CommandType[256];
    private static final OpCodeType[] OPCODE_TYPE = new OpCodeType[256];
    private static final StatusCode[] STATUS_CODE = new StatusCode[256];
    private static final QueryType[] QUERY_TYPE = new QueryType[256];

    
    static {
        COMMAND_TYPE[0x01] = CommandType.CONNECT;
        COMMAND_TYPE[0x02] = CommandType.QUERY;
        
        OPCODE_TYPE[0x01] = OpCodeType.REQUEST;
        OPCODE_TYPE[0x02] = OpCodeType.RESPONSE;
        OPCODE_TYPE[0x01] = OpCodeType.REQUEST;
        OPCODE_TYPE[0x02] = OpCodeType.RESPONSE;
        
        STATUS_CODE[0x01] = StatusCode.OK;
        STATUS_CODE[0x02] = StatusCode.ERROR;
        STATUS_CODE[0x01] = StatusCode.OK;
        STATUS_CODE[0x02] = StatusCode.ERROR;
        
        QUERY_TYPE[0x01] = QueryType.GET;
        QUERY_TYPE[0x02] = QueryType.SET;
        QUERY_TYPE[0x03] = QueryType.GETALL;
    }
    
    // Helper func
    private static byte getArrayKey(Object value, Object[] array) {
        byte key_byte = 0x00;
        for (byte i = 0; i<= array.length; i++) {
            key_byte = (array[i].equals(value)) ? i : null;
        }
        return key_byte;
    }

    public static Byte getCommandByte(CommandType commandType) {
        return getArrayKey(commandType, COMMAND_TYPE);
    }

    
    public static CommandType getCommandType(Byte bytec) {
        return COMMAND_TYPE[bytec];
    }

    public static Byte getOpCodeByte(OpCodeType opCodeType) {
        return getArrayKey(opCodeType, OPCODE_TYPE);
    }

    public static OpCodeType getOpCodeType(Byte bytec) {
        return OPCODE_TYPE[bytec];
    }

    public static StatusCode getStatusCode(Byte bytec) {
        return STATUS_CODE[bytec];
    }

    public static byte getStatusByte(StatusCode code) {
        return getArrayKey(code, STATUS_CODE);
    }

    public static QueryType getQueryType(Byte bytec) {
        return QUERY_TYPE[bytec];
    }

    public static byte getQueryByte(QueryType queryType) {
        return getArrayKey(queryType, QUERY_TYPE);
    }
}