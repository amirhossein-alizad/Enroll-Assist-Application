package ir.proprog.enrollassist.controller.section;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import ir.proprog.enrollassist.domain.section.ExamTime;
import ir.proprog.enrollassist.domain.section.PresentationSchedule;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@RestController
@AllArgsConstructor
@RequestMapping("/sections")
public class SectionController {
    SectionRepository sectionRepository;
    EnrollmentListRepository enrollmentListRepository;
    CourseRepository courseRepository;

    private Section getSection(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
    }

    @GetMapping
    public Iterable<SectionView> all() {
        return StreamSupport.stream(sectionRepository.findAll().spliterator(), false).map(SectionView::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public SectionView one(@PathVariable Long id) {
        Section section = getSection(id);
        return new SectionView(section);
    }

    @DeleteMapping("/{id}")
    public SectionView removeOne(@PathVariable Long id) {
        Section section = getSection(id);
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
            newSection = new Section(course, section.getSectionNo());
            if(section.getExamTime() != null)
                newSection.setExamTime(section.getExamTime());
            if(section.getSchedule().size() > 0)
                newSection.setPresentationSchedule(section.getSchedule());
            this.sectionRepository.save(newSection);
            return new SectionView(newSection);
        } catch (ExceptionList e) {
            exceptionList.addExceptions(e.getExceptions());
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptionList.toString());
    }

    @PutMapping("/{id}/setExamTime")
    public ExamTime setExamTime(@RequestBody ExamTime examTime, @PathVariable Long id) {
        Section section = getSection(id);
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
        Section section = getSection(id);
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
        section.setPresentationSchedule(new HashSet<>(schedule));
        sectionRepository.save(section);
        return new SectionView(section);
    }

    @PutMapping(value = "/{id}/getExamTimeConflicts")
    public ConflictView getNumberOfExamTimeConflicts(@RequestBody ExamTime examTime, @PathVariable Long id) {
        Section section = getSection(id);
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
        Section section = getSection(id);
        int numberOfConflict = 0;
        List<EnrollmentList> enrollmentLists = enrollmentListRepository.findEnrollmentListContainingSection(id);
        for (EnrollmentList e: enrollmentLists) {
            if (e.makePresentationScheduleConflict(section, schedule))
                numberOfConflict = 1 + numberOfConflict;
        }
        return new ConflictView(numberOfConflict);
    }

}

