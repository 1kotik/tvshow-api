package com.javaprojects.tvshowapi.controllers;

import com.javaprojects.tvshowapi.entities.Viewer;
import com.javaprojects.tvshowapi.services.ViewerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viewers")
@AllArgsConstructor
public class ViewerController {
    private final ViewerService viewerService;

    @GetMapping("/get")
    public List<Viewer> searchByName(@RequestParam(required = false) String name) {
        return viewerService.searchByName(name);
    }

    @PostMapping("/post")
    public void insertViewer(@RequestBody Viewer viewer) {
        viewerService.insertViewer(viewer);
    }

    @DeleteMapping("/delete")
    public void deleteViewer(@RequestParam Long id) {
        viewerService.deleteViewer(id);
    }

    @PutMapping("/update")
    public void updateViewer(@RequestBody Viewer viewer) {
        viewerService.updateViewer(viewer);
    }
}

