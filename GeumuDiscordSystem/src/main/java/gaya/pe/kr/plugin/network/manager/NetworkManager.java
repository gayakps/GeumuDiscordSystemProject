package gaya.pe.kr.plugin.network.manager;

import gaya.pe.kr.network.connection.initializer.MinecraftServerInitializer;
import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketStartDirection;
import gaya.pe.kr.plugin.network.handler.MinecraftServerPacketHandler;
import gaya.pe.kr.plugin.thread.SchedulerUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class NetworkManager {

    private static class SingleTon {
        private static final NetworkManager NETWORK_MANAGER = new NetworkManager();
    }

    public static NetworkManager getInstance() {
        return SingleTon.NETWORK_MANAGER;
    }

    Channel channel;

    public void init() {

        new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new MinecraftServerInitializer(PacketStartDirection.CLIENT, new MinecraftServerPacketHandler()));
                ChannelFuture future = bootstrap.connect("localhost", 8080).sync();
                channel = future.channel();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }

        }).start();

    }

    public void sendData(MinecraftPacket minecraftPacket) {
        SchedulerUtil.runWaitTask( ()-> {
            ChannelFuture channelFuture = channel.writeAndFlush(minecraftPacket);
            try {
                Void result = channelFuture.get();
            } catch (InterruptedException | ExecutionException e ) {
                e.printStackTrace();
            }
        });
    }

    public void sendData(MinecraftPacket minecraftPacket, Player sender, Consumer<Player> sendSuccessAfterConsumer) {

        SchedulerUtil.runWaitTask( ()-> {
            ChannelFuture channelFuture = channel.writeAndFlush(minecraftPacket);
            try {
                Void result = channelFuture.get();
                sendSuccessAfterConsumer.accept(sender);
            } catch (InterruptedException | ExecutionException e ) {
                e.printStackTrace();
            }
        });

    }

}
