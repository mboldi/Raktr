package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.LocationDao;
import hu.bsstudio.raktr.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

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
        verify(mockLocationRequest).getName();
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
}
