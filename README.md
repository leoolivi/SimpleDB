# Java Simple Key-Value DB

This is a low level learning project where I try to learn how to code a database system from scratch using only java and a build tool to manage packages structure. I'm also writing my first README.md wrote completely by myself.

## Index

- [Java Simple Key-Value DB](#java-simple-key-value-db)
  - [Index](#index)
  - [Description](#description)
  - [Stack](#stack)
  - [Features](#features)
  - [Learning goals](#learning-goals)

## Description

In this database we have a client-server connection with a simple java socket on a specific host/port binding. Once the client is connected it can send simple strings that the server is going to process and handle. In the client we can paste different commands:

- `GET <key>` Returns the record (key-value pair) of the required key (unique pk).
- `SET <key> TO <value>` Creates or updates if exists a record with the new value.
- `GETALL` Returns all the records in the database.

To save the records I'm using a plain .csv file comma-separated

## Stack

Here's all the features that you should know

| Feature | Value |
| ------ | ---- |
| Language | Java 21 |
| Programming paradigms | OOP, Functional & Imperative Programming |
| Build Tool | Maven |

## Features

In this project i'm going to focus on implementing these features:

- [x] Basic creation/reading and updating records
- [x] Proper error handling
- [ ] More robust communication protocol
- [ ] Multiple clients handling with Thread pool
- [ ] Concurrency and race conditions handling
- [ ] Support for more complex tables and queries
- [ ] Transactions
- [ ] Database locking
- [ ] Security features like encryption and auth layer

## Learning goals

The purpose of this project is to learn crucial programming skills in an AI dominated world where most junior devs can't write a simple HelloWorld file without asking AI. My main goals are:

- learn clean code and follow SOLID principles
- learn how to write efficient and thread safe code
- learn common and useful design patterns and best practices
- learn the basics and even advanced concepts of concurrency and race condition
- improve language knowledge
- learn how to read javadoc
- learn how to write meaningful comments and .md files

If you have tips or any suggestion, feel free to ask.
