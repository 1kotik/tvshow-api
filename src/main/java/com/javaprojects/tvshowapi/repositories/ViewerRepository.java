package com.javaprojects.tvshowapi.repositories;

import com.javaprojects.tvshowapi.entities.Viewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewerRepository extends JpaRepository<Viewer, Long> {
    @Query("SELECT viewer FROM Viewer viewer WHERE viewer.name = ?1")
    List<Viewer> searchByName(String name);
}
