package com.example.db1.Repositories;

import com.example.db1.Models.Event;
import com.example.db1.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findAllByAuthor(User author);
    Event findFirstById(long id);
}
