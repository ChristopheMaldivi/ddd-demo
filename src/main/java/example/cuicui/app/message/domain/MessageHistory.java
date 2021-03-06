package example.cuicui.app.message.domain;

import example.cuicui.app.message.domain.Message.MessageBuilder;
import example.cuicui.app.message.events.CuiCuiCreated;
import example.cuicui.app.message.events.CuiCuiLiked;
import org.tophe.cqrses.event.Event;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class MessageHistory {
  public static Message apply(Message message, Event event) {
    return apply(message.toBuilder(), event).build();
  }

  public static Message from(Iterable<Event> events) {
    MessageBuilder builder = Message.builder();
    for (Event event : events) {
      builder = apply(builder, event);
    }
    return builder.build();
  }

  private static MessageBuilder applyCuicuiCreated(CuiCuiCreated e) {
    return Message.builder()
      ._id(e.aggregateId)
      .message(e.message);
  }

  private static MessageBuilder applyCuiCuiLiked(MessageBuilder builder) {
    Message message = builder.build();
    return message.toBuilder().like(message.like == null ? 1 : message.like + 1);
  }

  static MessageBuilder apply(MessageBuilder builder, Event event) {
    return Match(event).of(

      Case($(instanceOf(
        CuiCuiCreated.class)), e -> applyCuicuiCreated(e)
      ),
      Case($(instanceOf(
        CuiCuiLiked.class)), e -> applyCuiCuiLiked(builder)
      ),

      Case($(), o -> {
        throw new IllegalStateException("No Matcher for event: " + event.getClass());
      })
    );
  }
}
