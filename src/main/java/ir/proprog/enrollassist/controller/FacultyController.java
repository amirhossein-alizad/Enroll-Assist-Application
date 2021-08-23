package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.Faculty;
import ir.proprog.enrollassist.repository.FacultyRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@RestController
@RequestMapping("/faculties")
public class FacultyController {
    private final FacultyRepository facultyRepository;

    @GetMapping
    public Iterable<FacultyView> all() {
        return StreamSupport.stream(facultyRepository.findAll().spliterator(), false).map(FacultyView::new).collect(Collectors.toList());
    }

    @PostMapping( consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public FacultyView addOne(@RequestBody FacultyView facultyView) {
        ExceptionList exceptions = new ExceptionList();
        if (facultyRepository.findByFacultyName(facultyView.getFacultyName()).isPresent())
            exceptions.addNewException(new Exception("Faculty with name " + facultyView.getFacultyName() + " exists."));
        Faculty faculty = null;
        try {
            faculty = new Faculty(facultyView.getFacultyName());
        } catch (ExceptionList exceptionList) {
            exceptions.addExceptions(exceptionList.getExceptions());
        }
        if (exceptions.hasException())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptions.toString());
        facultyRepository.save(faculty);
        return new FacultyView(faculty);
    }
}
