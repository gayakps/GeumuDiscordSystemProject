package gaya.pe.kr.velocity.minecraft.player;

import io.netty.channel.Channel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListHandler {

    static HashMap<Channel, List<String>> playerListAsChannel = new HashMap<>();

    public static List<String> getAllConnectionPlayers() {
        List<String> value = new ArrayList<>();
        playerListAsChannel.values().forEach(value::addAll);
        return value;
    }

    public static void setChannelAsPlayerList(Channel channel, List<String> playerList) {
        playerListAsChannel.put(channel, playerList);
        playerListAsChannel.forEach( (channel1, list) -> {
            for (String playerData : list) {
                System.out.printf("%n [유저목록] %s 채널의 %s %n", channel1.remoteAddress(), playerData);
            }
        });
    }

    public static List<String> getChannelAsPlayerList(Channel channel) {
        return ( playerListAsChannel.containsKey(channel) ? new ArrayList<>() : playerListAsChannel.get(channel));
    }

    @Nullable
    public static Channel getPlayerAsChannel(String playerName) {
        for (Map.Entry<Channel, List<String>> channelListEntry : playerListAsChannel.entrySet()) {
            Channel channel = channelListEntry.getKey();
            List<String> list = channelListEntry.getValue();
            for (String playerData : list) {
                if ( playerData.equals(playerName) ) {
                    return channel;
                }
            }
        }
        return null;
    }


    public static void removeChannel(Channel channel) {
        playerListAsChannel.remove(channel);
    }

}
