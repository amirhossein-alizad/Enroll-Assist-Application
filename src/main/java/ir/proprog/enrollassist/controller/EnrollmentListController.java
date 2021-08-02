package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.EnrollmentList;
import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/lists")
public class EnrollmentListController {
    private EnrollmentListRepository enrollmentListRepository;
    private SectionRepository sectionRepository;
    private StudentRepository studentRepository;

    static class NewEnrollmentList {
        String studentNumber;
        String listName;
        public NewEnrollmentList() {
        }
        public NewEnrollmentList(String studentNumber, String listName) {
            this.studentNumber = studentNumber;
            this.listName = listName;
        }
    }

    public EnrollmentListController(EnrollmentListRepository enrollmentListRepository, SectionRepository sectionRepository, StudentRepository studentRepository) {
        this.enrollmentListRepository = enrollmentListRepository;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public Iterable<EnrollmentListView> all() {
        return StreamSupport.stream(enrollmentListRepository.findAll().spliterator(), false).map(EnrollmentListView::new).collect(Collectors.toList());
    }

    @PostMapping(
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public EnrollmentListView addOne(@RequestBody NewEnrollmentList req) {
        Student student = studentRepository.findStudentByStudentNumber(req.studentNumber);
        EnrollmentList enrollmentList = new EnrollmentList(req.listName, student);
        enrollmentListRepository.save(enrollmentList);
        return new EnrollmentListView(enrollmentList);
    }

    @GetMapping("/{id}")
    public EnrollmentListView one(@PathVariable Long id) {
        EnrollmentList enrollmentList = enrollmentListRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));
        return new EnrollmentListView(enrollmentList);
    }

    @GetMapping("/{id}/sections")
    public Iterable<SectionView> getListSections(@PathVariable Long id) {
        EnrollmentList enrollmentList = enrollmentListRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));
        return enrollmentList.getSections().stream().map(SectionView::new).collect(Collectors.toList());
    }

    @PutMapping("/{listId}/sections/{sectionId}")
    public Iterable<SectionView> addSection(@PathVariable Long listId, @PathVariable Long sectionId) {
        EnrollmentList enrollmentList = enrollmentListRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        enrollmentList.addSection(section);
        enrollmentListRepository.save(enrollmentList);
        return enrollmentList.getSections().stream().map(SectionView::new).collect(Collectors.toList());
    }

    @GetMapping("/{listId}/check")
    public EnrollmentCheckResultView checkRegulations(@PathVariable Long listId) {
        EnrollmentList enrollmentList = enrollmentListRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));

        return new EnrollmentCheckResultView(enrollmentList.checkEnrollmentRules());
    }
}
