package com.javaprojects.tvshowapi.controllers;

import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.services.CharacterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
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

    @Operation(summary = "Показать всех персонажей")
    @GetMapping("/get-all")
    public List<Character> getCharacters() {
        return characterService.getCharacters();
    }

    @Operation(summary = "Поиск персонажей по имени")
    @GetMapping("/get")
    public List<Character> searchByName(@Parameter(description = "Имя персонажа")
                                        @RequestParam(required = false) final String name) {
        return characterService.searchByName(name);
    }

    @Operation(summary = "Поиск персонажей по названию сериала")
    @GetMapping("/get-by-title")
    public List<Character> searchByTVShowTitle(@Parameter(description = "Имя персонажа")
                                               @RequestParam(required = false) final String title) {
        return characterService.searchByTVShowTitle(title);
    }

    @Operation(summary = "Добавление персонажа", description = "Указать хотя бы имя персонажа и ID его сериала")
    @PostMapping("/post")
    public void insertCharacter(@Parameter(description = "ID сериала") @RequestParam(required = false) final Long id,
                                @Parameter(description = "Тело персонажа")
                                @RequestBody(required = false) final Character character) {
        characterService.insertCharacter(id, character);
    }

    @Operation(summary = "Удаление персонажа", description = "Необходимо указать ID персонажа")
    @DeleteMapping("/delete")
    public void deleteCharacter(@Parameter(description = "ID персонажа")
                                @RequestParam(required = false) final Long id) {
        characterService.deleteCharacter(id);
    }

    @Operation(summary = "Обновить персонажа", description = "Необходимо указать хотя бы ID и имя персонажа")
    @PutMapping("/update")
    public void updateCharacter(@Parameter(description = "Тело персонажа")
                                @RequestBody(required = false) final Character character) {
        characterService.updateCharacter(character);
    }

    @Operation(summary = "Добавление персонажей", description = "Указать хотя бы имя персонажа и ID его сериала")
    @PostMapping("/post-more")
    public void insertCharacters(@Parameter(description = "ID сериала") @RequestParam(required = false) final Long id,
                                @Parameter(description = "Тело")
                                @RequestBody(required = false) final Character[] characters){
        Stream.of(characters).forEach(c->characterService.insertCharacter(id,c));
    }
}
