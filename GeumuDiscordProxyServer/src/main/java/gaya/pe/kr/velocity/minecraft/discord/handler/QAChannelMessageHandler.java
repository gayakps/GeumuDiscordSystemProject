package gaya.pe.kr.velocity.minecraft.discord.handler;

import gaya.pe.kr.qa.answer.packet.client.PlayerTransientProceedingAnswerRequest;
import gaya.pe.kr.qa.data.QARequestResult;
import gaya.pe.kr.qa.data.QAUser;
import gaya.pe.kr.qa.question.data.Question;
import gaya.pe.kr.qa.question.packet.client.PlayerTransientProceedingQuestionRequest;
import gaya.pe.kr.util.option.data.options.ConfigOption;
import gaya.pe.kr.velocity.minecraft.discord.handler.abs.MessageChannelHandler;
import gaya.pe.kr.velocity.minecraft.discord.manager.DiscordManager;
import gaya.pe.kr.velocity.minecraft.option.manager.ServerOptionManager;
import gaya.pe.kr.velocity.minecraft.qa.answer.manager.AnswerManager;
import gaya.pe.kr.velocity.minecraft.qa.manager.QAUserManager;
import gaya.pe.kr.velocity.minecraft.qa.question.manager.QuestionManager;
import gaya.pe.kr.velocity.minecraft.thread.VelocityThreadUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class QAChannelMessageHandler extends MessageChannelHandler {

    QuestionManager questionManager;
    AnswerManager answerManager;
    DiscordManager discordManager;

    QAUserManager qaUserManager;

    ServerOptionManager serverOptionManager;

    public QAChannelMessageHandler(TextChannel textChannel) {
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

            if ( !qaUserManager.existUser(sendUserId) ) {
                discordManager.sendMessageAndRemove(event.getChannel(), "``` 미 인증 유저는 답변할 수 없습니다 ```", 3000, true, receivedMessage);
                return;
            }

            QAUser answerUser = qaUserManager.getUser(sendUserId);
            Message repliedMessage = event.getMessage().getReferencedMessage(); // 레퍼런스 메시지

            if ( repliedMessage == null ) {
                discordManager.sendMessageAndRemove(event.getChannel(), configOption.getInvalidQuestionNumber(), 3000, true, receivedMessage);
                return;
            }

            long repliedMessageId = repliedMessage.getIdLong();

            if ( questionManager.existQuestionByDiscordMessageId(repliedMessageId) ) {
                // 이 메세지가 삭제된 질문이 아니라면

                Question question = questionManager.getQuestionByDiscordMessageId(repliedMessageId);

                String receivedMessageContent = receivedMessage.getContentRaw();

                PlayerTransientProceedingAnswerRequest playerTransientProceedingAnswerRequest = new PlayerTransientProceedingAnswerRequest(question.getId(), receivedMessageContent, answerUser.getDiscordPlayerUserId());

                QARequestResult qaRequestResult = answerManager.processAnswer(playerTransientProceedingAnswerRequest);

                if ( qaRequestResult.getType().equals(QARequestResult.Type.SUCCESS) ) {
                    // 디코에서 답장을 성공하면 할게없음
                    VelocityThreadUtil.asyncTask(() -> {
                        receivedMessage.delete().queue();
                        repliedMessage.delete().queue();
                    });
                } else {
                    discordManager.sendMessageAndRemove(event.getChannel(),qaRequestResult.getMessage(), 3000, true, receivedMessage);
                }

            } else {
                //삭제된 메세지일 경우
                discordManager.sendMessageAndRemove(event.getChannel(), configOption.getInvalidQuestionNumber(), 3000, true, receivedMessage);
            }

        } else {
            //질문의 경우 접두사 검사

            String prefix = questionManager.getQuestPrefix();
            String receivedMessageContent = receivedMessage.getContentRaw();

            if ( receivedMessageContent.startsWith(prefix)) {

                //prefix 를 제외하고 모두 질문으로 인식하여 질문 과정을 거치게됨
                String questionContents = receivedMessageContent.replace(prefix, "").trim();

                PlayerTransientProceedingQuestionRequest playerTransientProceedingQuestionRequest = new PlayerTransientProceedingQuestionRequest(sendUserId, questionContents);
                QARequestResult qaRequestResult = questionManager.processQuestion(playerTransientProceedingQuestionRequest);

                if ( qaRequestResult.getType().equals(QARequestResult.Type.SUCCESS) ) {
                    VelocityThreadUtil.asyncTask( ()-> receivedMessage.delete().queue());
                } else {
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
