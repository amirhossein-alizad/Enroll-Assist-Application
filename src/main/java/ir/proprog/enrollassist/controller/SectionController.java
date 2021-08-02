package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@RestController
@RequestMapping("/sections")
public class SectionController {
    SectionRepository sectionRepository;
    EnrollmentListRepository enrollmentListRepository;
    CourseRepository courseRepository;

    public SectionController(SectionRepository sectionRepository, EnrollmentListRepository enrollmentListRepository) {
        this.sectionRepository = sectionRepository;
        this.enrollmentListRepository = enrollmentListRepository;
    }

    @GetMapping
    public Iterable<SectionView> all() {
        return StreamSupport.stream(sectionRepository.findAll().spliterator(), false).map(SectionView::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SectionView one(@PathVariable Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        return new SectionView(section);
    }

    @GetMapping("/demands")
    public Iterable<SectionDemandView> allDemands() {
//        return enrollmentListRepository.findDemandForAllSections().forEach((SectionDemandView s) -> s.setSectionView(sectionRepository.findById(s.getSectionId()).orElseThrow()));
        List<SectionDemandView> demands = enrollmentListRepository.findDemandForAllSections();
        for (SectionDemandView demand : demands) {
            demand.setSectionView(sectionRepository.findById(demand.getSectionId()).orElseThrow());
        }
        return demands;
    }

    @PostMapping("/addSection")
    public Section addNewSection(@RequestBody SectionView section) {
        Course course = this.courseRepository.findById(section.getCourseId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        try {
            Section newSection = new Section(course, section.getSectionNo());
            sectionRepository.save(newSection);
            return newSection;
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Section number is not valid.");
        }
    }

}

