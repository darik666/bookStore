package ru.aston.controller.authorController;

import org.springframework.web.bind.annotation.*;
import ru.aston.dto.AuthorDto.AuthorDto;
import ru.aston.dto.AuthorDto.AuthorDtoShort;
import ru.aston.service.AuthorService;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    private AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/{author_id}")
    public AuthorDto getAuthorById(@PathVariable("author_id") int authorId) {
        return authorService.getAuthorById(authorId);
    }

    @GetMapping
    public List<AuthorDto> getAllAuthors() {
        return authorService.getAllAuthors();
    }

    @PostMapping
    public AuthorDto createAuthor(@RequestBody AuthorDtoShort authorDtoShort) {
        return authorService.createUser(authorDtoShort);
    }

    @DeleteMapping("/{author_id}")
    public void deleteAuthor(@PathVariable("author_id") int authorId) {
        authorService.deleteAuthor(authorId);
    }
}
