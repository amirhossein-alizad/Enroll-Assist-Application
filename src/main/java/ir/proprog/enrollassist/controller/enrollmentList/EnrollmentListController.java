package ir.proprog.enrollassist.controller.enrollmentList;

import ir.proprog.enrollassist.controller.section.SectionView;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.student.StudentNumber;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@AllArgsConstructor
@RequestMapping("/lists")
public class EnrollmentListController {
    private EnrollmentListRepository enrollmentListRepository;
    private SectionRepository sectionRepository;
    private StudentRepository studentRepository;

    @GetMapping
    public Iterable<EnrollmentListView> all() {
        return StreamSupport.stream(enrollmentListRepository.findAll().spliterator(), false).map(EnrollmentListView::new).collect(Collectors.toList());
    }

    @PostMapping(
            value="/{studentNo}",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public EnrollmentListView addOne(@PathVariable String studentNo, @RequestBody EnrollmentListView req) {
        Student student = studentRepository.findByStudentNumber(new StudentNumber(studentNo)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        if(req.getEnrollmentListName().equals(""))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EnrollmentList must have a name.");
        List<EnrollmentListView> lists = studentRepository.findAllListsForStudent(studentNo);
        for (EnrollmentListView e: lists)
            if (e.getEnrollmentListName().equals(req.getEnrollmentListName()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EnrollmentList with name " + req.getEnrollmentListName() + " already exists.");
        EnrollmentList enrollmentList = new EnrollmentList(req.getEnrollmentListName(), student);
        enrollmentListRepository.save(enrollmentList);
        return new EnrollmentListView(enrollmentList);
    }


    @GetMapping("/{id}")
    public EnrollmentListView one(@PathVariable Long id) {
        EnrollmentList enrollmentList = getEnrollmentList(id);
        return new EnrollmentListView(enrollmentList);
    }

    @GetMapping("/{id}/sections")
    public Iterable<SectionView> getListSections(@PathVariable Long id) {
        EnrollmentList enrollmentList = getEnrollmentList(id);
        return enrollmentList.getSections().stream().map(SectionView::new).collect(Collectors.toList());
    }

    @PutMapping("/{listId}/clear")
    public Iterable<SectionView> emptyList(@PathVariable Long listId) {
        EnrollmentList enrollmentList = getEnrollmentList(listId);
        enrollmentList.clear();
        enrollmentListRepository.save(enrollmentList);
        return enrollmentList.getSections().stream().map(SectionView::new).collect(Collectors.toList());
    }

    @PutMapping("/{listId}/sections/{sectionId}")
    public Iterable<SectionView> addSection(@PathVariable Long listId, @PathVariable Long sectionId) {
        EnrollmentList enrollmentList = getEnrollmentList(listId);
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        enrollmentList.addSection(section);
        enrollmentListRepository.save(enrollmentList);
        return enrollmentList.getSections().stream().map(SectionView::new).collect(Collectors.toList());
    }

    @DeleteMapping("/{listId}/sections/{sectionId}")
    public Iterable<SectionView> removeSection(@PathVariable Long listId, @PathVariable Long sectionId) {
        EnrollmentList enrollmentList = getEnrollmentList(listId);
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        enrollmentList.removeSection(section);
        enrollmentListRepository.save(enrollmentList);
        return enrollmentList.getSections().stream().map(SectionView::new).collect(Collectors.toList());
    }

    @GetMapping("/{listId}/check")
    public EnrollmentCheckResultView checkRegulations(@PathVariable Long listId) {
        EnrollmentList enrollmentList = getEnrollmentList(listId);
        return new EnrollmentCheckResultView(enrollmentList.checkEnrollmentRules());
    }

    private EnrollmentList getEnrollmentList(Long id) {
        return enrollmentListRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));
    }

}
