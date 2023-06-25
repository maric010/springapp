package com.example.db1.Models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;
    @ElementCollection
    @CollectionTable(name = "user_events", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "event_id")
    private Set<Long> participants = new HashSet<>();
    @ElementCollection
    @CollectionTable(name = "user_temp_events", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "event_id")
    private Set<Long> tempparticipants = new HashSet<>();


    private String description;

    private Integer cost;

    public Event() {

    }



    public Event(User author, String name, Set<Long> participants,Set<Long> tempparticipants, String description) {
        this.author = author;
        this.name = name;
        this.participants = participants;
        this.description = description;
        this.tempparticipants = tempparticipants;
    }
    public Set<Long> getTempparticipants() {
        return tempparticipants;
    }

    public void setTempparticipants(Set<Long> tempparticipants) {
        this.tempparticipants = tempparticipants;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Long> participants) {
        this.participants = participants;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
