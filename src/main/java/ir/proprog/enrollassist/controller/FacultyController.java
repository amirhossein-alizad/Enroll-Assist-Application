package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.repository.FacultyRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
