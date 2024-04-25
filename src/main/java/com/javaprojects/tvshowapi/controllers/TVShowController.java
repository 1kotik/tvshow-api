package com.javaprojects.tvshowapi.controllers;

import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.services.RequestCounterService;
import com.javaprojects.tvshowapi.services.TVShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

@Controller
@RequestMapping("/tvshows")
@AllArgsConstructor
@Tag(name = "Сериалы", description = "Управляет сериалами")
public class TVShowController {

    private final TVShowService tvShowService;
    private final RequestCounterService requestCounterService;


    @Operation(summary = "Показать все сериалы")
    @GetMapping("/get-all")
    public String getTVShows(Model model) {
        requestCounterService.increment();
        try {
            List<TVShow> tvShows = tvShowService.getTVShows();
            model.addAttribute("tvshows", tvShows);
            return "shows";
        }catch (RuntimeException e){
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @Operation(summary = "Поиск сериалов по названию")
    @GetMapping("/get")
    public List<TVShow> searchByTitle(@Parameter(description = "Название сериала")
                                      @RequestParam(required = false) final String title) {
        requestCounterService.increment();
        return tvShowService.searchByTitle(title);
    }

    @Operation(summary = "Поиск персонажей по ID сериала")
    @GetMapping("/get-characters")
    public List<Character> getCharacters(@Parameter(description = "ID сериала")
                                         @RequestParam(required = false) final Long id) {
        requestCounterService.increment();
        return tvShowService.getCharacters(id);
    }

    @Operation(summary = "Добавление сериала", description = "Необходимо указать хотя бы название сериала")
    @PostMapping("/post")
    public ResponseEntity<String> insertTVShow(@Parameter(description = "Тело сериала")
                                               @RequestBody(required = false) final TVShow tvShow) {
        requestCounterService.increment();
        return tvShowService.insertTVShow(tvShow);
    }

    @Operation(summary = "Удаление сериала", description = "Необходимо указать ID сериала")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTVShow(@Parameter(description = "ID сериала")
                                               @RequestParam(required = false) final Long id) {
        requestCounterService.increment();
        return tvShowService.deleteTVShow(id);
    }

    @Operation(summary = "Обновление сериала", description = "Необходимо указать хотя бы ID и название сериала")
    @PutMapping("/update")
    public ResponseEntity<String> updateTVShow(@Parameter(description = "Тело сериала")
                                               @RequestBody(required = false) final TVShow tvShow) {
        requestCounterService.increment();
        return tvShowService.updateTVShow(tvShow);
    }

    @Operation(summary = "Добавление нескольких сериалов", description = "Укажите тела сериалов")
    @PostMapping("/post-more")
    public ResponseEntity<String> insertTVShows(@Parameter(description = "Тело")
                                                @RequestBody(required = false) final TVShow[] tvShows) {
        requestCounterService.increment();
        Stream.of(tvShows).forEach(tvShowService::insertTVShow);
        return ResponseEntity.ok("TV Shows are inserted successfully");
    }
}
