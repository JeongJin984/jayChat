package com.jaychat;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    static Logger log = LoggerFactory.getLogger("org.example.Main");
    static final String URL = System.getProperty("url", "ws://127.0.0.1:9090/ws");

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            String[] input = br.readLine().split(" ");
            log.info("{}", input[0]);
            try {
                if(input[0].equalsIgnoreCase("/enter")) {
                    Channel channel = connect(input[1]);

                    if(channel == null) {
                        System.err.println("something is wrong");
                        break;
                    }

                    while(true) {
                        String msg = br.readLine();

                        if(msg.equalsIgnoreCase("/close")) {
                            channel.close();
                        } else if(msg == null) {
                            break;
                        } else if("ping".equalsIgnoreCase(msg)) {
                            WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[]{8, 1, 8, 1}));
                            channel.writeAndFlush(frame);
                        } else {
                            WebSocketFrame frame = new TextWebSocketFrame(msg);
                            channel.writeAndFlush(frame);
                        }
                    }
                } else if(input[0].equalsIgnoreCase("/exit")) {
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Channel connect(String roomId) throws SSLException, URISyntaxException {
        URI uri = new URI(URL);
        String schema = uri.getScheme() == null ? "ws" : uri.getScheme();
        final String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        final int port;
        if(uri.getPort() == -1) {
            if("ws".equalsIgnoreCase(schema)) {
                port = 80;
            } else if("wss".equalsIgnoreCase(schema)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = uri.getPort();
        }

        if(!"ws".equalsIgnoreCase(schema)  && !"wss".equalsIgnoreCase(schema)) {
            System.err.println("Only WS(S) is supported");
            return null;
        }

        final boolean ssl = "wss".equalsIgnoreCase(schema);
        final SslContext sslCtx;
        if(ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } else {
            sslCtx = null;
        }
        try(EventLoopGroup group = new NioEventLoopGroup()) {
            final WebSocketClientHandler handler = new WebSocketClientHandler(
                    WebSocketClientHandshakerFactory.newHandshaker(
                            uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()
                    )
            );

            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            if(sslCtx != null) {
                                pipeline.addLast(sslCtx.newHandler(channel.alloc(), host, port));
                            }
                            pipeline.addLast(
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(8192),
                                    WebSocketClientCompressionHandler.INSTANCE,
                                    handler
                            );
                        }
                    });

            Channel channel = bootstrap.connect(uri.getHost(), port).sync().channel();
            handler.handShakerFuture().sync();
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}