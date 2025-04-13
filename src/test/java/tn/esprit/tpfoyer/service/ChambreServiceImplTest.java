package tn.esprit.tpfoyer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import tn.esprit.tpfoyer.entity.Chambre;
import tn.esprit.tpfoyer.entity.TypeChambre;
import tn.esprit.tpfoyer.repository.ChambreRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

public class ChambreServiceImplTest {

    @Mock
    private ChambreRepository chambreRepository;

    @InjectMocks
    private ChambreServiceImpl chambreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRetrieveAllChambres() {
        Chambre c1 = new Chambre();
        c1.setIdChambre(1L);
        Chambre c2 = new Chambre();
        c2.setIdChambre(2L);

        when(chambreRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<Chambre> chambres = chambreService.retrieveAllChambres();
        assertEquals(2, chambres.size());
    }

    @Test
    void testRetrieveChambre_found() {
        Chambre chambre = new Chambre();
        chambre.setIdChambre(1L);
        chambre.setNumeroChambre(101);
        chambre.setTypeC(TypeChambre.DOUBLE);

        when(chambreRepository.findById(1L)).thenReturn(Optional.of(chambre));

        Chambre result = chambreService.retrieveChambre(1L);
        assertNotNull(result);
        assertEquals(1L, result.getIdChambre());
        assertEquals(101, result.getNumeroChambre());
        assertEquals(TypeChambre.DOUBLE, result.getTypeC());
    }

    @Test
    void testAddChambre() {
        Chambre chambre = new Chambre();
        chambre.setNumeroChambre(202);
        chambre.setTypeC(TypeChambre.SIMPLE);

        when(chambreRepository.save(chambre)).thenReturn(chambre);

        Chambre result = chambreService.addChambre(chambre);
        assertNotNull(result);
        assertEquals(202, result.getNumeroChambre());
        assertEquals(TypeChambre.SIMPLE, result.getTypeC());
    }

    @Test
    void testDeleteChambre() {
        doNothing().when(chambreRepository).deleteById(1L);
        chambreService.removeChambre(1L);
        verify(chambreRepository, times(1)).deleteById(1L);
    }
}
