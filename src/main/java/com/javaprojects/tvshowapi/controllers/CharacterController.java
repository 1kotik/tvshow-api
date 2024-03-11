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

    @GetMapping("/get")
    public List<Character> searchByName(@RequestParam(required = false) String name) {
        return characterService.searchByName(name);
    }

    @PostMapping("/post")
    public void insertCharacter(@RequestParam Long tv_id, @RequestBody Character character) {
        characterService.insertCharacter(tv_id, character);
    }

    @DeleteMapping("/delete")
    public void deleteCharacter(@RequestParam Long id) {
        characterService.deleteCharacter(id);
    }

    @PutMapping("/update")
    public void updateCharacter(@RequestBody Character character) {
        characterService.updateCharacter(character);
    }
}
