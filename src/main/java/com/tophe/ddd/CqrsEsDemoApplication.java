package com.tophe.ddd;

import com.tophe.ddd.commands.CommandBus;
import com.tophe.ddd.example.message.command.CuiCuiCommandHandler;
import com.tophe.ddd.example.message.command.LikeCuiCuiCommandHandler;
import com.tophe.ddd.example.message.infrastructure.persistence.MessageRepository;
import com.tophe.ddd.example.message.query.GetMessageQueryHandler;
import com.tophe.ddd.infrastructure.event.Event;
import com.tophe.ddd.infrastructure.event.EventBus;
import com.tophe.ddd.infrastructure.persistence.EventRepository;
import com.tophe.ddd.queries.QueryBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CqrsEsDemoApplication {
  @Autowired
  MongoTemplate mongoTemplate;

  @Autowired
  EventRepository eventRepository;
  @Autowired
  MessageRepository messageRepository;

  @Bean
  public CommandBus commandBus() {
    EventBus eventBus = initEventBus();

    CommandBus commandBus = new CommandBus();
    commandBus.register(
      new CuiCuiCommandHandler(messageRepository, eventBus),
      new LikeCuiCuiCommandHandler(messageRepository, eventBus)
    );
    return commandBus;
  }

  private EventBus initEventBus() {
    EventBus eventBus = new EventBus(eventRepository);
    // FIXME eventBus.register(projections);
    return eventBus;
  }

  @Bean
  public QueryBus queryBus() {
    QueryBus queryBus = new QueryBus();
    queryBus.register(
      new GetMessageQueryHandler(messageRepository)
    );
    return queryBus;
  }

  @PostConstruct
  public void initEventAggregateIdIndex() throws NoSuchFieldException {
    String fieldName = "aggregateId";
    // let exception be raised if field name is not correct (due to a refactoring?)
    Event.class.getDeclaredField(fieldName);
    mongoTemplate.indexOps(Event.class).ensureIndex(new Index().on(fieldName, Sort.Direction.ASC));
  }


  public static void main(String[] args) {
    SpringApplication.run(CqrsEsDemoApplication.class, args);
  }
}