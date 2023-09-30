package com.getjavajob.training.maksyutovs.socialnetwork.web.listeners;

import com.getjavajob.training.maksyutovs.socialnetwork.domain.ChatMessage;
import com.getjavajob.training.maksyutovs.socialnetwork.service.security.AccountDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessageSendingOperations messagingTemplate;

    @Autowired
    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        AccountDetails details = (AccountDetails)
                ((UsernamePasswordAuthenticationToken) Objects.requireNonNull(event.getUser())).getPrincipal();
        logger.info("New web socket connection established by {}", details.getAccount());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        if (username != null) {
            logger.info("User disconnected: {}", username);
            var chatMessage = new ChatMessage(ChatMessage.MessageType.LEAVE, username);
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }

}
