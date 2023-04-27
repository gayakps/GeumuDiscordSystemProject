package gaya.pe.kr.velocity.minecraft.network.manager;

import gaya.pe.kr.network.connection.initializer.MinecraftServerInitializer;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketStartDirection;
import gaya.pe.kr.velocity.minecraft.network.handler.MinecraftClientPacketHandler;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class NetworkManager {


    private static class SingleTon {
        private static final NetworkManager NETWORK_MANAGER = new NetworkManager();
    }

    public static NetworkManager getInstance() {
        return SingleTon.NETWORK_MANAGER;
    }

    MinecraftClientPacketHandler minecraftClientPacketHandler = new MinecraftClientPacketHandler();

    public void init() {
        new Thread(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap()
                        .group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new MinecraftServerInitializer(PacketStartDirection.SERVER, minecraftClientPacketHandler))
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                ChannelFuture future = bootstrap.bind(8080).sync();

                System.out.println("Server started on port " + 8080);
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }).start();
    }

    public MinecraftClientPacketHandler getMinecraftClientPacketHandler() {
        return minecraftClientPacketHandler;
    }

    public void sendPacket(Channel channel, AbstractMinecraftPacket minecraftPacket) {
        getMinecraftClientPacketHandler().sendPacket(channel, minecraftPacket);
    }

    public void sendPacketAllChannel(AbstractMinecraftPacket minecraftPacket) {
        getMinecraftClientPacketHandler().sendPacketAllChannel(minecraftPacket);
    }


}
