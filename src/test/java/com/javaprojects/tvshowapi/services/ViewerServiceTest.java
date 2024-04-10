package com.javaprojects.tvshowapi.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.javaprojects.tvshowapi.cache.EntityCache;
import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.entities.Viewer;
import com.javaprojects.tvshowapi.exceptions.BadRequestException;
import com.javaprojects.tvshowapi.exceptions.NotFoundException;
import com.javaprojects.tvshowapi.exceptions.ServerException;
import com.javaprojects.tvshowapi.repositories.TVShowRepository;
import com.javaprojects.tvshowapi.repositories.ViewerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ViewerServiceTest {
    @Mock
    private ViewerRepository viewerRepository;

    @Mock
    private TVShowRepository tvShowRepository;

    @Mock
    private EntityCache<Integer, List<Viewer>> cache;

    @InjectMocks
    private ViewerService viewerService;

    private static final Viewer viewer = new Viewer();

    private static final TVShow tvShow = new TVShow();

    @BeforeEach
    public void prepareData() {
        tvShow.setId(1L);
        tvShow.setTitle("Test");
        tvShow.setPermalink("test");
        tvShow.setStartDate("1.1.2001");
        tvShow.setEndDate("2.1.2001");
        tvShow.setCountry("US");
        tvShow.setNetwork("Netflix");
        tvShow.setStatus("Ended");
        tvShow.setImageThumbnailPath("test-image");
        tvShow.setViewers(Set.of(viewer));

        viewer.setId(1L);
        viewer.setName("John");
        viewer.setCountry("Germany");
        viewer.setAge(20);
        viewer.setTvShows(Set.of(tvShow));
    }

    @Test
    void getViewersTest_Success() {
        when(viewerRepository.findAll().stream()
                .sorted(Comparator.comparing(Viewer::getId)).collect(Collectors.toList()))
                .thenReturn(List.of(viewer));

        List<Viewer> result = viewerService.getViewers();

        assertEquals(List.of(viewer), result);
    }

    @Test
    void getViewersTest_Error404() {
        when(viewerRepository.findAll().stream()
                .sorted(Comparator.comparing(Viewer::getId)).collect(Collectors.toList()))
                .thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> viewerService.getViewers());
    }

    @Test
    void getViewersTest_Error500() {
        when(viewerRepository.findAll().stream()
                .sorted(Comparator.comparing(Viewer::getId)).collect(Collectors.toList()))
                .thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> viewerService.getViewers());
    }

    @Test
    void searchByNameTest_Success() {
        String name = "John";
        int hashCode = Objects.hashCode(name);
        when(cache.get(hashCode)).thenReturn(null);
        when(viewerRepository.findAll().stream()
                .filter(v -> v.getName().contains(name)).toList())
                .thenReturn(List.of(viewer));
        doNothing().when(cache).put(hashCode, List.of(viewer));

        List<Viewer> result = viewerService.searchByName(name);

        assertEquals(List.of(viewer), result);
    }

    @Test
    void searchByNameTest_CacheNotNull() {
        String name = "John";
        int hashCode = Objects.hashCode(name);
        when(cache.get(hashCode)).thenReturn(List.of(viewer));

        List<Viewer> result = viewerService.searchByName(name);

        assertEquals(List.of(viewer), result);
    }

    @Test
    void searchByNameTest_Error400() {
        assertThrows(BadRequestException.class, () -> viewerService.searchByName(null));
        assertThrows(BadRequestException.class, () -> viewerService.searchByName(""));
    }

    @Test
    void searchByNameTest_Error404() {
        String name = "John";
        int hashCode = Objects.hashCode(name);
        when(cache.get(hashCode)).thenReturn(null);
        when(viewerRepository.findAll().stream()
                .filter(v -> v.getName().contains(name)).toList())
                .thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> viewerService.searchByName(name));
    }

    @Test
    void searchByNameTest_Error500() {
        String name = "John";
        int hashCode = Objects.hashCode(name);
        when(cache.get(hashCode)).thenReturn(null);
        when(viewerRepository.findAll().stream()
                .filter(v -> v.getName().contains(name)).toList())
                .thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> viewerService.searchByName(name));
    }

    @Test
    void insertViewerTest_Success() {
        viewer.getTvShows().forEach(tv -> tv.getCharacters().forEach(c -> c.setTvShow(tv)));
        when(viewerRepository.save(viewer)).thenReturn(viewer);
        doNothing().when(cache).remove(Objects.hashCode(viewer.getName()));

        ResponseEntity<String> result = viewerService.insertViewer(viewer);

        assertEquals(ResponseEntity.ok("Viewer is inserted successfully"), result);
    }

    @Test
    void insertViewerTest_Error400() {
        Viewer invalidViewer = new Viewer();
        invalidViewer.setId(2L);

        assertThrows(BadRequestException.class, () -> viewerService.insertViewer(invalidViewer));
    }

    @Test
    void insertViewerTest_Error500() {
        viewer.getTvShows().forEach(tv -> tv.getCharacters().forEach(c -> c.setTvShow(tv)));
        when(viewerRepository.save(viewer)).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> viewerService.insertViewer(viewer));
    }

    @Test
    void deleteViewerTest_Success() {
        when(viewerRepository.findById(anyLong())).thenReturn(Optional.of(viewer));
        doNothing().when(cache).remove(Objects.hashCode(viewer.getName()));
        doNothing().when(viewerRepository).deleteById(anyLong());

        ResponseEntity<String> result = viewerService.deleteViewer(anyLong());

        assertEquals(ResponseEntity.ok("Viewer is deleted successfully"), result);
    }

    @Test
    void deleteViewerTest_Error400() {
        assertThrows(BadRequestException.class, () -> viewerService.deleteViewer(null));
    }

    @Test
    void deleteViewerTest_Error404() {
        when(viewerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> viewerService.deleteViewer(1L));
    }

    @Test
    void deleteViewerTest_Error500() {
        when(viewerRepository.findById(anyLong())).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> viewerService.deleteViewer(1L));
    }

    @Test
    void updateViewerTest_Success() {
        Viewer viewerToUpdate = new Viewer();
        viewerToUpdate.setId(1L);
        viewerToUpdate.setName("Mary");

        viewer.getTvShows().forEach(tv -> tv.getCharacters().forEach(c -> c.setTvShow(tv)));
        when(viewerRepository.findById(viewerToUpdate.getId())).thenReturn(Optional.of(viewer));
        doNothing().when(cache).remove(Objects.hashCode(viewer.getName()));
        doNothing().when(cache).remove(Objects.hashCode(viewerToUpdate.getName()));
        when(viewerRepository.save(viewer)).thenReturn(viewer);

        ResponseEntity<String> result = viewerService.updateViewer(viewerToUpdate);

        assertEquals(ResponseEntity.ok("Viewer us updated successfully"), result);
    }

    @Test
    void updateViewerTest_Error400() {
        Viewer invalidViewer1 = new Viewer();
        invalidViewer1.setId(1L);

        Viewer invalidViewer2 = new Viewer();
        invalidViewer2.setName("Alex");

        assertThrows(BadRequestException.class, () -> viewerService.updateViewer(invalidViewer1));
        assertThrows(BadRequestException.class, () -> viewerService.updateViewer(invalidViewer2));
    }

    @Test
    void updateViewerTest_Error404() {
        Viewer viewerToUpdate = new Viewer();
        viewerToUpdate.setId(1L);
        viewerToUpdate.setName("Mary");

        viewer.getTvShows().forEach(tv -> tv.getCharacters().forEach(c -> c.setTvShow(tv)));
        when(viewerRepository.findById(viewerToUpdate.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> viewerService.updateViewer(viewerToUpdate));
    }

    @Test
    void updateViewerTest_Error500() {
        Viewer viewerToUpdate = new Viewer();
        viewerToUpdate.setId(1L);
        viewerToUpdate.setName("Mary");

        viewer.getTvShows().forEach(tv -> tv.getCharacters().forEach(c -> c.setTvShow(tv)));
        when(viewerRepository.findById(viewerToUpdate.getId())).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> viewerService.updateViewer(viewerToUpdate));
    }

    @Test
    void addToWatchedTest_Success() {
        Viewer viewerTest = new Viewer();
        viewerTest.setId(2L);
        viewerTest.setName("Mary");

        when(tvShowRepository.findById(1L)).thenReturn(Optional.of(tvShow));
        when(viewerRepository.findById(2L)).thenReturn(Optional.of(viewerTest));

        ResponseEntity<String> result = viewerService.addToWatched(2L, 1L);

        assertEquals(ResponseEntity.ok("TV Show is successfully added to viewer"), result);
    }

    @Test
    void addToWatchedTest_Error400() {
        assertThrows(BadRequestException.class, () -> viewerService.addToWatched(null, 1L));
        assertThrows(BadRequestException.class, () -> viewerService.addToWatched(1L, null));
    }

    @Test
    void addToWatchedTest_Error404() {
        when(tvShowRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> viewerService.addToWatched(1L, 2L));
    }

    @Test
    void addToWatchedTest_Error500() {
        when(tvShowRepository.findById(2L)).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> viewerService.addToWatched(1L, 2L));
    }

    @Test
    void getWatchedTVShowsTest_Success() {
        when(tvShowRepository.findAll().stream()
                .filter(tv -> tv.getViewers().stream().anyMatch(v -> v.getId().equals(1L))).collect(Collectors.toList()))
                .thenReturn(List.of(tvShow));

        List<TVShow> result = viewerService.getWatchedTVShows(1L);

        assertEquals(List.of(tvShow), result);
    }

    @Test
    void getWatchedTVShowsTest_Error400() {
        assertThrows(BadRequestException.class, () -> viewerService.getWatchedTVShows(null));
    }

    @Test
    void getWatchedTVShowsTest_Error404() {
        when(tvShowRepository.findAll().stream()
                .filter(tv -> tv.getViewers().stream().anyMatch(v -> v.getId().equals(1L))).collect(Collectors.toList()))
                .thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> viewerService.getWatchedTVShows(1L));
    }

    @Test
    void getWatchedTVShowsTest_Error500() {
        when(tvShowRepository.findAll().stream()
                .filter(tv -> tv.getViewers().stream().anyMatch(v -> v.getId().equals(1L))).collect(Collectors.toList()))
                .thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> viewerService.getWatchedTVShows(1L));
    }
}
