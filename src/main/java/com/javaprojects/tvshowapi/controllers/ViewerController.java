package com.javaprojects.tvshowapi.controllers;

import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.entities.Viewer;
import com.javaprojects.tvshowapi.services.ViewerService;
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
import java.util.Set;
import java.util.stream.Stream;

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
    public List<Viewer> searchByName(@Parameter(description = "Имя зрителя")
                                     @RequestParam(required = false) final String name) {
        return viewerService.searchByName(name);
    }

    @Operation(summary = "Поиск просмотренных сериалов", description = "Необходимо указать ID зрителя")
    @GetMapping("/get-watched")
    public List<TVShow> getWatchedTVShows(@Parameter(description = "ID зрителя") @RequestParam final Long id) {
        return viewerService.getWatchedTVShows(id);
    }

    @Operation(summary = "Добавление зрителя", description = "Необходимо указать хотя бы имя зрителя")
    @PostMapping("/post")
    public ResponseEntity<String> insertViewer(@Parameter(description = "Тело зрителя") @RequestBody final Viewer viewer) {
        return viewerService.insertViewer(viewer);
    }

    @Operation(summary = "Удаление зрителя", description = "Необходимо указать ID зрителя")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteViewer(@Parameter(description = "ID зрителя") @RequestParam final Long id) {
        return viewerService.deleteViewer(id);
    }

    @Operation(summary = "Обновление зрителя", description = "Необходимо указать хотя бы ID и имя зрителя")
    @PutMapping("/update")
    public ResponseEntity<String> updateViewer(@Parameter(description = "Тело зрителя") @RequestBody final Viewer viewer) {
        return viewerService.updateViewer(viewer);
    }

    @Operation(summary = "Добавить в просмотренные", description = "Необходимо указать ID зрителя и ID сериала")
    @PutMapping("/add-to-watched")
    public ResponseEntity<String> addToWatched(@Parameter(description = "ID зрителя") @RequestParam final Long vid,
                             @Parameter(description = "ID сериала") @RequestParam final Long tv) {
        return viewerService.addToWatched(vid, tv);
    }
    @Operation(summary = "Добавление зрителей", description = "Необходимо указать хотя бы имя зрителя")
    @PostMapping("/post-more")
    public ResponseEntity<String> insertViewers(@Parameter(description = "Тело") @RequestBody final Viewer[] viewers){
        Stream.of(viewers).forEach(viewerService::insertViewer);
        return ResponseEntity.ok("Viewers are inserted successfully");
    }
}
