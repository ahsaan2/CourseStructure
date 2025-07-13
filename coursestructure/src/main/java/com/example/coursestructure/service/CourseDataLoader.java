package com.example.coursestructure.service;

import com.example.coursestructure.model.CourseDocument;
import com.example.coursestructure.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseDataLoader implements ApplicationRunner {
    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (courseRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("sample-courses.json");
            try (InputStream is = resource.getInputStream()) {
                List<CourseDocument> courses = objectMapper.readValue(is, new TypeReference<List<CourseDocument>>() {});
                courseRepository.saveAll(courses);
                System.out.println("Sample courses indexed into Elasticsearch.");
            }
        }
    }
} 