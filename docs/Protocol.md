# The protocol

In this file there will be all the infos related to the communication protocol.

## General Idea

The idea of this protocol is the following.

A textual handshake based of fstring exchange and Diffie H. Algorithm for crypto.
A well defined packet structure to wait for, described byte per byte. See examples below.

## General Packet Structure

- Command (Head)
  - CONNECT 0x01
  - QUERY 0x02
- Op Code
  - REQUEST 0x01
  - RESPONSE 0x02
- Status (only res)
  - OK 0x01
  - ERROR 0x02
- Error msg len (int)
- Error msg (if present) byte[]
- chunk length (int)
- value

## Real Packets Structures

### Query Data Response

- Command 0x02
- Op Code 0x02
- Response Status 0x01
- Error msg len (int)
- Error msg (if present) bytes[]
- Lines number (int)
- Line i key length
- Line i key bytes
- Line i val length
- Line i val bytes
