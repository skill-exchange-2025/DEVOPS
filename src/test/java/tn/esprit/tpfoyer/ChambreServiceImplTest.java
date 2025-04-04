package tn.esprit.tpfoyer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Chambre;
import tn.esprit.tpfoyer.entity.TypeChambre;
import tn.esprit.tpfoyer.repository.ChambreRepository;
import tn.esprit.tpfoyer.service.ChambreServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class ChambreServiceImplTest {
    @Mock
    ChambreRepository chambreRepository;

    @InjectMocks
    ChambreServiceImpl chambreService;

    Chambre chambre;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data using the constructor
        chambre = new Chambre(1L, 101L, TypeChambre.SIMPLE, null, null);
    }

    @Test
    void testRetrieveAllChambres() {
        // Arrange
        when(chambreRepository.findAll()).thenReturn(List.of(chambre));

        // Act
        List<Chambre> result = chambreService.retrieveAllChambres();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(chambre, result.get(0));
        verify(chambreRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveChambre() {
        // Arrange
        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));

        // Act
        Chambre result = chambreService.retrieveChambre(1L);

        // Assert
        assertNotNull(result);
        assertEquals(chambre, result);
        verify(chambreRepository, times(1)).findById(1L);
    }

    @Test
    void testAddChambre() {
        // Arrange
        when(chambreRepository.save(any(Chambre.class))).thenReturn(chambre);

        // Act
        Chambre result = chambreService.addChambre(chambre);

        // Assert
        assertNotNull(result);
        assertEquals(chambre, result);
        verify(chambreRepository, times(1)).save(chambre);
    }

    @Test
    void testModifyChambre() {
        // Arrange
        when(chambreRepository.save(any(Chambre.class))).thenReturn(chambre);

        // Act
        Chambre result = chambreService.modifyChambre(chambre);

        // Assert
        assertNotNull(result);
        assertEquals(chambre, result);
        verify(chambreRepository, times(1)).save(chambre);
    }

    @Test
    void testRemoveChambre() {
        // Act
        chambreService.removeChambre(1L);

        // Assert
        verify(chambreRepository, times(1)).deleteById(1L);
    }

    @Test
    void testRecupererChambresSelonTyp() {
        // Arrange
        TypeChambre typeChambre = TypeChambre.SIMPLE;
        when(chambreRepository.findAllByTypeC(typeChambre)).thenReturn(List.of(chambre));

        // Act
        List<Chambre> result = chambreService.recupererChambresSelonTyp(typeChambre);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(chambre, result.get(0));
        verify(chambreRepository, times(1)).findAllByTypeC(typeChambre);
    }

    @Test
    void testTrouverChambreSelonEtudiant() {
        // Arrange
        long cin = 12345L;
        when(chambreRepository.trouverChselonEt(cin)).thenReturn(chambre);

        // Act
        Chambre result = chambreService.trouverchambreSelonEtudiant(cin);

        // Assert
        assertNotNull(result);
        assertEquals(chambre, result);
        verify(chambreRepository, times(1)).trouverChselonEt(cin);
    }
}
