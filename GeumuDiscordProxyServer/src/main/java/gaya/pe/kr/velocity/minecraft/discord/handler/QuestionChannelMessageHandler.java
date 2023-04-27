package gaya.pe.kr.velocity.minecraft.discord.handler;

import gaya.pe.kr.qa.data.QARequestResult;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.qa.question.data.TransientPlayerProceedingQuestion;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.velocity.minecraft.discord.exception.NonExistPlayerAuthenticationDataException;
import gaya.pe.kr.velocity.minecraft.discord.handler.abs.MessageChannelHandler;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.option.manager.ServerOptionManager;
import gaya.pe.kr.velocity.minecraft.qa.answer.manager.AnswerManager;
import gaya.pe.kr.velocity.minecraft.qa.manager.QAUserManager;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.checkerframework.checker.units.qual.A;

public class QuestionChannelMessageHandler extends MessageChannelHandler {

    QuestionManager questionManager;
    AnswerManager answerManager;
    DiscordManager discordManager;

    QAUserManager qaUserManager;

    ServerOptionManager serverOptionManager;

    public QuestionChannelMessageHandler(TextChannel textChannel) {
        super(textChannel);
        qaUserManager = QAUserManager.getInstance();
        questionManager = QuestionManager.getInstance();
        answerManager = AnswerManager.getInstance();
        discordManager = DiscordManager.getInstance();
        serverOptionManager = ServerOptionManager.getInstance();
    }

    @Override
    protected void handleEvent(MessageReceivedEvent event) {

        Message receivedMessage = event.getMessage();
        User user = receivedMessage.getAuthor();
        long sendUserId = user.getIdLong(); // 사용자 고유 ID를 가져옵니다.

        MessageType messageType = receivedMessage.getType();

        ConfigOption configOption = serverOptionManager.getConfigOption();

        if ( messageType.equals(MessageType.INLINE_REPLY ) ) {

            //TODO 디스코드 내에서 답장 기능

            if ( !qaUserManager.existUser(sendUserId) ) {
                discordManager.sendMessageAndRemove(event.getChannel(), "``` 미 인증 유저는 답변할 수 없습니다 ```", 3000, true, receivedMessage);
                return;
            }

            QAUser answerUser = qaUserManager.getUser(sendUserId);
            Message repliedMessage = event.getMessage().getReferencedMessage(); // 레퍼런스 메시지

            if ( repliedMessage == null ) {
                discordManager.sendMessageAndRemove(event.getChannel(), configOption.getRemoveQFailNotExist(), 3000, true, receivedMessage);
                return;
            }

            long repliedMessageId = repliedMessage.getIdLong();

            if ( questionManager.existQuestionByDiscordMessageId(repliedMessageId) ) {
                // 이 메세지가 삭제된 질문이 아니라면

                Question question = questionManager.getQuestionByDiscordMessageId(repliedMessageId);
                QAUser questionUser = question.getQaUser();

                if ( questionUser.equals(answerUser) ) {
                    //자신의 질문에 답장할 때
                    discordManager.sendMessageAndRemove(event.getChannel(),  configOption.getQuestionNumberAnswerSendFailCanNotSelfAnswer(), 3000, true, receivedMessage);
                    return;
                }

                // 자신의 답장이 아니라면

                //TODO 정상적으로 답변을 진행해야됨



            } else {
                //삭제된 메세지일 경우
                discordManager.sendMessageAndRemove(event.getChannel(), configOption.getRemoveQFailNotExist(), 3000, true, receivedMessage);
            }

        } else {
            //질문의 경우 접두사 검사

            String prefix = questionManager.getQuestPrefix();
            String receivedMessageContent = receivedMessage.getContentRaw();

            if ( receivedMessageContent.startsWith(prefix)) {

                //prefix 를 제외하고 모두 질문으로 인식하여 질문 과정을 거치게됨

                String questionContents = receivedMessageContent.replace(prefix, "").trim();

                PlayerTransientProceedingQuestionRequest playerTransientProceedingQuestionRequest = new PlayerTransientProceedingQuestionRequest(sendUserId, questionContents);
                QARequestResult qaRequestResult = questionManager.canQuestion(playerTransientProceedingQuestionRequest);

                QAUser qaUser = qaUserManager.getUser(sendUserId);
                Question question = new Question(questionManager.getQuestionNumber(), questionContents, qaUser);
                questionManager.broadCastQuestion(question, qaRequestResult);

                if ( qaRequestResult.getType().equals(QARequestResult.Type.FAIL) ) {
                    discordManager.sendMessageAndRemove(event.getChannel(), qaRequestResult.getMessage(), 3000, true, receivedMessage);
                }

            } else {

                // 질문 접두사로 질문안했다면?
                discordManager.sendMessageAndRemove(event.getChannel(), questionManager.getQuestPrefixHelpMessage()
                        .replace("%playername%", discordManager.getFullName(user))
                        .replace("%prefix%", prefix), 3000, true, receivedMessage);

            }
        }


    }




}
