# The protocol

In this file there will be all the infos related to the communication protocol.

## Packet Structure

- Comando (Head)
  - CONNECT 0x01
  - QUERY 0x02
- Length (int)
- Op Code
  - REQUEST 0x01
  - RESPONSE 0x02
- Payload (body serialized binary) byte[]
