package tn.esprit.tpfoyer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Bloc;
import tn.esprit.tpfoyer.repository.BlocRepository;
import tn.esprit.tpfoyer.service.BlocServiceImpl;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BlocServiceImplTest {

    @Mock
    private BlocRepository blocRepository;

    @InjectMocks
    private BlocServiceImpl blocService;

    private Bloc bloc;

    @BeforeEach
    void setUp() {
        bloc = new Bloc(1L, "Bloc A", 100, null, new HashSet<>());
    }


    @Test
    void testRetrieveAllBlocs() {
        List<Bloc> blocs = new ArrayList<>();
        blocs.add(bloc);

        when(blocRepository.findAll()).thenReturn(blocs);

        List<Bloc> result = blocService.retrieveAllBlocs();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bloc A", result.get(0).getNomBloc());
        verify(blocRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveBlocsSelonCapacite() {
        List<Bloc> blocs = new ArrayList<>();
        blocs.add(bloc);

        when(blocRepository.findAll()).thenReturn(blocs);

        List<Bloc> result = blocService.retrieveBlocsSelonCapacite(50L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bloc A", result.get(0).getNomBloc());
        verify(blocRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveBloc() {
        when(blocRepository.findById(1L)).thenReturn(Optional.of(bloc));

        Bloc result = blocService.retrieveBloc(1L);

        assertNotNull(result);
        assertEquals("Bloc A", result.getNomBloc());
        verify(blocRepository, times(1)).findById(1L);
    }

    @Test
    void testAddBloc() {
        when(blocRepository.save(bloc)).thenReturn(bloc);

        Bloc result = blocService.addBloc(bloc);

        assertNotNull(result);
        assertEquals("Bloc A", result.getNomBloc());
        verify(blocRepository, times(1)).save(bloc);
    }

    @Test
    void testModifyBloc() {
        when(blocRepository.save(bloc)).thenReturn(bloc);

        Bloc result = blocService.modifyBloc(bloc);

        assertNotNull(result);
        assertEquals("Bloc A", result.getNomBloc());
        verify(blocRepository, times(1)).save(bloc);
    }

    @Test
    void testRemoveBloc() {
        doNothing().when(blocRepository).deleteById(1L);

        blocService.removeBloc(1L);

        verify(blocRepository, times(1)).deleteById(1L);
    }

    @Test
    void testTrouverBlocsSansFoyer() {
        List<Bloc> blocs = new ArrayList<>();
        blocs.add(bloc);

        when(blocRepository.findAllByFoyerIsNull()).thenReturn(blocs);

        List<Bloc> result = blocService.trouverBlocsSansFoyer();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bloc A", result.get(0).getNomBloc());
        verify(blocRepository, times(1)).findAllByFoyerIsNull();
    }

    @Test
    void testTrouverBlocsParNomEtCap() {
        List<Bloc> blocs = new ArrayList<>();
        blocs.add(bloc);

        when(blocRepository.findAllByNomBlocAndCapaciteBloc("Bloc A", 100L)).thenReturn(blocs);

        List<Bloc> result = blocService.trouverBlocsParNomEtCap("Bloc A", 100L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bloc A", result.get(0).getNomBloc());
        verify(blocRepository, times(1)).findAllByNomBlocAndCapaciteBloc("Bloc A", 100L);
    }

    // Test for Bloc not found
    @Test
    void testRetrieveBlocNotFound() {
        // Test the case when the Bloc is not found
        when(blocRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> blocService.retrieveBloc(1L));
        verify(blocRepository, times(1)).findById(1L);
    }
}
