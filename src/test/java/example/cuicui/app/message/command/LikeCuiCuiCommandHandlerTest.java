package example.cuicui.app.message.command;

import org.tophe.cqrses.commands.CommandResponse;
import example.cuicui.app.message.domain.Message;
import example.cuicui.app.message.events.CuiCuiCreated;
import example.cuicui.app.message.events.CuiCuiLiked;
import example.cuicui.app.message.infrastructure.MessageInMemoryRepository;
import example.cuicui.app.message.infrastructure.persistence.MessageRepository;
import org.tophe.cqrses.event.EventBus;
import org.tophe.cqrses.event.TestEventHandler;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LikeCuiCuiCommandHandlerTest {
  TestEventHandler testEventHandler = new TestEventHandler();
  EventBus eventBus = new EventBus();
  MessageRepository messageRepository = new MessageInMemoryRepository();
  LikeCuiCuiCommandHandler handler = new LikeCuiCuiCommandHandler(messageRepository, eventBus);

  @Before
  public void setUp() {
    eventBus.register(testEventHandler);
  }

  @Test
  public void like_a_cuicui_and_check_message_repository_is_updated() {
    // given
    String cuicui = "Say cuicui little bird";
    String id = (new CuiCuiCommandHandler(messageRepository, eventBus)).execute(new CuiCuiCommand(cuicui)).value();

    // when
    CommandResponse<String> response = handler.execute(new LikeCuiCuiCommand(id));

    // then
    assertThat(response.success()).isTrue();
    Message message = messageRepository.findById(id).get();
    assertThat(message).isEqualTo(Message.builder()
      ._id(message._id)
      .message(cuicui)
      .like(1)
      .build()
    );
  }

  @Test
  public void like_a_cuicui_and_check_event_is_emitted() {
    // given
    String cuicui = "Say cuicui little bird";
    String id = (new CuiCuiCommandHandler(messageRepository, eventBus)).execute(new CuiCuiCommand(cuicui)).value();

    // when
    CommandResponse<String> response = handler.execute(new LikeCuiCuiCommand(id));

    // then
    assertThat(response.success()).isTrue();
    assertThat(testEventHandler.events()).containsExactly(
      new CuiCuiCreated(id, cuicui),
      new CuiCuiLiked(id)
    );
  }
}
