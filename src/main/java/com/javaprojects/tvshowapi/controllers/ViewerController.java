package com.javaprojects.tvshowapi.controllers;

import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.entities.Viewer;
import com.javaprojects.tvshowapi.services.ViewerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/viewers")
@AllArgsConstructor
@Tag(name = "Зрители", description = "Управляет зрителями сериалов")
public class ViewerController {
    private final ViewerService viewerService;

    @Operation(summary = "Показать всех зрителей")
    @GetMapping("/get-all")
    public List<Viewer> getViewers() {
        return viewerService.getViewers();
    }

    @Operation(summary = "Поиск зрителя по имени")
    @GetMapping("/get")
    public List<Viewer> searchByName(@Parameter(description = "Имя зрителя") @RequestParam(required = false) String name) {
        return viewerService.searchByName(name);
    }

    @Operation(summary = "Поиск просмотренных сериалов", description = "Необходимо указать ID зрителя")
    @GetMapping("/get-watched")
    public Set<TVShow> getWatchedTVShows(@Parameter(description = "ID зрителя") @RequestParam Long id) {
        return viewerService.getWatchedTVShows(id);
    }

    @Operation(summary = "Добавление зрителя", description = "Необходимо указать хотя бы имя зрителя")
    @PostMapping("/post")
    public void insertViewer(@Parameter(description = "Тело зрителя") @RequestBody Viewer viewer) {
        viewerService.insertViewer(viewer);
    }

    @Operation(summary = "Удаление зрителя", description = "Необходимо указать ID зрителя")
    @DeleteMapping("/delete")
    public void deleteViewer(@Parameter(description = "ID зрителя") @RequestParam Long id) {
        viewerService.deleteViewer(id);
    }

    @Operation(summary = "Обновление зрителя", description = "Необходимо указать хотя бы ID и имя зрителя")
    @PutMapping("/update")
    public void updateViewer(@Parameter(description = "Тело зрителя") @RequestBody Viewer viewer) {
        viewerService.updateViewer(viewer);
    }

    @Operation(summary = "Добавить в просмотренные", description = "Необходимо указать ID зрителя и ID сериала")
    @PutMapping("/add-to-watched")
    public void addToWatched(@Parameter(description = "ID зрителя") @RequestParam Long vid,
                             @Parameter(description = "ID сериала") @RequestParam Long tv) {
        viewerService.addToWatched(vid, tv);
    }
}

