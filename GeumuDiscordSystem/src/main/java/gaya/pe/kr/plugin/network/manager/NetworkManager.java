package gaya.pe.kr.plugin.network.manager;

import gaya.pe.kr.network.connection.initializer.MinecraftServerInitializer;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
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

import java.util.HashSet;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static gaya.pe.kr.plugin.GeumuDiscordSystem.msg;

public class NetworkManager {

    private static class SingleTon {
        private static final NetworkManager NETWORK_MANAGER = new NetworkManager();
    }

    public static NetworkManager getInstance() {
        return SingleTon.NETWORK_MANAGER;
    }

    Channel channel;

    MinecraftServerPacketHandler minecraftServerPacketHandler = new MinecraftServerPacketHandler();

    public void init() {

        new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new MinecraftServerInitializer(PacketStartDirection.CLIENT, minecraftServerPacketHandler));
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

    public void sendPacket(AbstractMinecraftPacket minecraftPacket) {
        SchedulerUtil.runWaitTask( ()-> {
            ChannelFuture channelFuture = channel.writeAndFlush(minecraftPacket);
            try {
                Void result = channelFuture.get();
            } catch (InterruptedException | ExecutionException e ) {
                e.printStackTrace();
            }
        });
    }

    public void sendDataExpectResponse(AbstractMinecraftPacket requestMinecraftPacket, Player sender, Consumer<Player> sendSuccessAfterConsumer) {

        SchedulerUtil.runWaitTask( ()-> {
            ChannelFuture channelFuture = channel.writeAndFlush(requestMinecraftPacket);
            try {
                Void result = channelFuture.get();
                sendSuccessAfterConsumer.accept(sender);
                HashSet<Long> waitingResponseTicketHashSet = minecraftServerPacketHandler.getWaitingResponseTicketHashSet();

                long requestTicketId = requestMinecraftPacket.getPacketID();

                waitingResponseTicketHashSet.add(requestMinecraftPacket.getPacketID());

                SchedulerUtil.runLaterTask( ()-> {
                    if ( waitingResponseTicketHashSet.contains(requestTicketId) ) {
                        waitingResponseTicketHashSet.remove(requestTicketId);
                        msg(sender, "&c서버로 부터 응답이 없습니다 다시 시도해주세요");
                    }
                }, 20*5);


            } catch (InterruptedException | ExecutionException e ) {
                sender.sendMessage("§c데이터 송신에 문제가 발생했습니다!");
                e.printStackTrace();
            }
        });

    }

    public void sendPacket(AbstractMinecraftPacket minecraftPacket, Player sender, Consumer<Player> sendSuccessAfterConsumer) {

        SchedulerUtil.runWaitTask( ()-> {
            ChannelFuture channelFuture = channel.writeAndFlush(minecraftPacket);
            try {
                Void result = channelFuture.get();
                sendSuccessAfterConsumer.accept(sender);
            } catch (InterruptedException | ExecutionException e ) {
                sender.sendMessage("§c데이터 송신에 문제가 발생했습니다!");
                e.printStackTrace();
            }
        });

    }

}
