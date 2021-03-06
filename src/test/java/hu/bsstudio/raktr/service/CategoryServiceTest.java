package hu.bsstudio.raktr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import hu.bsstudio.raktr.dao.CategoryDao;
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
    private CategoryDao mockCategoryDao;

    @Mock
    private Category mockCategoryRequest;

    private Category category;
    private CategoryService underTest;

    @BeforeEach
    void init() {
        initMocks(this);
        underTest = spy(new CategoryService(mockCategoryDao));

        given(mockCategoryRequest.getName()).willReturn(NAME);

        category = spy(Category.builder()
            .withName(NAME)
            .build());
    }

    @Test
    void testCreateCategory() {
        //given
        doReturn(category).when(mockCategoryDao).save(any(Category.class));

        //when
        final Category result = underTest.create(mockCategoryRequest);

        //then
        verify(mockCategoryDao).save(mockCategoryRequest);
        assertEquals(result.getName(), mockCategoryRequest.getName());
    }

    @Test
    void testDelete() {
        //given

        //when
        underTest.delete(category);

        //then
        verify(mockCategoryDao).delete(category);
    }

    @Test
    void testUpdate() {
        //given
        mockCategoryRequest = Category.builder()
            .withId(CATEGORY_ID)
            .withName(UPDATED_NAME)
            .build();

        given(mockCategoryDao.findById(CATEGORY_ID)).willReturn(Optional.ofNullable(category));
        doReturn(category).when(mockCategoryDao).save(category);

        //when
        category = underTest.update(mockCategoryRequest);

        //then
        verify(category).setName(UPDATED_NAME);
        verify(mockCategoryDao).save(category);
        assertEquals(UPDATED_NAME, category.getName());
    }

    @Test
    void testUpdateCategoryNotFound() {
        //given
        given(mockCategoryDao.findById(CATEGORY_ID)).willReturn(Optional.empty());

        //when

        //then
        assertThrows(ObjectNotFoundException.class, () -> underTest.update(mockCategoryRequest));
    }

    @Test
    void testGetAll() {
        //given
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        doReturn(categories).when(mockCategoryDao).findAll();

        //when
        List<Category> fetchedCategories = underTest.getAll();

        //then
        assertEquals(categories, fetchedCategories);
    }

}
