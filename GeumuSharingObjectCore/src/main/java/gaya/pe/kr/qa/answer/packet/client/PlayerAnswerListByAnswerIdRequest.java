package gaya.pe.kr.qa.answer.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import org.bukkit.entity.Player;

import java.util.HashSet;

/**
 *
 */
public class PlayerAnswerListByAnswerIdRequest extends AbstractMinecraftPlayerRequestPacket {

    HashSet<Integer> answerIdHashSet = new HashSet<>();

    public PlayerAnswerListByAnswerIdRequest(Player player) {
        super(PacketType.PLAYER_ANSWER_LIST_BY_ANSWER_ID_REQUEST, player);
    }

    public PlayerAnswerListByAnswerIdRequest addAnswerId(int answerId) {

        answerIdHashSet.add(answerId);
        return this;

    }

    public void addAnswerId(int... answerIds) {
        for (int answerId : answerIds) {
            answerIdHashSet.add(answerId);
        }
    }


}
