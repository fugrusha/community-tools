package com.community.tools.service.slack;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.users.UsersListRequest;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.model.Channel;
import com.github.seratch.jslack.api.model.Conversation;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.webhook.Payload;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlackService {

  @Value("${slack.token}")
  private String token;
  @Value("${slack.webhook}")
  private String slackWebHook;

  /**
   * Send private message with messageText to username.
   * @param username Slack login
   * @param messageText Text of message
   * @return timestamp of message
   * @throws IOException IOException
   * @throws SlackApiException SlackApiException
   */
  public String sendPrivateMessage(String username, String messageText)
      throws IOException, SlackApiException {
    Slack slack = Slack.getInstance();

    User user = slack.methods(token).usersList(req -> req).getMembers().stream()
        .filter(u -> u.getProfile().getDisplayName().equals(username))
        .findFirst().get();

    ChatPostMessageResponse postResponse =
        slack.methods(token).chatPostMessage(
            req -> req.channel(user.getId()).asUser(true)
                .text(messageText));

    return postResponse.getTs();
  }

  /**
   * Send block message with messageText to username.
   * @param username Slack login
   * @param messageText Text of message
   * @return timestamp of message
   * @throws IOException IOException
   * @throws SlackApiException SlackApiException
   */
  public String sendBlocksMessage(String username, String messageText)
      throws IOException, SlackApiException {
    Slack slack = Slack.getInstance();

    User user = slack.methods(token).usersList(req -> req).getMembers().stream()
        .filter(u -> u.getProfile().getDisplayName().equals(username))
        .findFirst().get();

    ChatPostMessageResponse postResponse =
        slack.methods(token).chatPostMessage(
            req -> req.channel(user.getId()).asUser(true)
                .blocksAsString(messageText));

    return postResponse.getTs();
  }

  /**
   * Send attachment message with messageText to username.
   * @param username Slack login
   * @param messageText Text of message
   * @return timestamp of message
   * @throws IOException IOException
   * @throws SlackApiException SlackApiException
   */
  public String sendAttachmentsMessage(String username, String messageText)
      throws IOException, SlackApiException {
    Slack slack = Slack.getInstance();

    User user = slack.methods(token).usersList(req -> req).getMembers().stream()
        .filter(u -> u.getProfile().getDisplayName().equals(username))
        .findFirst().get();

    ChatPostMessageResponse postResponse =
        slack.methods(token).chatPostMessage(
            req -> req.channel(user.getId()).asUser(true)
                .attachmentsAsString(messageText));

    return postResponse.getTs();
  }

  /**
   * Send attachment message with messageText to channel.
   * @param channelName Name of channel
   * @param messageText Text of message
   * @return timestamp of message
   * @throws IOException IOException
   * @throws SlackApiException SlackApiException
   */
  public String sendMessageToConversation(String channelName, String messageText)
      throws IOException, SlackApiException {

    Slack slack = Slack.getInstance();
    Conversation channel = slack.methods(token)
        .conversationsList(req -> req)
        .getChannels()
        .stream()
        .filter(u -> u.getName().equals(channelName))
        .findFirst().get();
    ChatPostMessageResponse postResponse =
        slack.methods(token).chatPostMessage(
            req -> req.channel(channel.getId()).asUser(true).text(messageText));
    return postResponse.getTs();
  }

  /**
   * Send attachment message with messageText to channel.
   * @param channelName Name of channel
   * @param messageText Text of message
   * @return timestamp of message
   * @throws IOException IOException
   * @throws SlackApiException SlackApiException
   */
  @Deprecated
  public String sendMessageToChat(String channelName, String messageText)
      throws IOException, SlackApiException {
    Slack slack = Slack.getInstance();

    Channel channel = slack.methods(token)
        .channelsList(req -> req)
        .getChannels()
        .stream()
        .filter(u -> u.getName().equals(channelName))
        .findFirst().get();

    ChatPostMessageResponse postResponse =
        slack.methods(token).chatPostMessage(
            req -> req.channel(channel.getId()).asUser(true).text(messageText));

    return postResponse.getTs();
  }

  /**
   * Get user by Slack`s id.
   * @param id Slack`s id
   * @return realName of User
   */
  public String getUserById(String id) {
    Slack slack = Slack.getInstance();
    try {
      User user = slack.methods(token).usersList(req -> req).getMembers().stream()
          .filter(u -> u.getId().equals(id))
          .findFirst().get();
      return user.getRealName();
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get user by Slack`s id.
   * @param id Slack`s id
   * @return Slack`s id
   */
  public String getIdByUser(String id) {
    Slack slack = Slack.getInstance();
    try {
      User user = slack.methods(token).usersList(req -> req).getMembers().stream()
          .filter(u -> u.getRealName().equals(id))
          .findFirst().get();
      return user.getId();
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get all Slack`s user.
   * @return Set of users.
   */
  public Set<User> getAllUsers() {
    try {
      Slack slack = Slack.getInstance();
      Set<User> users = new HashSet<>(slack.methods()
          .usersList(UsersListRequest.builder()
              .token(token)
              .build())
          .getMembers());

      return users;
    } catch (IOException | SlackApiException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Send announcement with message.
   * @param message Text of message
   */
  public void sendAnnouncement(String message) {
    try {
      Payload payload = Payload.builder().text(message).build();
      Slack slack = Slack.getInstance();
      slack.send(slackWebHook, payload);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}