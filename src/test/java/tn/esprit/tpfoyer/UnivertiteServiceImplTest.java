package tn.esprit.tpfoyer;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Universite;
import tn.esprit.tpfoyer.repository.UniversiteRepository;
import tn.esprit.tpfoyer.service.UniversiteServiceImpl;

import java.util.List;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)

public class UnivertiteServiceImplTest {
    @Mock
    private UniversiteRepository universiteRepository;

    @InjectMocks
    private UniversiteServiceImpl universiteService;

    private Universite universite;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        universite = new Universite();
        universite.setIdUniversite(1L);
        universite.setNomUniversite("Université Tunis");
        universite.setAdresse("Tunis, Tunisia");
    }

    @Test
    void testRetrieveAllUniversites() {
        // Arrange
        when(universiteRepository.findAll()).thenReturn(List.of(universite));

        // Act
        List<Universite> result = universiteService.retrieveAllUniversites();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Université Tunis", result.get(0).getNomUniversite());
    }

    @Test
    void testRetrieveUniversite() {
        // Arrange
        when(universiteRepository.findById(1L)).thenReturn(Optional.of(universite));

        // Act
        Universite result = universiteService.retrieveUniversite(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdUniversite());
        assertEquals("Université Tunis", result.getNomUniversite());
    }

    @Test
    void testAddUniversite() {
        // Arrange
        when(universiteRepository.save(universite)).thenReturn(universite);

        // Act
        Universite result = universiteService.addUniversite(universite);

        // Assert
        assertNotNull(result);
        assertEquals("Université Tunis", result.getNomUniversite());
    }

    @Test
    void testModifyUniversite() {
        // Arrange
        when(universiteRepository.save(universite)).thenReturn(universite);

        // Act
        Universite result = universiteService.modifyUniversite(universite);

        // Assert
        assertNotNull(result);
        assertEquals("Université Tunis", result.getNomUniversite());
    }

    @Test
    void testRemoveUniversite() {
        // Arrange
        doNothing().when(universiteRepository).deleteById(1L);

        // Act
        universiteService.removeUniversite(1L);

        // Assert
        verify(universiteRepository, times(1)).deleteById(1L);
    }
}
