package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.Faculty;
import ir.proprog.enrollassist.domain.Major;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.domain.StudentNumber;
import ir.proprog.enrollassist.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@RestController
@RequestMapping("/majors")
public class MajorController {
    private MajorRepository majorRepository;
    private FacultyRepository facultyRepository;

    @GetMapping
    public Iterable<MajorView> all() {
        return StreamSupport.stream(majorRepository.findAll().spliterator(), false).map(MajorView::new).collect(Collectors.toList());
    }

    @PostMapping(value = "/{facultyId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public MajorView addOne(@PathVariable Long facultyId, @RequestBody MajorView majorView) {
        Faculty faculty = this.facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty not found"));
        ExceptionList exceptions = new ExceptionList();
        if (majorRepository.findByMajorName(majorView.getMajorName()).isPresent())
            exceptions.addNewException(new Exception("Major with name " + majorView.getMajorName() + " exists."));
        Major major = null;
        try {
            major = new Major(majorView.getMajorNumber(), majorView.getMajorName());
        } catch (ExceptionList exceptionList) {
            exceptions.addExceptions(exceptionList.getExceptions());
        }
        if (exceptions.hasException())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptions.toString());
        faculty.addMajor(major);
        facultyRepository.save(faculty);
        majorRepository.save(major);
        return new MajorView(major);
    }
}
