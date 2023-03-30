package gaya.pe.kr;

import gaya.pe.kr.network.connection.manager.ConnectionManager;

public class Main {
    public static void main(String[] args) {
        ConnectionManager connectionManager = new ConnectionManager();
        connectionManager.init();
        System.out.println("Hello world!");
    }
}