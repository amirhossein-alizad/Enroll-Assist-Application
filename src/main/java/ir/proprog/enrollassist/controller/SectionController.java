package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.*;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    @DeleteMapping("/{id}")
    public SectionView removeOne(@PathVariable Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        enrollmentListRepository.findEnrollmentListContainingSection(id).forEach(list -> {
            list.removeSection(section);
            enrollmentListRepository.save(list);
        });
        sectionRepository.delete(section);
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

    @PutMapping(value = "/{id}/setSchedule")
    public SectionView setSchedule(@RequestBody List<PresentationSchedule> schedule, @PathVariable Long id){
        Section section = this.sectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found."));
        ExceptionList exceptionList = new ExceptionList();
        for(PresentationSchedule ps: schedule){
            try {
                ps.validateFields();
            }catch (ExceptionList exception){
                exceptionList.addExceptions(exception.getExceptions());
            }
        }
        if(exceptionList.hasException())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptionList.toString());
        Set<PresentationSchedule> presentationScheduleSet = new HashSet<>(schedule);
        section.setPresentationSchedule(presentationScheduleSet);
        sectionRepository.save(section);
        return new SectionView(section);
    }

    @PutMapping(value = "/{id}/getExamTimeConflicts")
    public ConflictView getNumberOfExamTimeConflicts(@RequestBody ExamTime examTime, @PathVariable Long id) {
        Section section = this.sectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        int numberOfConflict = 0;
        List<EnrollmentList> enrollmentLists = enrollmentListRepository.findEnrollmentListContainingSection(id);
        for (EnrollmentList e:enrollmentLists) {
            try {
                if (e.makeExamTimeConflict(section, examTime))
                    numberOfConflict = numberOfConflict + 1;
            } catch (ExceptionList exceptionList) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptionList.toString());
            }
        }
        return new ConflictView(numberOfConflict);
    }

    @PutMapping(value = "/{id}/getScheduleConflicts")
    public ConflictView getNumberOfScheduleConflicts(@RequestBody List<PresentationSchedule> schedule, @PathVariable Long id) {
        Section section = this.sectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        int numberOfConflict = 0;
        List<EnrollmentList> enrollmentLists = enrollmentListRepository.findEnrollmentListContainingSection(id);
        for (EnrollmentList e: enrollmentLists) {
            if (e.makePresentationScheduleConflict(section, schedule))
                numberOfConflict = 1 + numberOfConflict;
        }
        return new ConflictView(numberOfConflict);
    }

}

