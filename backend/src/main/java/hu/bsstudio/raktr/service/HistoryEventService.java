package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.model.HistoryEvent;
import hu.bsstudio.raktr.model.User;
import hu.bsstudio.raktr.repository.HistoryEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HistoryEventService {

    private final HistoryEventRepository eventRepository;

    public HistoryEventService( final HistoryEventRepository historyEventRepository ) {
        this.eventRepository = historyEventRepository;
    }

    public final List<HistoryEvent> getAll() {
        List<HistoryEvent> historyEvents = eventRepository.findAll();
        log.info("History events fetched: {}", historyEvents);
        return historyEvents;
    }

    public final List<HistoryEvent> getAllOfUser( final User user ) {
        List<HistoryEvent> historyEvents = eventRepository.findByUser(user);
        log.info("History events fetched: {}", historyEvents);
        return historyEvents;
    }

}
