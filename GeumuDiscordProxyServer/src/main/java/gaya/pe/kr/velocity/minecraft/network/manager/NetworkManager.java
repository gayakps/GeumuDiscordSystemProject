package gaya.pe.kr.velocity.minecraft.network.manager;

import gaya.pe.kr.network.connection.initializer.MinecraftServerInitializer;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketStartDirection;
import gaya.pe.kr.velocity.minecraft.network.handler.MinecraftClientPacketHandler;
import gaya.pe.kr.velocity.minecraft.qa.answer.manager.AnswerManager;
import gaya.pe.kr.velocity.minecraft.qa.manager.QAUserManager;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
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


    public void init() {
        new Thread(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {


                ServerBootstrap bootstrap = new ServerBootstrap()
                        .group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new MinecraftServerInitializer(PacketStartDirection.SERVER, () -> new MinecraftClientPacketHandler()))
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                ChannelFuture future = bootstrap.bind(8080).sync();

                System.out.println("Server started on port " + 8080);

                QAUserManager qaUserManager = QAUserManager.getInstance();
                qaUserManager.init();

                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }).start();
    }


    public void sendPacket(Channel channel, AbstractMinecraftPacket minecraftPacket) {
        MinecraftClientPacketHandler.channelGroup.forEach(channel1 -> {
            if ( channel1.equals(channel) ) {
                channel.writeAndFlush(minecraftPacket);
                return;
            }
        });
    }

    public void sendPacketAllChannel(AbstractMinecraftPacket minecraftPacket) {
        MinecraftClientPacketHandler.channelGroup.forEach(channel -> {
           channel.writeAndFlush(minecraftPacket);
        });
    }


}
