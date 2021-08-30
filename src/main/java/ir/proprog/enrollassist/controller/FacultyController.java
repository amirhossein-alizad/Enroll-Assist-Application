package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.Faculty;
import ir.proprog.enrollassist.domain.Major;
import ir.proprog.enrollassist.repository.FacultyRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@RestController
@RequestMapping("/faculties")
public class FacultyController {
    private final FacultyRepository facultyRepository;
    private final MajorRepository majorRepository;

    @GetMapping
    public Iterable<FacultyView> all() {
        return StreamSupport.stream(facultyRepository.findAll().spliterator(), false).map(FacultyView::new).collect(Collectors.toList());
    }

    @PostMapping( consumes = {MediaType.APPLICATION_JSON_VALUE})
    public FacultyView addOne(@RequestBody String facultyName) {
        ExceptionList exceptions = new ExceptionList();
        if (facultyRepository.findByFacultyName(facultyName).isPresent())
            exceptions.addNewException(new Exception("Faculty with name " + facultyName + " exists."));
        Faculty faculty = null;
        try {
            faculty = new Faculty(facultyName);
        } catch (ExceptionList exceptionList) {
            exceptions.addExceptions(exceptionList.getExceptions());
        }
        if (exceptions.hasException())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptions.toString());
        facultyRepository.save(faculty);
        return new FacultyView(faculty);
    }
}
