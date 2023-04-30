package gaya.pe.kr.qa.question.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPlayerRequestPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class PlayerQuestionListByQuestionIdRequest extends AbstractMinecraftPlayerRequestPacket {

    HashSet<Integer> questionIdHashSet = new HashSet<>();

    public PlayerQuestionListByQuestionIdRequest(Player player) {
        super(PacketType.PLAYER_QUESTION_LIST_BY_QUESTION_ID_REQUEST, player);
    }

    public PlayerQuestionListByQuestionIdRequest addQuestionId(int answerId) {
        questionIdHashSet.add(answerId);
        return this;
    }

    public void addQuestionId(int... questionIds) {
        for (int questionId : questionIds) {
            questionIdHashSet.add(questionId);
        }
    }

}
