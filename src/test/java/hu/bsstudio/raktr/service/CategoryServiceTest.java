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

import hu.bsstudio.raktr.repository.CategoryRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

final class CategoryServiceTest {

    private static final Long CATEGORY_ID = 1L;
    private static final String NAME = "category";
    private static final String UPDATED_NAME = "category2";

    @Mock
    private CategoryRepository mockCategoryRepository;

    @Mock
    private Category mockCategoryRequest;

    private Category category;
    private CategoryService underTest;

    @BeforeEach
    void init() {
        initMocks(this);
        underTest = spy(new CategoryService(mockCategoryRepository));

        given(mockCategoryRequest.getName()).willReturn(NAME);

        category = spy(Category.builder()
            .withId(CATEGORY_ID)
            .withName(NAME)
            .build());
    }

    @Test
    void testCreateCategory() {
        //given
        doReturn(category).when(mockCategoryRepository).save(any(Category.class));

        //when
        final var result = underTest.create(mockCategoryRequest);

        //then
        verify(mockCategoryRepository).save(mockCategoryRequest);
        assertEquals(result.get().getName(), mockCategoryRequest.getName());
    }

    @Test
    void testDelete() {
        //given
        given(mockCategoryRepository.findById(CATEGORY_ID)).willReturn(Optional.of(category));

        //when
        Optional<Category> deleted = underTest.delete(category);

        //then
        assertTrue(deleted.isPresent());
        assertEquals(deleted.get(), category);
        verify(mockCategoryRepository).delete(any(Category.class));
    }

    @Test
    void testCannotDeleteNotFound() {
        given(mockCategoryRepository.findById(CATEGORY_ID)).willReturn(Optional.empty());

        var deleted = underTest.delete(category);

        assertTrue(deleted.isEmpty());
    }

    @Test
    void testUpdate() {
        //given
        mockCategoryRequest = Category.builder()
            .withId(CATEGORY_ID)
            .withName(UPDATED_NAME)
            .build();

        given(mockCategoryRepository.findById(CATEGORY_ID)).willReturn(Optional.ofNullable(category));
        doReturn(category).when(mockCategoryRepository).save(category);

        //when
        final var updatedCategory = underTest.update(mockCategoryRequest);

        //then
        verify(category).setName(UPDATED_NAME);
        verify(mockCategoryRepository).save(category);
        assertEquals(UPDATED_NAME, updatedCategory.get().getName());
    }

    @Test
    void testUpdateCategoryNotFound() {
        //given
        given(mockCategoryRepository.findById(CATEGORY_ID)).willReturn(Optional.empty());

        //when
        Optional<Category> updated = underTest.update(mockCategoryRequest);

        //then
        assertTrue(updated.isEmpty());
    }

    @Test
    void testGetAll() {
        //given
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        doReturn(categories).when(mockCategoryRepository).findAll();

        //when
        List<Category> fetchedCategories = underTest.getAll();

        //then
        assertEquals(categories, fetchedCategories);
    }

}
