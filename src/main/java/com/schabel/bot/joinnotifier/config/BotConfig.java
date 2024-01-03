package com.schabel.bot.joinnotifier.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.schabel.bot.joinnotifier.listener.EventListener;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class BotConfig {

    @Value("${join-notifier.token}")
    private String token;

    @Value("${join-notifier.notify.voice-state-change.text-channel}")
    private String voiceStateChangeTextChannelsToNotify;

    public static MessageChannel NOTIFICATION_MESSAGE_CHANNEL;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
        GatewayDiscordClient client = DiscordClientBuilder.create(token)
                .build()
                .gateway()
                .setEnabledIntents(IntentSet.of(Intent.GUILD_MESSAGES, Intent.DIRECT_MESSAGES,
                        Intent.MESSAGE_CONTENT, Intent.GUILD_VOICE_STATES))
                .login()
                .block();

        for (EventListener<T> listener : eventListeners) {
            client.on(listener.getEventType())
                    .flatMap(listener::execute)
                    .onErrorResume(listener::handleError)
                    .subscribe();
        }

        client.getChannelById(Snowflake.of(voiceStateChangeTextChannelsToNotify))
                .ofType(MessageChannel.class)
                .flatMap(channel -> setNotificationMessageChannel(channel))
                .subscribe();

        return client;
    }

    private Mono<Void> setNotificationMessageChannel(MessageChannel messageChannel) {
        NOTIFICATION_MESSAGE_CHANNEL = messageChannel;
        log.info("Set " + NOTIFICATION_MESSAGE_CHANNEL.getId().asString() + " as the Noticiation Message Channel.");
        return Mono.empty();
    }

}
