package com.javaprojects.tvshowapi.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.javaprojects.tvshowapi.cache.EntityCache;
import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.exceptions.BadRequestException;
import com.javaprojects.tvshowapi.exceptions.NotFoundException;
import com.javaprojects.tvshowapi.exceptions.ServerException;
import com.javaprojects.tvshowapi.repositories.CharacterRepository;
import com.javaprojects.tvshowapi.repositories.TVShowRepository;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TVShowServiceTest {
    @Mock
    private TVShowRepository tvShowRepository;

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private EntityCache<Integer, List<TVShow>> cache;

    @InjectMocks
    private TVShowService tvShowService;

    private static final TVShow tvShow = new TVShow();

    private static final Character character = new Character();


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

        character.setId(1L);
        character.setName("Alex");
        character.setFeature("the best");
        character.setTvShow(tvShow);

    }

    @Test
    void getTVShowsTest_Success() {
        when(tvShowRepository.findAll().stream()
                .sorted((tv1, tv2) -> tv1.getId().compareTo(tv2.getId())).toList()).thenReturn(List.of(tvShow));

        List<TVShow> result = tvShowService.getTVShows();

        assertEquals(List.of(tvShow), result);
    }

    @Test
    void getTVShowsTest_Error404() {
        when(tvShowRepository.findAll().stream()
                .sorted((tv1, tv2) -> tv1.getId().compareTo(tv2.getId())).toList()).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> tvShowService.getTVShows());
    }

    @Test
    void getTVShowsTest_Error500() {
        when(tvShowRepository.findAll().stream()
                .sorted((tv1, tv2) -> tv1.getId().compareTo(tv2.getId())).toList()).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> tvShowService.getTVShows());
    }

    @Test
    void searchByTitleTest_Success() {
        String title = "Test";
        int hashCode = Objects.hashCode(title);
        when(cache.get(hashCode)).thenReturn(null);
        when(tvShowRepository.findAll().stream()
                .filter(tv -> tv.getTitle().contains(title)).toList()).thenReturn(List.of(tvShow));
        doNothing().when(cache).put(hashCode, List.of(tvShow));

        List<TVShow> result = tvShowService.searchByTitle(title);

        assertEquals(List.of(tvShow), result);
    }

    @Test
    void searchByTitleTest_CacheNotNull() {
        String title = "Test";
        int hashCode = Objects.hashCode(title);
        when(cache.get(hashCode)).thenReturn(List.of(tvShow));

        List<TVShow> result = tvShowService.searchByTitle(title);

        assertEquals(List.of(tvShow), result);
    }

    @Test
    void searchByTitleTest_Error400() {
        assertThrows(BadRequestException.class, () -> tvShowService.searchByTitle(null));
        assertThrows(BadRequestException.class, () -> tvShowService.searchByTitle(""));
    }

    @Test
    void searchByTitleTest_Error404() {
        String title = "Test";
        int hashCode = Objects.hashCode(title);
        when(cache.get(hashCode)).thenReturn(null);
        when(tvShowRepository.findAll().stream()
                .filter(tv -> tv.getTitle().contains(title)).toList()).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> tvShowService.searchByTitle(title));
    }

    @Test
    void searchByTitleTest_Error500() {
        String title = "Test";
        int hashCode = Objects.hashCode(title);
        when(cache.get(hashCode)).thenReturn(null);
        when(tvShowRepository.findAll().stream()
                .filter(tv -> tv.getTitle().contains(title)).toList()).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> tvShowService.searchByTitle(title));
    }

    @Test
    void insertTVShowTest_Success() {
        tvShow.getCharacters().forEach(c -> c.setTvShow(tvShow));
        tvShow.getViewers().forEach(v -> v.getTvShows().add(tvShow));
        doReturn(tvShow).when(tvShowRepository).save(tvShow);
        doNothing().when(cache).remove(Objects.hashCode(tvShow.getTitle()));

        ResponseEntity<String> result = tvShowService.insertTVShow(tvShow);

        assertEquals(ResponseEntity.ok("TV Show is inserted successfully"), result);
    }

    @Test
    void insertTVShowTest_Error400() {
        TVShow invalidTVShow = new TVShow();
        invalidTVShow.setId(2L);

        assertThrows(BadRequestException.class, () -> tvShowService.insertTVShow(invalidTVShow));
    }

    @Test
    void insertTVShowTest_Error500() {
        tvShow.getCharacters().forEach(c -> c.setTvShow(tvShow));
        tvShow.getViewers().forEach(v -> v.getTvShows().add(tvShow));
        doThrow(RuntimeException.class).when(tvShowRepository).save(tvShow);

        assertThrows(ServerException.class, () -> tvShowService.insertTVShow(tvShow));
    }

    @Test
    void deleteTVShowTest_Success() {
        when(tvShowRepository.findById(anyLong())).thenReturn(Optional.of(tvShow));
        tvShow.getViewers().forEach(v -> v.getTvShows().remove(tvShow));
        doNothing().when(characterRepository).deleteAll(tvShow.getCharacters());
        doNothing().when(cache).remove(Objects.hashCode(tvShow.getTitle()));
        doNothing().when(tvShowRepository).deleteById(anyLong());

        ResponseEntity<String> result = tvShowService.deleteTVShow(anyLong());

        assertEquals(ResponseEntity.ok("TV Show is deleted successfully"), result);
    }

    @Test
    void deleteTVShowTest_Error400() {
        assertThrows(BadRequestException.class, () -> tvShowService.deleteTVShow(null));
    }

    @Test
    void deleteTVShowTest_Error404() {
        when(tvShowRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tvShowService.deleteTVShow(1L));
    }

    @Test
    void deleteTVShowTest_Error500() {
        when(tvShowRepository.findById(anyLong())).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> tvShowService.deleteTVShow(1L));
    }

    @Test
    void updateTVShowTest_Success() {
        TVShow tvShowToUpdate = new TVShow();
        tvShowToUpdate.setId(1L);
        tvShowToUpdate.setTitle("Any");

        tvShowToUpdate.getCharacters().forEach(c -> c.setTvShow(tvShowToUpdate));
        tvShowToUpdate.getViewers().forEach(v -> v.getTvShows().add(tvShowToUpdate));
        when(tvShowRepository.findById(tvShowToUpdate.getId())).thenReturn(Optional.of(tvShow));
        doNothing().when(cache).remove(Objects.hashCode(tvShowToUpdate.getTitle()));
        doNothing().when(cache).remove(Objects.hashCode(tvShow.getTitle()));
        when(tvShowRepository.save(tvShow)).thenReturn(tvShow);

        ResponseEntity<String> result = tvShowService.updateTVShow(tvShowToUpdate);

        assertEquals(ResponseEntity.ok("TV Show is updated successfully"), result);

    }

    @Test
    void updateTVShowTest_Error400() {
        TVShow invalidTVShow = new TVShow();
        invalidTVShow.setId(1L);

        assertThrows(BadRequestException.class, () -> tvShowService.updateTVShow(invalidTVShow));
    }

    @Test
    void updateTVShowTest_Error404() {
        when(tvShowRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tvShowService.updateTVShow(tvShow));
    }

    @Test
    void updateTVShowTest_Error500() {
        when(tvShowRepository.findById(anyLong())).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> tvShowService.updateTVShow(tvShow));
    }

    @Test
    void getCharactersTest_Success() {
        when(characterRepository.findAll().stream().sorted(Comparator.comparing(Character::getId))
                .filter(c -> c.getTvShow().getId().equals(1L)).toList())
                .thenReturn(List.of(character));

        List<Character> result = tvShowService.getCharacters(1L);

        assertEquals(List.of(character), result);
    }

    @Test
    void getCharactersTest_Error400() {
        assertThrows(BadRequestException.class, () -> tvShowService.getCharacters(null));
    }

    @Test
    void getCharactersTest_Error404() {
        when(characterRepository.findAll().stream().sorted(Comparator.comparing(Character::getId))
                .filter(c -> c.getTvShow().getId().equals(1L)).toList())
                .thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> tvShowService.getCharacters(1L));
    }

    @Test
    void getCharactersTest_Error500() {
        when(characterRepository.findAll().stream().sorted(Comparator.comparing(Character::getId))
                .filter(c -> c.getTvShow().getId().equals(1L)).toList())
                .thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> tvShowService.getCharacters(1L));
    }
}
