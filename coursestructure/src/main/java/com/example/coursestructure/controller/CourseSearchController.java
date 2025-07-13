package com.example.coursestructure.controller;

import com.example.coursestructure.model.CourseDocument;
import com.example.coursestructure.service.CourseSearchService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class CourseSearchController {
    private final CourseSearchService courseSearchService;

    @GetMapping
    public SearchResponse searchCourses(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "minAge", required = false) Integer minAge,
            @RequestParam(value = "maxAge", required = false) Integer maxAge,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "sort", required = false, defaultValue = "upcoming") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        String sortParam = sort;
        if (sort.equals("upcoming")) {
            sortParam = null; // default sort by nextSessionDate
        }
        Page<CourseDocument> resultPage = courseSearchService.searchCourses(
                q, minAge, maxAge, minPrice, maxPrice, category, type, startDate, sortParam, page, size
        );
        List<CourseSummary> courses = resultPage.getContent().stream().map(CourseSummary::from).collect(Collectors.toList());
        return new SearchResponse(resultPage.getTotalElements(), courses);
    }

    @Data
    public static class SearchResponse {
        private final long total;
        private final List<CourseSummary> courses;
    }

    @Data
    public static class CourseSummary {
        private final String id;
        private final String title;
        private final String category;
        private final double price;
        private final LocalDate nextSessionDate;

        public static CourseSummary from(CourseDocument doc) {
            return new CourseSummary(
                    doc.getId(),
                    doc.getTitle(),
                    doc.getCategory(),
                    doc.getPrice(),
                    doc.getStartDate()
            );
        }
    }
} 