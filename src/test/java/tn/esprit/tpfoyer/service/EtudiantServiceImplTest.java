package tn.esprit.tpfoyer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.service.tpfoyer.entity.Etudiant;
import tn.esprit.tpfoyer.service.tpfoyer.repository.EtudiantRepository;
import tn.esprit.tpfoyer.service.tpfoyer.service.EtudiantServiceImpl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EtudiantServiceImplTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EtudiantServiceImpl etudiantService;

    private Etudiant etudiant;

    @BeforeEach
    void setUp() {
        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1L);
        etudiant.setNomEtudiant("Doe");
        etudiant.setPrenomEtudiant("John");
        etudiant.setCinEtudiant(12345678);
        etudiant.setDateNaissance(new Date());
    }

    @Test
    void retrieveAllEtudiants_shouldReturnAllEtudiants() {
        // Given
        Etudiant etudiant2 = new Etudiant();
        etudiant2.setIdEtudiant(2L);
        etudiant2.setNomEtudiant("Smith");
        etudiant2.setPrenomEtudiant("Jane");

        when(etudiantRepository.findAll()).thenReturn(Arrays.asList(etudiant, etudiant2));

        // When
        List<Etudiant> result = etudiantService.retrieveAllEtudiants();

        // Then
        assertEquals(2, result.size());
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
    void retrieveEtudiant_shouldReturnEtudiantWhenIdExists() {
        // Given
        when(etudiantRepository.findById(1L)).thenReturn(Optional.of(etudiant));

        // When
        Etudiant result = etudiantService.retrieveEtudiant(1L);

        // Then
        assertNotNull(result);
        assertEquals(etudiant.getIdEtudiant(), result.getIdEtudiant());
        assertEquals(etudiant.getNomEtudiant(), result.getNomEtudiant());
        verify(etudiantRepository, times(1)).findById(1L);
    }

    @Test
    void addEtudiant_shouldSaveAndReturnEtudiant() {
        // Given
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // When
        Etudiant result = etudiantService.addEtudiant(etudiant);

        // Then
        assertNotNull(result);
        assertEquals(etudiant.getIdEtudiant(), result.getIdEtudiant());
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void modifyEtudiant_shouldUpdateAndReturnEtudiant() {
        // Given
        etudiant.setNomEtudiant("UpdatedName");
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);

        // When
        Etudiant result = etudiantService.modifyEtudiant(etudiant);

        // Then
        assertNotNull(result);
        assertEquals("UpdatedName", result.getNomEtudiant());
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void removeEtudiant_shouldCallDeleteById() {
        // Given
        doNothing().when(etudiantRepository).deleteById(1L);

        // When
        etudiantService.removeEtudiant(1L);

        // Then
        verify(etudiantRepository, times(1)).deleteById(1L);
    }

    @Test
    void recupererEtudiantParCin_shouldReturnEtudiantWhenCinExists() {
        // Given
        when(etudiantRepository.findEtudiantByCinEtudiant(12345678)).thenReturn(etudiant);

        // When
        Etudiant result = etudiantService.recupererEtudiantParCin(12345678);

        // Then
        assertNotNull(result);
        assertEquals(etudiant.getCinEtudiant(), result.getCinEtudiant());
        verify(etudiantRepository, times(1)).findEtudiantByCinEtudiant(12345678);
    }
}