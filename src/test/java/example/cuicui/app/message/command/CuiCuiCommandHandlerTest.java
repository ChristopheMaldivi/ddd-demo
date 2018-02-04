package example.cuicui.app.message.command;

import org.tophe.cqrses.commands.CommandResponse;
import example.cuicui.app.message.domain.Message;
import example.cuicui.app.message.events.CuiCuiCreated;
import example.cuicui.app.message.infrastructure.MessageInMemoryRepository;
import example.cuicui.app.message.infrastructure.persistence.MessageRepository;
import org.tophe.cqrses.event.EventBus;
import org.tophe.cqrses.event.TestEventHandler;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CuiCuiCommandHandlerTest {
  TestEventHandler testEventHandler = new TestEventHandler();
  EventBus eventBus = new EventBus();
  MessageRepository messageRepository = new MessageInMemoryRepository();
  CuiCuiCommandHandler handler = new CuiCuiCommandHandler(messageRepository, eventBus);

  @Before
  public void setUp() {
    eventBus.register(testEventHandler);
  }

  @Test
  public void create_message_when_user_says_cuicui_and_check_message_repository() {
    // given
    String cuicui = "Say cuicui little bird";
    CuiCuiCommand command = new CuiCuiCommand(cuicui);

    // when
    CommandResponse<String> response = handler.execute(command);

    // then
    assertThat(response.success()).isTrue();
    assertThat(messageRepository.findAll()).hasSize(1);
    Message message = messageRepository.findAll().iterator().next();
    assertThat(response.value()).isEqualTo(message._id);
    assertThat(message).isEqualTo(Message.builder()
      ._id(message._id)
      .message(cuicui)
      .build()
    );
  }

  @Test
  public void create_message_when_user_says_cuicui_and_check_event_was_emitted() {
    // given
    String cuicui = "Say cuicui little bird";
    CuiCuiCommand command = new CuiCuiCommand(cuicui);

    // when
    CommandResponse<String> response = handler.execute(command);

    // then
    assertThat(response.success()).isTrue();
    assertThat(testEventHandler.events()).containsExactly(
      new CuiCuiCreated(response.value(), cuicui)
    );
  }
}