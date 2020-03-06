package hu.bsstudio.raktr.model;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DomainAuditModelTest {

    private static DomainAuditModel mockDomainAuditModel;

    @BeforeEach
    void init() {
        mockDomainAuditModel = mock(
            DomainAuditModel.class,
            CALLS_REAL_METHODS
        );
    }

    @Test
    void testGetCreatedAt() {
        assertNull(mockDomainAuditModel.getCreatedAt());
    }

    @Test
    void testGetUpdatedAt() {
        assertNull(mockDomainAuditModel.getUpdatedAt());
    }
}
