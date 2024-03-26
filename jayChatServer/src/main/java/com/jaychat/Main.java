package com.jaychat;

import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Please give port as argument");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        final ChatServer endpoint = new ChatServer();
        ChannelFuture future = endpoint.start(
                new InetSocketAddress(port)
        );
        Runtime.getRuntime().addShutdownHook(new Thread(endpoint::destroy));

        future.channel().closeFuture().syncUninterruptibly();
    }
}