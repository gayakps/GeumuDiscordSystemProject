package gaya.pe.kr.plugin.network.manager;

import gaya.pe.kr.network.connection.initializer.MinecraftServerInitializer;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketStartDirection;
import gaya.pe.kr.plugin.GeumuDiscordSystem;
import gaya.pe.kr.plugin.discord.manager.BukkitDiscordManager;
import gaya.pe.kr.plugin.network.handler.MinecraftServerPacketHandler;
import gaya.pe.kr.plugin.thread.SchedulerUtil;
import gaya.pe.kr.plugin.util.data.WaitingTicket;
import gaya.pe.kr.util.data.ConsumerTwoObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.bukkit.entity.Player;

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

    public <T> void sendDataExpectResponse(AbstractMinecraftPacket requestMinecraftPacket, Player sender, Class<T> expectResponseClazz, ConsumerTwoObject<Player, T> consumerTwoObject) {

        SchedulerUtil.runWaitTask( ()-> {
            ChannelFuture channelFuture = channel.writeAndFlush(requestMinecraftPacket);
            try {
                Void result = channelFuture.get();
                long requestTicketId = requestMinecraftPacket.getPacketID();

                WaitingTicket<?> waitingTicket = new WaitingTicket<>(sender, sender.getUniqueId(), consumerTwoObject, expectResponseClazz);
                minecraftServerPacketHandler.addWaitingTicket(requestMinecraftPacket, waitingTicket);

                SchedulerUtil.runLaterTask( ()-> {
                    if ( minecraftServerPacketHandler.isWaitingTicket(requestTicketId) ) {
                        minecraftServerPacketHandler.removeWaitingTicket(requestTicketId);
                        GeumuDiscordSystem.msg(sender, "&f[&c&l!&f] §c&l메인 서버로부터 응답이 없습니다");
                    }
                }, 20*5);

                waitingTicket.executeWaitingTicket();

            } catch (InterruptedException | ExecutionException e ) {
                GeumuDiscordSystem.msg(sender, "&f[&c&l!&f] §c&l데이터 송신에 문제가 발생했습니다");
                e.printStackTrace();
            }
        });

    }

    public void sendPacket(AbstractMinecraftPacket requestMinecraftPacket, Player sender, Consumer<Player> sendSuccessAfterConsumer) {

        SchedulerUtil.runWaitTask( ()-> {
            ChannelFuture channelFuture = channel.writeAndFlush(requestMinecraftPacket);
            try {
                Void result = channelFuture.get();
                sendSuccessAfterConsumer.accept(sender);
            } catch (InterruptedException | ExecutionException e ) {
                GeumuDiscordSystem.msg(sender, "&f[&c&l!&f] §c&l데이터 송신에 문제가 발생했습니다");
                e.printStackTrace();
            }
        });

    }

    public void sendPacket(AbstractMinecraftPacket requestMinecraftPacket, Consumer<Player> sendSuccessAfterConsumer, Player sender) {

        SchedulerUtil.runWaitTask( ()-> {
            ChannelFuture channelFuture = channel.writeAndFlush(requestMinecraftPacket);
            try {
                Void result = channelFuture.get();

                    WaitingTicket<Boolean> waitingTicket = new WaitingTicket<>(sender, sender.getUniqueId(), (player, aBoolean) -> {
                        sendSuccessAfterConsumer.accept(player);
                    } , Boolean.class);
                    minecraftServerPacketHandler.addWaitingTicket(requestMinecraftPacket, waitingTicket);

                    long requestTicketId = requestMinecraftPacket.getPacketID();

                    SchedulerUtil.runLaterTask( ()-> {
                        if ( minecraftServerPacketHandler.isWaitingTicket(requestTicketId) ) {
                            minecraftServerPacketHandler.removeWaitingTicket(requestTicketId);
                            GeumuDiscordSystem.msg(sender, "&f[&c&l!&f] §c&l메인 서버로부터 응답이 없습니다");
                        }
                    }, 20*5);

                    sendSuccessAfterConsumer.accept(sender);

            } catch (InterruptedException | ExecutionException e ) {
                GeumuDiscordSystem.msg(sender, "&f[&c&l!&f] §c&l데이터 송신에 문제가 발생했습니다");
                e.printStackTrace();
            }
        });

    }

}
