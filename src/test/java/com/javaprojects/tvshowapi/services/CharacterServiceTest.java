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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CharacterServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private TVShowRepository tvShowRepository;

    @Mock
    private EntityCache<Integer, List<Character>> cache;


    @InjectMocks
    private CharacterService characterService;

    private static final Character character = new Character();
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

        character.setId(1L);
        character.setName("Alex");
        character.setFeature("the best");
        character.setTvShow(tvShow);

    }


    @Test
    public void getCharactersTest_Success() {
        when(characterRepository.findAll()).thenReturn(List.of(character));

        List<Character> result = characterService.getCharacters();

        assertEquals(List.of(character), result);

    }

    @Test
    public void getCharactersTest_Error404() {
        when(characterRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(NotFoundException.class, () -> characterService.getCharacters());
    }

    @Test
    public void getCharactersTest_Error500() {
        when(characterRepository.findAll()).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> characterService.getCharacters());
    }

    @Test
    public void searchByNameTest_Success() {
        int hashCode = Objects.hashCode("Alex");
        when(cache.get(hashCode)).thenReturn(null);
        when(characterRepository.findAll().stream().filter(c -> c.getName().equals("Alex")).toList())
                .thenReturn(List.of(character));

        List<Character> result = characterService.searchByName("Alex");

        assertEquals(List.of(character), result);
    }

    @Test
    public void searchByNameTest_CacheNotNull() {
        int hashCode = Objects.hashCode("Alex");
        when(cache.get(hashCode)).thenReturn(List.of(character));
        doNothing().when(cache).put(hashCode, List.of(character));
        List<Character> result = characterService.searchByName("Alex");

        assertEquals(List.of(character), result);

    }

    @Test
    public void searchByNameTest_Error400() {
        assertThrows(BadRequestException.class, () -> characterService.searchByName(""));
        assertThrows(BadRequestException.class, () -> characterService.searchByName(null));
    }

    @Test
    public void searchByNameTest_Error404() {
        int hashCode = Objects.hashCode("Alex");
        when(cache.get(hashCode)).thenReturn(null);
        when(characterRepository.findAll().stream().filter(c -> c.getName().equals("Test")).toList())
                .thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> characterService.searchByName("Test"));
    }

    @Test
    public void searchByNameTest_Error500() {
        int hashCode = Objects.hashCode("Alex");
        when(cache.get(hashCode)).thenReturn(null);
        when(characterRepository.findAll().stream().filter(c -> c.getName().equals("Alex")).toList())
                .thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> characterService.searchByName("Alex"));
    }

    @Test
    public void insertCharacterTest_Success() {
        when(tvShowRepository.findById(1L)).thenReturn(Optional.of(tvShow));
        when(characterRepository.save(character)).thenReturn(null);
        doNothing().when(cache).remove(Objects.hashCode(character.getName()));

        ResponseEntity<String> result = characterService.insertCharacter(1L, character);

        assertEquals(ResponseEntity.ok("Character is saved successfully"), result);

    }

    @Test
    public void insertCharacterTest_Error400() {
        Character c = new Character();
        assertThrows(BadRequestException.class, () -> characterService.insertCharacter(null, character));
        assertThrows(BadRequestException.class, () -> characterService.insertCharacter(1L, c));
    }

    @Test
    public void insertCharacterTest_Error404() {
        doReturn(Optional.empty()).when(tvShowRepository).findById(1L);
        assertThrows(NotFoundException.class, () -> characterService.insertCharacter(1L, character));
    }

    @Test
    public void insertCharacterTest_Error500() {
        when(tvShowRepository.findById(1L)).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> characterService.insertCharacter(1L, character));
    }

    @Test
    public void deleteCharacterTest_Success() {
        when(characterRepository.findById(1L)).thenReturn(Optional.of(character));
        doNothing().when(characterRepository).delete(any());
        doNothing().when(cache).remove(Objects.hashCode(character.getName()));

        ResponseEntity<String> result = characterService.deleteCharacter(1L);

        assertEquals(ResponseEntity.ok("Character is deleted successfully"), result);
    }

    @Test
    public void deleteCharacterTest_Error400() {
        assertThrows(BadRequestException.class, () -> characterService.deleteCharacter(null));
    }

    @Test
    public void deleteCharacterTest_Error404() {
        when(characterRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> characterService.deleteCharacter(anyLong()));
    }

    @Test
    public void deleteCharacterTest_Error500() {
        when(characterRepository.findById(anyLong())).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> characterService.deleteCharacter(anyLong()));
    }

    @Test
    public void updateCharacterTest_Success() {
        Character characterToUpdate = new Character();
        characterToUpdate.setName("John");
        characterToUpdate.setId(1L);

        when(characterRepository.findById(anyLong())).thenReturn(Optional.of(character));
        doNothing().when(cache).remove(Objects.hashCode(character.getName()));
        doNothing().when(cache).remove(Objects.hashCode(characterToUpdate.getName()));
        when(characterRepository.save(characterToUpdate)).thenReturn(null);

        ResponseEntity<String> result = characterService.updateCharacter(characterToUpdate);

        assertEquals(ResponseEntity.ok("Character is updated successfully"), result);
    }

    @Test
    public void updateCharacterTest_Error400() {
        Character invalidCharacter = new Character();
        invalidCharacter.setId(1L);

        assertThrows(BadRequestException.class, () -> characterService.updateCharacter(invalidCharacter));
    }

    @Test
    public void updateCharacterTest_Error404() {
        when(characterRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> characterService.updateCharacter(character));
    }

    @Test
    public void updateCharacterTest_Error500() {
        when(characterRepository.findById(anyLong())).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> characterService.updateCharacter(character));
    }

    @Test
    public void searchByTVShowTitleTest_Success() {
        when(characterRepository.findAll().stream()
                .filter(c -> c.getTvShow().getTitle().equals("Test")).toList()).thenReturn(List.of(character));

        List<Character> result = characterService.searchByTVShowTitle("Test");

        assertEquals(List.of(character), result);
    }

    @Test
    public void searchByTVShowTitleTest_Error400() {
        assertThrows(BadRequestException.class, () -> characterService.searchByTVShowTitle(null));
        assertThrows(BadRequestException.class, () -> characterService.searchByTVShowTitle(""));
    }

    @Test
    public void searchByTVShowTitleTest_Error404() {
        when(characterRepository.findAll().stream()
                .filter(c -> c.getTvShow().getTitle().equals("any")).toList()).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> characterService.searchByTVShowTitle("any"));
    }

    @Test
    public void searchByTVShowTitleTest_Error500() {
        when(characterRepository.findAll().stream()
                .filter(c -> c.getTvShow().getTitle().equals("any")).toList()).thenThrow(RuntimeException.class);

        assertThrows(ServerException.class, () -> characterService.searchByTVShowTitle("any"));
    }


}
