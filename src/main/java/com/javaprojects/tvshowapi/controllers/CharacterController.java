package com.javaprojects.tvshowapi.controllers;

import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.services.CharacterService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/characters")
@AllArgsConstructor
public class CharacterController {
    private final CharacterService characterService;

    @GetMapping("/get-all")
    public List<Character> getCharacters() {
        return characterService.getCharacters();
    }

    @GetMapping("/get")
    public List<Character> searchByName(@RequestParam(required = false) String name) {
        return characterService.searchByName(name);
    }

    @GetMapping("/get-by-title")
    public List<Character> searchByTVShowTitle(@RequestParam(required = false) String title) {
        return characterService.searchByTVShowTitle(title);
    }

    @PostMapping("/post")
    public void insertCharacter(@RequestParam(required = false) Long id, @RequestBody(required = false) Character character) {
        characterService.insertCharacter(id, character);
    }

    @DeleteMapping("/delete")
    public void deleteCharacter(@RequestParam(required = false) Long id) {
        characterService.deleteCharacter(id);
    }

    @PutMapping("/update")
    public void updateCharacter(@RequestBody(required = false) Character character) {
        characterService.updateCharacter(character);
    }
}
