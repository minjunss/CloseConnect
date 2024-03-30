package com.CloseConnect.closeconnect.global.handler;

import com.CloseConnect.closeconnect.security.oatuh2.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
//@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 스프링의 빈 순서를 지정하는 애노테이션으로, StompHandler 의 우선순위를 설정, Ordered.HIGHEST_PRECEDENCE 는 가장 높은 우선순위
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 유효성 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
                token = token.substring(7);
            }
            if (!jwtTokenProvider.validateToken(token)) {
                throw new IllegalStateException("유효하지 않은 토큰");
            }
        }
        return message;
    }

    @EventListener
    public void handleWebSocketConnectionListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        GenericMessage<Object> genericMessage = (GenericMessage<Object>) accessor.getHeader("simpConnectMessage");
        Map<String, ?> nativeHeaders = (Map<String, Object>) genericMessage.getHeaders().get("nativeHeaders");
        List<String> authorizationHeader = (List<String>) nativeHeaders.get("username");

        String username = authorizationHeader.get(0);
        log.info("{} 입장", username);
    }

    @EventListener
    public void handleWebSocketDisconnectionListener(SessionDisconnectEvent event) {
        log.info("사용자 퇴장");
    }


}
