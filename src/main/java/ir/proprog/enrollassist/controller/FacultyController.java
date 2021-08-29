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

    @PostMapping( consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public FacultyView addOne(@RequestBody FacultyView facultyView) {
        ExceptionList exceptions = new ExceptionList();
        Set<Major> majors = new HashSet<>();
        for(Long id: facultyView.getMajors()){
            Optional<Major> major = majorRepository.findById(id);
            if(major.isEmpty()) {
                exceptions.addNewException(new Exception("Major with id:" + id + " does not exist."));
                continue;
            }
            majors.add(major.get());
        }
        if (exceptions.hasException())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exceptions.toString());
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
        for(Major m:majors)
            faculty.addMajor(m);
        facultyRepository.save(faculty);
        return new FacultyView(faculty);
    }
}
