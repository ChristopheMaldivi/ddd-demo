package example.cuicui.app.message.query.projection;

import example.cuicui.app.message.domain.Message;
import example.cuicui.app.message.events.CuiCuiCreated;
import example.cuicui.app.message.events.CuiCuiLiked;
import example.cuicui.app.message.events.MessageEvent;
import example.cuicui.app.message.infrastructure.MessageEventInMemoryRepository;
import example.cuicui.app.message.infrastructure.persistence.MessageEventRepository;
import io.vavr.collection.List;
import org.junit.Test;
import org.tophe.cqrses.event.Event;

import static org.assertj.core.api.Assertions.assertThat;

public class MessagesProjectionTest {
  MessageEventRepository eventRepository = new MessageEventInMemoryRepository();
  MessagesProjection projection = new MessagesProjection(eventRepository);

  @Test
  public void init_projection_with_a_created_message() {
    // given
    List<MessageEvent> events = List.of(new CuiCuiCreated("0", "cui"));
    eventRepository.saveAll(events);

    // when
    projection.init();

    // then
    assertThat(projection.get("0").get()).isEqualTo(Message.builder()
      ._id("0")
      .message("cui")
      .build()
    );
  }

  @Test
  public void init_projection_with_a_created_message_then_trig_a_like_event() {
    // given
    List<MessageEvent> events = List.of(new CuiCuiCreated("0", "cui"));
    eventRepository.saveAll(events);

    // when
    projection.init();
    projection.onEvent(new CuiCuiLiked("0"));

    // then
    assertThat(projection.get("0").get()).isEqualTo(Message.builder()
      ._id("0")
      .message("cui")
      .like(1)
      .build()
    );
  }

  @Test
  public void init_projection_with_a_created_message_already_liked_then_trig_a_like_event() {
    // given
    List<MessageEvent> events = List.of(
      new CuiCuiCreated("0", "cui"),
      new CuiCuiLiked("0")
    );
    eventRepository.saveAll(events);

    // when
    projection.init();
    projection.onEvent(new CuiCuiLiked("0"));

    // then
    assertThat(projection.get("0").get()).isEqualTo(Message.builder()
      ._id("0")
      .message("cui")
      .like(2)
      .build()
    );
  }
}