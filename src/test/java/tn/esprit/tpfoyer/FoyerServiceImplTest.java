package tn.esprit.tpfoyer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Foyer;
import tn.esprit.tpfoyer.repository.FoyerRepository;
import tn.esprit.tpfoyer.service.FoyerServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)

public class FoyerServiceImplTest {
    @Mock
    private FoyerRepository foyerRepository;

    @InjectMocks
    private FoyerServiceImpl foyerService;

    private Foyer foyer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        foyer = new Foyer();
        foyer.setIdFoyer(1L);
        foyer.setNomFoyer("Foyer A");
        foyer.setCapaciteFoyer(100);  // Use capaciteFoyer as it exists
    }


    @Test
    void testRetrieveAllFoyers() {
        List<Foyer> foyers = List.of(foyer);
        when(foyerRepository.findAll()).thenReturn(foyers);

        List<Foyer> result = foyerService.retrieveAllFoyers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Foyer A", result.get(0).getNomFoyer());
        verify(foyerRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveFoyer() {
        when(foyerRepository.findById(1L)).thenReturn(Optional.of(foyer));

        Foyer result = foyerService.retrieveFoyer(1L);

        assertNotNull(result);
        assertEquals("Foyer A", result.getNomFoyer());
        verify(foyerRepository, times(1)).findById(1L);
    }

    @Test
    void testAddFoyer() {
        when(foyerRepository.save(foyer)).thenReturn(foyer);

        Foyer result = foyerService.addFoyer(foyer);

        assertNotNull(result);
        assertEquals("Foyer A", result.getNomFoyer());
        verify(foyerRepository, times(1)).save(foyer);
    }

    @Test
    void testModifyFoyer() {
        when(foyerRepository.save(foyer)).thenReturn(foyer);

        Foyer result = foyerService.modifyFoyer(foyer);

        assertNotNull(result);
        assertEquals("Foyer A", result.getNomFoyer());
        verify(foyerRepository, times(1)).save(foyer);
    }

    @Test
    void testRemoveFoyer() {
        doNothing().when(foyerRepository).deleteById(1L);

        foyerService.removeFoyer(1L);

        verify(foyerRepository, times(1)).deleteById(1L);
    }
}
