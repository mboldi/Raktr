package hu.bsstudio.raktr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import hu.bsstudio.raktr.repository.LocationRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

final class LocationServiceTest {

    private static final Long LOCATION_ID = 1L;
    private static final String NAME = "location";
    private static final String UPDATED_NAME = "location2";

    @Mock
    private LocationRepository mockLocationRepository;

    @Mock
    private Location mockLocationRequest;

    private Location location;
    private LocationService underTest;

    @BeforeEach
    void init() {
        initMocks(this);
        underTest = spy(new LocationService(mockLocationRepository));

        given(mockLocationRequest.getName()).willReturn(NAME);

        location = spy(Location.builder()
            .withId(LOCATION_ID)
            .withName(NAME)
            .build());
    }

    @Test
    void testCreateLocation() {
        //given
        given(mockLocationRepository.findByName(NAME)).willReturn(Optional.empty());
        doReturn(location).when(mockLocationRepository).save(any(Location.class));

        //when
        final var result = underTest.create(mockLocationRequest);

        //then
        verify(mockLocationRepository).save(mockLocationRequest);
        assertEquals(result.get().getName(), mockLocationRequest.getName());
    }

    @Test
    void testDelete() {
        //given
        given(mockLocationRepository.findById(LOCATION_ID)).willReturn(Optional.of(location));

        //when
        final var deleted = underTest.delete(location);

        //then
        assertTrue(deleted.isPresent());
        assertEquals(deleted.get(), location);
        verify(mockLocationRepository).delete(location);
    }

    @Test
    void testCannotDeleteNotFound() {
        //given
        given(mockLocationRepository.findById(LOCATION_ID)).willReturn(Optional.empty());

        //when
        final var deleted = underTest.delete(location);

        //then
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testUpdate() {
        //given
        mockLocationRequest = Location.builder()
            .withId(LOCATION_ID)
            .withName(UPDATED_NAME)
            .build();

        given(mockLocationRepository.findById(LOCATION_ID)).willReturn(Optional.of(location));
        doReturn(location).when(mockLocationRepository).save(location);

        //when
        final var updatedLocation = underTest.update(mockLocationRequest);

        //then
        verify(location).setName(UPDATED_NAME);
        verify(mockLocationRepository).save(location);
        assertTrue(updatedLocation.isPresent());
        assertEquals(UPDATED_NAME, updatedLocation.get().getName());
    }

    @Test
    void testGetAll() {
        //given
        List<Location> locations = new ArrayList<>();
        locations.add(location);
        doReturn(locations).when(mockLocationRepository).findAll();

        //when
        List<Location> fetchedLocations = underTest.getAll();

        //then
        assertEquals(locations, fetchedLocations);
    }

    @Test
    void testUpdateLocationNotFound() {
        //given
        given(mockLocationRepository.findById(LOCATION_ID)).willReturn(Optional.empty());

        //when
        final var updatedLocation = underTest.update(mockLocationRequest);

        //then
        assertTrue(updatedLocation.isEmpty());
    }
}
