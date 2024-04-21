package com.javaprojects.tvshowapi.controllers;

import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.services.CharacterService;
import com.javaprojects.tvshowapi.services.RequestCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/characters")
@AllArgsConstructor
@Tag(name = "Персонажи", description = "Управляет персонажами сериалов")
public class CharacterController {
    private final CharacterService characterService;
    private final RequestCounterService requestCounterService;

    @Operation(summary = "Показать всех персонажей")
    @GetMapping("/get-all")
    public List<Character> getCharacters() {
        requestCounterService.increment();
        return characterService.getCharacters();
    }

    @Operation(summary = "Поиск персонажей по имени")
    @GetMapping("/get")
    public List<Character> searchByName(@Parameter(description = "Имя персонажа")
                                        @RequestParam(required = false) final String name) {
        requestCounterService.increment();
        return characterService.searchByName(name);
    }

    @Operation(summary = "Поиск персонажей по названию сериала")
    @GetMapping("/get-by-title")
    public List<Character> searchByTVShowTitle(@Parameter(description = "Имя персонажа")
                                               @RequestParam(required = false) final String title) {
        requestCounterService.increment();
        return characterService.searchByTVShowTitle(title);
    }

    @Operation(summary = "Добавление персонажа", description = "Указать хотя бы имя персонажа и ID его сериала")
    @PostMapping("/post")
    public ResponseEntity<String> insertCharacter(@Parameter(description = "ID сериала")
                                                  @RequestParam(required = false) final Long id,
                                                  @Parameter(description = "Тело персонажа")
                                                  @RequestBody(required = false) final Character character) {
        requestCounterService.increment();
        return characterService.insertCharacter(id, character);
    }

    @Operation(summary = "Удаление персонажа", description = "Необходимо указать ID персонажа")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCharacter(@Parameter(description = "ID персонажа")
                                                  @RequestParam(required = false) final Long id) {
        requestCounterService.increment();
        return characterService.deleteCharacter(id);
    }

    @Operation(summary = "Обновить персонажа", description = "Необходимо указать хотя бы ID и имя персонажа")
    @PutMapping("/update")
    public ResponseEntity<String> updateCharacter(@Parameter(description = "Тело персонажа")
                                                  @RequestBody(required = false) final Character character) {
        requestCounterService.increment();
        return characterService.updateCharacter(character);
    }

    @Operation(summary = "Добавление персонажей", description = "Указать хотя бы имя персонажа и ID его сериала")
    @PostMapping("/post-more")
    public ResponseEntity<String> insertCharacters(@Parameter(description = "ID сериала")
                                                   @RequestParam(required = false) final Long id,
                                                   @Parameter(description = "Тело")
                                                   @RequestBody(required = false) final Character[] characters) {
        requestCounterService.increment();
        Stream.of(characters).forEach(c -> characterService.insertCharacter(id, c));
        return ResponseEntity.ok("Characters are saved successfully");
    }
}
