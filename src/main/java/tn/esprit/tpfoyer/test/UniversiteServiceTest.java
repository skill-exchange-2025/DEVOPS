package tn.esprit.tpfoyer.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.tpfoyer.entity.Universite;
import tn.esprit.tpfoyer.repository.UniversiteRepository;
import tn.esprit.tpfoyer.service.UniversiteServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UniversiteServiceTest {

    @Mock
    private UniversiteRepository universiteRepository;

    @InjectMocks
    private UniversiteServiceImpl universiteService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRetrieveUniversite() {
        Universite mockU = new Universite();
        mockU.setIdUniversite(1L);
        mockU.setNomUniversite("ESPRIT");
        mockU.setAdresse("Ariana");

        when(universiteRepository.findById(1L)).thenReturn(Optional.of(mockU));

        Universite result = universiteService.retrieveUniversite(1L);

        assertEquals("ESPRIT", result.getNomUniversite());
        assertEquals("Ariana", result.getAdresse());
    }
}
