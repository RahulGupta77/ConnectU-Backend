package com.rahulsproject.connectu.connections_service.service;

import com.rahulsproject.connectu.connections_service.auth.UserContextHolder;
import com.rahulsproject.connectu.connections_service.entity.Person;
import com.rahulsproject.connectu.connections_service.repository.PersonsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionsService {
    private final PersonsRepository personsRepository;

    public List<Person> getFirstDegreeConnections(){

        Long userId = UserContextHolder.getCurrentUserId();

        log.info("Getting first degree Connections for userId: {}", userId);

        return personsRepository.getFirstDegreeConnections(userId);
    }
}
