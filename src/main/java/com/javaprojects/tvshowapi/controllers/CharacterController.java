package com.javaprojects.tvshowapi.controllers;

import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.services.CharacterService;
import com.javaprojects.tvshowapi.services.RequestCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Controller
@RequestMapping("/characters")
@AllArgsConstructor
@Tag(name = "Персонажи", description = "Управляет персонажами сериалов")
public class CharacterController {
    private final CharacterService characterService;
    private final RequestCounterService requestCounterService;
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";

    @GetMapping
    public String showMainPage(Model model) {
        model.addAttribute("name");
        return "mainPage";
    }


    @Operation(summary = "Показать всех персонажей")
    @GetMapping("/get-all")
    public List<Character> getCharacters() {
        requestCounterService.increment();
        return characterService.getCharacters();
    }

    @Operation(summary = "Поиск персонажей по имени")
    @GetMapping("/get")
    public String searchByName(@Parameter(description = "Имя персонажа")
                               @RequestParam(required = false) final String name, Model model) {
        requestCounterService.increment();
        try {
            List<Character> characters;
            if (name.equals("")) {
                characters = characterService.getCharacters();
            } else {
                characters = characterService.searchByName(name);
            }
            model.addAttribute("characters", characters);
            return "searchByName";
        } catch (RuntimeException e) {
            model.addAttribute(MESSAGE, e.getMessage());
            return ERROR;
        }
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
    public String insertCharacter(@Parameter(description = "ID сериала")
                                  @ModelAttribute("title") final String title,
                                  @Parameter(description = "Тело персонажа")
                                  @ModelAttribute("character") final Character character,
                                  Model model) {
        if (title.equals("")) {
            return "insertCharacter";
        }
        requestCounterService.increment();
        try {
            characterService.insertCharacter(title, character);
            model.addAttribute(MESSAGE, "Персонаж успешно добавлен");
            return MESSAGE;
        } catch (RuntimeException e) {
            model.addAttribute(MESSAGE, e.getMessage());
            return ERROR;
        }
    }

    @Operation(summary = "Удаление персонажа", description = "Необходимо указать ID персонажа")
    @GetMapping("/delete")
    public String deleteCharacter(@Parameter(description = "ID персонажа")
                                  @RequestParam final Long id, Model model) {
        requestCounterService.increment();
        try {
            characterService.deleteCharacter(id);
        } catch (RuntimeException e) {
            model.addAttribute(MESSAGE, e.getMessage());
            return ERROR;
        }
        model.addAttribute(MESSAGE, "Персонаж успешно удален");
        return MESSAGE;
    }

    @GetMapping("/update-redirect")
    public String showUpdatePage(@RequestParam Long id, Model model) {
        model.addAttribute("character", characterService.findById(id));
        return "updateCharacter";
    }

    @Operation(summary = "Обновить персонажа", description = "Необходимо указать хотя бы ID и имя персонажа")
    @PostMapping("/update")
    public String updateCharacter(@Parameter(description = "Тело персонажа") final Character character, Model model) {
        requestCounterService.increment();
        try {
            characterService.updateCharacter(character);
        } catch (RuntimeException e) {
            model.addAttribute(MESSAGE, e.getMessage());
            return ERROR;
        }
        model.addAttribute(MESSAGE, "Персонаж успешно обновлен");
        return MESSAGE;
    }

    @Operation(summary = "Добавление персонажей", description = "Указать хотя бы имя персонажа и ID его сериала")
    @PostMapping("/post-more")
    public ResponseEntity<String> insertCharacters(@Parameter(description = "ID сериала")
                                                   @RequestParam(required = false) final String title,
                                                   @Parameter(description = "Тело")
                                                   @RequestBody(required = false) final Character[] characters) {
        requestCounterService.increment();
        Stream.of(characters).forEach(c -> characterService.insertCharacter(title, c));
        return ResponseEntity.ok("Characters are saved successfully");
    }
}
