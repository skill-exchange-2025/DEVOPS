import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.tpfoyer.TpFoyerApplication; // Import your main class
import tn.esprit.tpfoyer.control.BlocRestController;
import tn.esprit.tpfoyer.entity.*;
import tn.esprit.tpfoyer.service.IBlocService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BlocRestController.class)
@ContextConfiguration(classes = TpFoyerApplication.class) // Add this line
public class BlocRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IBlocService blocService;

    @Captor
    private ArgumentCaptor<Bloc> blocCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void shouldAddBlocSuccessfully() throws Exception {
        Foyer foyer = new Foyer(1L, "Foyer A", 1L, null, null);
        Chambre chambre1 = new Chambre(1L, 12, TypeChambre.DOUBLE, null, null);
        Set<Chambre> chambres = new HashSet<>();
        chambres.add(chambre1);

        Bloc bloc = new Bloc(1L, "Bloc A", 100, foyer, chambres);
        when(blocService.addBloc(any(Bloc.class))).thenReturn(bloc);

        mockMvc.perform(post("/bloc/add-bloc")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bloc)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idBloc").value(1))
                .andExpect(jsonPath("$.nomBloc").value("Bloc A"));

        verify(blocService).addBloc(blocCaptor.capture());
        assertEquals("Bloc A", blocCaptor.getValue().getNomBloc());
    }

    @Test
    void shouldReturnAllBlocs() throws Exception {
        Foyer foyer = new Foyer(1L, "Foyer A", 1L, null, null);
        Chambre chambre1 = new Chambre(1L, 12, TypeChambre.DOUBLE, null, null);
        Chambre chambre2 = new Chambre(2L, 14, TypeChambre.DOUBLE, null, null);
        Set<Chambre> chambres = new HashSet<>(Arrays.asList(chambre1, chambre2));

        Bloc bloc1 = new Bloc(1L, "Bloc A", 100, foyer, chambres);
        Bloc bloc2 = new Bloc(2L, "Bloc B", 150, foyer, chambres);

        List<Bloc> blocs = Arrays.asList(bloc1, bloc2);
        when(blocService.retrieveAllBlocs()).thenReturn(blocs);

        mockMvc.perform(get("/bloc/retrieve-all-blocs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idBloc").value(1))
                .andExpect(jsonPath("$[1].idBloc").value(2))
                .andExpect(jsonPath("$[0].nomBloc").value("Bloc A"))
                .andExpect(jsonPath("$[1].nomBloc").value("Bloc B"));
    }


    @Test
    void shouldReturnBlocById() throws Exception {
        Foyer foyer = new Foyer(1L, "Foyer A", 1L, null, null);
        Chambre chambre1 = new Chambre(1L, 12, TypeChambre.DOUBLE, null, null);
        Set<Chambre> chambres = new HashSet<>();
        chambres.add(chambre1);

        Bloc bloc = new Bloc(1L, "Bloc A", 100, foyer, chambres);
        when(blocService.retrieveBloc(1L)).thenReturn(bloc);

        mockMvc.perform(get("/bloc/retrieve-bloc/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idBloc").value(1))
                .andExpect(jsonPath("$.nomBloc").value("Bloc A"))
                .andExpect(jsonPath("$.foyer.nomFoyer").value("Foyer A"));
    }



}