package com.schabel.bot.joinnotifier.listener;

import org.springframework.stereotype.Service;

import com.schabel.bot.joinnotifier.config.BotConfig;

import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.VoiceState;
import reactor.core.publisher.Mono;

@Service
public class VoiceStateChangeListener implements EventListener<VoiceStateUpdateEvent> {

    private static final String JOIN_ACTION = " joined ";
    private static final String LEFT_ACTION = " left ";

    @Override
    public Class<VoiceStateUpdateEvent> getEventType() {
        return VoiceStateUpdateEvent.class;
    }

    @Override
    public Mono<Void> execute(VoiceStateUpdateEvent event) {
        if (event.isJoinEvent()) {
            return processStateChange(event.getCurrent(), JOIN_ACTION);
        } else if (event.isLeaveEvent()) {
            if(event.getOld().isPresent()) {
                return processStateChange(event.getOld().get(), LEFT_ACTION);
            } else {
                return Mono.empty();
            }
        } else {
            return Mono.empty();
        }
    }

    public Mono<Void> processStateChange(VoiceState event, String action) {
        return Mono.just(event)
                .filter(nullState -> nullState.getChannel().block() != null || nullState.getMember().block() != null)
                .flatMap(state -> BotConfig.NOTIFICATION_MESSAGE_CHANNEL.createMessage(
                        state.getMember().block().getDisplayName() + 
                        action + 
                        state.getChannel().block().getName()
                        + "."))
                .then();
    }

}
