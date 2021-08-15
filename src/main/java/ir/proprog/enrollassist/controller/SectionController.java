package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.ExamTime;
import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@RestController
@RequestMapping("/sections")
public class SectionController {
    SectionRepository sectionRepository;
    EnrollmentListRepository enrollmentListRepository;
    CourseRepository courseRepository;

    public SectionController(SectionRepository sectionRepository, EnrollmentListRepository enrollmentListRepository, CourseRepository courseRepository) {
        this.sectionRepository = sectionRepository;
        this.enrollmentListRepository = enrollmentListRepository;
        this.courseRepository = courseRepository;
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
        List<SectionDemandView> demands = enrollmentListRepository.findDemandForAllSections();
        for (SectionDemandView demand : demands) {
            demand.setSectionView(sectionRepository.findById(demand.getSectionId()).orElseThrow());
        }
        return demands;
    }

    @PostMapping
    public SectionView addNewSection(@RequestBody SectionView section) {
        Course course = this.courseRepository.findById(section.getCourseId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        ExceptionList exceptionList = new ExceptionList();
        List<Section> sections = this.sectionRepository.findOneSectionOfSpecialCourse(section.getCourseId(), section.getSectionNo());
        if (sections.size() != 0) {
            exceptionList.addNewException(new Exception("This section already exists."));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptionList.toString());
        }
        try {
            Section newSection;
            if (section.getExamTime() == null)
                newSection = new Section(course, section.getSectionNo());
            else
                newSection = new Section(course, section.getSectionNo(), section.getExamTime(), section.getSchedule());
            this.sectionRepository.save(newSection);
            return new SectionView(newSection);
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptionList.toString());
    }

    @PostMapping("/{id}")
    public ExamTime changeExamTime(@RequestBody ExamTime examTime, @PathVariable Long id) {
        Section section = this.sectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        try {
            section.setExamTime(examTime);
            this.sectionRepository.save(section);
        }catch (ExceptionList exceptionList) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptionList.toString());
        }
        return examTime;
    }

    @PutMapping("/{id}/setExamTime")
    public SectionView setSchedule(@RequestBody List<String> schedule, @PathVariable Long id){
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found."));
        try {
            section.setPresentationSchedule(schedule);
            sectionRepository.save(section);
            return new SectionView(section);
        } catch(ExceptionList e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.toString());
        }
    }


}

