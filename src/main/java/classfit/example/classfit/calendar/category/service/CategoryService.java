package classfit.example.classfit.calendar.category.service;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.calendar.category.domain.Category;
import classfit.example.classfit.calendar.category.domain.enumType.CategoryColor;
import classfit.example.classfit.calendar.category.dto.request.CategoryCreateRequest;
import classfit.example.classfit.calendar.category.dto.request.CategoryUpdateRequest;
import classfit.example.classfit.calendar.category.dto.response.CategoryCreateResponse;
import classfit.example.classfit.calendar.category.dto.response.CategoryListResponse;
import classfit.example.classfit.calendar.category.dto.response.CategoryResponse;
import classfit.example.classfit.calendar.category.repository.CategoryRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.calendar.memberCalendar.domain.enumType.CalendarType;
import classfit.example.classfit.calendar.memberCalendar.domain.MemberCalendar;
import classfit.example.classfit.calendar.memberCalendar.service.MemberCalendarService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final MemberCalendarService memberCalendarService;

    @Transactional
    public CategoryCreateResponse addCategory(@AuthMember Member member, CategoryCreateRequest request) {
        Category category = buildCategory(member, request);
        Category savedCategory = categoryRepository.save(category);
        return CategoryCreateResponse.of(savedCategory.getId(), savedCategory.getName(),
                String.valueOf(savedCategory.getColor()), savedCategory.getMemberCalendar().getType());
    }

    private Category buildCategory(Member member, CategoryCreateRequest request) {
        MemberCalendar memberCalendar = getMemberCalendarByMemberAndType(member, request.type());
        String uniqueName = createUniqueCategoryName(request.name(), -1L);

        return Category.builder()
                .name(uniqueName)
                .color(CategoryColor.valueOf(request.color()))
                .memberCalendar(memberCalendar)
                .build();
    }

    private MemberCalendar getMemberCalendarByMemberAndType(Member member, CalendarType type) {
        return memberCalendarService.getMemberCalendar(member, type);
    }

    private String createUniqueCategoryName(String baseName, Long categoryId) {
        String uniqueName = baseName;
        int count = 1;

        while (isDuplicateName(uniqueName, categoryId)) {
            uniqueName = baseName + " (" + count + ")";
            count++;
        }
        return uniqueName;
    }

    private boolean isDuplicateName(String name, Long categoryId) {
        return categoryRepository.existsByNameAndIdNot(name, categoryId);
    }

    @Transactional(readOnly = true)
    public CategoryListResponse getCategories(@AuthMember Member member) {
        MemberCalendar personalCalendar = getMemberCalendarByMemberAndType(member, CalendarType.PERSONAL);
        List<CategoryResponse> personalCategories = classifyAndSortCategories(personalCalendar);

        List<CategoryResponse> sharedCategories = classifyAndSortSharedCategories(member);

        return CategoryListResponse.of(personalCategories, sharedCategories);
    }

    private List<CategoryResponse> classifyAndSortCategories(MemberCalendar memberCalendar) {
        return categoryRepository.findByMemberCalendar(memberCalendar)
                .stream()
                .map(category -> CategoryResponse.of(
                        category.getId(),
                        category.getName(),
                        category.getColor()
                ))
                .sorted(Comparator.comparing(CategoryResponse::name))
                .collect(Collectors.toList());
    }

    private List<CategoryResponse> classifyAndSortSharedCategories(Member member) {
        Long academyId = member.getAcademy().getId();

        return categoryRepository.findByMemberCalendarTypeAndAcademyId(CalendarType.SHARED, academyId)
                .stream()
                .map(category -> CategoryResponse.of(
                        category.getId(),
                        category.getName(),
                        category.getColor()
                ))
                .sorted(Comparator.comparing(CategoryResponse::name))
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest request) {
        Category category = getCategoryById(categoryId);

        String uniqueName = createUniqueCategoryName(request.newName(), categoryId);
        category.updateNameAndColor(uniqueName, request.newColor());

        return CategoryResponse.of(category.getId(), category.getName(), category.getColor());
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ClassfitException(ErrorCode.EVENT_CATEGORY_NOT_FOUND));
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        categoryRepository.delete(category);
    }
}
