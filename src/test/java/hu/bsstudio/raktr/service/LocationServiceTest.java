package hu.bsstudio.raktr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import hu.bsstudio.raktr.dao.LocationDao;
import hu.bsstudio.raktr.model.Location;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

final class LocationServiceTest {

    private static final Long LOCATION_ID = 1L;
    private static final String NAME = "location";
    private static final String UPDATED_NAME = "location2";

    @Mock
    private LocationDao mockLocationDao;

    @Mock
    private Location mockLocationRequest;

    private Location location;
    private LocationService underTest;

    @BeforeEach
    void init() {
        initMocks(this);
        underTest = spy(new LocationService(mockLocationDao));

        given(mockLocationRequest.getName()).willReturn(NAME);

        location = spy(Location.builder()
            .withName(NAME)
            .build());
    }

    @Test
    void testCreateLocation() {
        //given
        doReturn(location).when(mockLocationDao).save(any(Location.class));

        //when
        final Location result = underTest.create(mockLocationRequest);

        //then
        verify(mockLocationDao).save(mockLocationRequest);
        assertEquals(result.getName(), mockLocationRequest.getName());
    }

    @Test
    void testDelete() {
        //given

        //when
        underTest.delete(location);

        //then
        verify(mockLocationDao).delete(location);
    }

    @Test
    void testUpdate() {
        //given
        mockLocationRequest = Location.builder()
            .withId(LOCATION_ID)
            .withName(UPDATED_NAME)
            .build();

        doReturn(location).when(mockLocationDao).getOne(LOCATION_ID);
        doReturn(location).when(mockLocationDao).save(location);

        //when
        location = underTest.update(mockLocationRequest);

        //then
        verify(location).setName(UPDATED_NAME);
        verify(mockLocationDao).save(location);
        assertEquals(UPDATED_NAME, location.getName());
    }

    @Test
    void testGetAll() {
        //given
        List<Location> locations = new ArrayList<>();
        locations.add(location);
        doReturn(locations).when(mockLocationDao).findAll();

        //when
        List<Location> fetchedLocations = underTest.getAll();

        //then
        assertEquals(locations, fetchedLocations);
    }
}
