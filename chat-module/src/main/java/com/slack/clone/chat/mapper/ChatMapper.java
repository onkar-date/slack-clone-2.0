package com.slack.clone.chat.mapper;

import com.slack.clone.chat.document.ChannelMessage;
import com.slack.clone.chat.document.DmMessage;
import com.slack.clone.chat.dto.ChannelDTO;
import com.slack.clone.chat.dto.DmConversationDTO;
import com.slack.clone.chat.dto.MessageDTO;
import com.slack.clone.chat.entity.Channel;
import com.slack.clone.chat.entity.DmConversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for chat entities
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMapper {

    @Mapping(target = "memberCount", expression = "java(channel.getMembers().size())")
    ChannelDTO toDTO(Channel channel);

    DmConversationDTO toDTO(DmConversation dmConversation);

    MessageDTO toDTO(ChannelMessage message);

    MessageDTO toDTO(DmMessage message);
}
