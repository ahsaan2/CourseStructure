package com.example.coursestructure.service;

import com.example.coursestructure.model.CourseDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseSearchService {
    private final ElasticsearchOperations elasticsearchOperations;

    public Page<CourseDocument> searchCourses(
            String query,
            Integer minAge,
            Integer maxAge,
            Double minPrice,
            Double maxPrice,
            String category,
            String type,
            LocalDate nextSessionDate,
            String sort,
            int page,
            int size
    ) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        // Full-text search on title and description (multi-match equivalent)
        if (query != null && !query.isEmpty()) {
            List<Query> shouldQueries = new ArrayList<>();
            shouldQueries.add(MatchQuery.of(m -> m.field("title").query(query))._toQuery());
            shouldQueries.add(MatchQuery.of(m -> m.field("description").query(query))._toQuery());
            boolQuery.should(shouldQueries);
            boolQuery.minimumShouldMatch("1");
        }

        // Range filters
        if (minAge != null || maxAge != null) {
            RangeQuery.Builder ageRange = new RangeQuery.Builder().field("age");
            if (minAge != null) ageRange.gte(JsonData.of(minAge));
            if (maxAge != null) ageRange.lte(JsonData.of(maxAge));
            boolQuery.filter(ageRange.build()._toQuery());
        }
        if (minPrice != null || maxPrice != null) {
            RangeQuery.Builder priceRange = new RangeQuery.Builder().field("price");
            if (minPrice != null) priceRange.gte(JsonData.of(minPrice));
            if (maxPrice != null) priceRange.lte(JsonData.of(maxPrice));
            boolQuery.filter(priceRange.build()._toQuery());
        }

        // Exact filters
        if (category != null && !category.isEmpty()) {
            boolQuery.filter(TermQuery.of(t -> t.field("category.keyword").value(category))._toQuery());
        }
        if (type != null && !type.isEmpty()) {
            boolQuery.filter(TermQuery.of(t -> t.field("type.keyword").value(type))._toQuery());
        }

        // Date filter for nextSessionDate
        if (nextSessionDate != null) {
            RangeQuery.Builder dateRange = new RangeQuery.Builder().field("startDate");
            dateRange.gte(JsonData.of(nextSessionDate.toString()));
            boolQuery.filter(dateRange.build()._toQuery());
        }

        // Sorting
        Sort sortObj = Sort.by("startDate").ascending(); // default
        if (sort != null) {
            switch (sort) {
                case "priceAsc":
                    sortObj = Sort.by("price").ascending();
                    break;
                case "priceDesc":
                    sortObj = Sort.by("price").descending();
                    break;
                default:
                    sortObj = Sort.by("startDate").ascending();
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolQuery.build())))
                .withPageable(pageable)
                .build();

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(nativeQuery, CourseDocument.class);
        List<CourseDocument> results = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        return new PageImpl<>(results, pageable, searchHits.getTotalHits());
    }
} 