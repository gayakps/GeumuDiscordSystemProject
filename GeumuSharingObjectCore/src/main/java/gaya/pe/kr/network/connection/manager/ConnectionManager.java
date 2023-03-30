package gaya.pe.kr.network.connection.manager;


import gaya.pe.kr.network.connection.initializer.MinecraftServerInitializer;
import gaya.pe.kr.network.packet.bound.server.PlayerMessage;
import gaya.pe.kr.network.packet.global.PacketStartDirection;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Random;
import java.util.UUID;

public class ConnectionManager {


    public void init() {

        try {
            new Server(8080).run(); // 서버 켜고

            new Thread(() -> {
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                try {
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(workerGroup)
                            .channel(NioSocketChannel.class)
                            .option(ChannelOption.SO_KEEPALIVE, true)
                            .handler(new MinecraftServerInitializer(PacketStartDirection.CLIENT));
                    ChannelFuture future = bootstrap.connect("localhost", 8080).sync();
                    Channel channel = future.channel();

                    Thread thread = new Thread( ()-> {
                        try {
                            while (true) {
                                Thread.sleep(2000);
                                Random random = new Random();
                                PlayerMessage playerMessage = new PlayerMessage(UUID.randomUUID(), String.format("User-%d", random.nextInt(100)), "안녕하세요 Message-" + random.nextInt(1000));
                                channel.writeAndFlush(playerMessage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    thread.start();
                    future.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    workerGroup.shutdownGracefully();
                }

            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public class Server {
        private final int port;

        public Server(int port) {
            this.port = port;
        }

        public void run() throws Exception {

            new Thread(() -> {
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap()
                            .group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new MinecraftServerInitializer(PacketStartDirection.SERVER))
                            .option(ChannelOption.SO_BACKLOG, 128)
                            .childOption(ChannelOption.SO_KEEPALIVE, true);

                    ChannelFuture future = bootstrap.bind(port).sync();
                    System.out.println("Server started on port " + port);
                    future.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                }
            }).start();


        }


    }
}
