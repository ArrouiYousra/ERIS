package Fourth_Argument.eris.messagingstompwebsocket.chat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import Fourth_Argument.eris.messagingstompwebsocket.chatroom.ChatRoomService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService.getChatRoomId(
            chatMessage.getSenderId(),
            chatMessage.getRecipientId(),
            true
        ).orElseThrow(() -> new IllegalStateException("Chat room not found"));
        chatMessage.setChatId(chatId);
        repository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findByChatMessages(
        String senderId,
        String recipientId
    ) {
        var chatId = chatRoomService.getChatRoomId(
            senderId, 
            recipientId,
            false);
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }
}
