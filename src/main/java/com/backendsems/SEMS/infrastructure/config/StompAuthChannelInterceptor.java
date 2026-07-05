package com.backendsems.SEMS.infrastructure.config;

import com.backendsems.iam.application.internal.outboundservices.tokens.TokenService;
import com.backendsems.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * Resuelve el usuario dueño de una conexión STOMP a partir del JWT enviado en el frame CONNECT,
 * para poder enviar las alertas de consumo (US23) por usuario ({@code convertAndSendToUser}) en
 * vez del canal global {@code /topic/alerts} que antes recibían todos los clientes conectados.
 */
@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final TokenService tokenService;
    private final ProfilesContextFacade profilesContextFacade;

    public StompAuthChannelInterceptor(TokenService tokenService, ProfilesContextFacade profilesContextFacade) {
        this.tokenService = tokenService;
        this.profilesContextFacade = profilesContextFacade;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            String token = authHeader != null ? authHeader.replace("Bearer ", "") : null;

            if (token != null && tokenService.validateToken(token) && !tokenService.isTokenRevoked(token)) {
                String email = tokenService.getEmailFromToken(token);
                Long profileId = profilesContextFacade.fetchProfileIdByEmail(email);
                if (profileId != null) {
                    Principal principal = profileId::toString;
                    accessor.setUser(principal);
                }
            }
        }
        return message;
    }
}
