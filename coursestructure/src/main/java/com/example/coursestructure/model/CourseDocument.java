package com.example.coursestructure.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;

@Data
@Document(indexName = "courses")
public class CourseDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String instructor;

    @Field(type = FieldType.Double)
    private double price;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Field(type = FieldType.Keyword)
    private List<String> tags;
} 