package ir.proprog.enrollassist.controller.faculty;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.major.MajorView;
import ir.proprog.enrollassist.domain.faculty.Faculty;
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

    @PostMapping( consumes = {MediaType.APPLICATION_JSON_VALUE})
    public FacultyAddView addOne(@RequestBody FacultyAddView facultyAddView) {
        ExceptionList exceptions = new ExceptionList();
        if (facultyRepository.findByFacultyName(facultyAddView.getFacultyName()).isPresent())
            exceptions.addNewException(new Exception("Faculty with name " + facultyAddView.getFacultyName() + " exists."));
        Faculty faculty = null;
        try {
            faculty = new Faculty(facultyAddView.getFacultyName());
        } catch (ExceptionList exceptionList) {
            exceptions.addExceptions(exceptionList.getExceptions());
        }
        if (exceptions.hasException())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exceptions.toString());
        facultyRepository.save(faculty);
        return new FacultyAddView(faculty);
    }
}
