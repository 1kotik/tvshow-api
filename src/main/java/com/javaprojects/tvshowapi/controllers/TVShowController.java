package com.javaprojects.tvshowapi.controllers;

import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.services.TVShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/tvshows")
@Tag(name="Сериалы", description = "Управляет сериалами")
public class TVShowController {

    private final TVShowService tvShowService;

    public TVShowController(TVShowService tvShowService) {
        this.tvShowService = tvShowService;
    }

    @Operation(summary = "Поиск сериалов во внешнем API", description = "Результат сохраняется в базу данных")
    @GetMapping("/get-from-api")
    public List<TVShow> searchByTitleFromAPI(@Parameter(description = "Название сериала") @RequestParam(required = false) String title) throws IOException {
        return tvShowService.searchByTitleFromAPI(title);
    }
    @Operation(summary = "Показать все сериалы")
    @GetMapping("/get-all")
    public List<TVShow> getTVShows(){
        return tvShowService.getTVShows();
    }
    @Operation(summary = "Поиск сериалов по названию")
    @GetMapping("/get")
    public List<TVShow> searchByTitle(@Parameter(description = "Название сериала") @RequestParam(required = false) String title) {
        return tvShowService.searchByTitle(title);
    }

    @Operation(summary = "Поиск персонажей по ID сериала")
    @GetMapping("/get-characters")
    public Set<Character> getCharacters(@Parameter(description = "ID сериала") @RequestParam(required = false) Long id) {
        return tvShowService.getCharacters(id);
    }

    @Operation(summary = "Добавление сериала", description = "Необходимо указать хотя бы название сериала")
    @PostMapping("/post")
    public void insertTVShow(@Parameter(description = "Тело сериала") @RequestBody(required = false) TVShow tvShow) {
        tvShowService.insertTVShow(tvShow);
    }

    @Operation(summary = "Удаление сериала", description = "Необходимо указать ID сериала")
    @DeleteMapping("/delete")
    public void deleteTVShow(@Parameter(description = "ID сериала") @RequestParam(required = false) Long id) {
        tvShowService.deleteTVShow(id);
    }

    @Operation(summary = "Обновление сериала", description = "Необходимо указать хотя бы ID и название сериала")
    @PutMapping("/update")
    public void updateTVShow(@Parameter(description = "Тело сериала") @RequestBody(required = false) TVShow tvShow) {
        tvShowService.updateTVShow(tvShow);
    }
}

