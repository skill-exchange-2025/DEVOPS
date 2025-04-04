package tn.esprit.tpfoyer;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Reservation;
import tn.esprit.tpfoyer.repository.ReservationRepository;
import tn.esprit.tpfoyer.service.ReservationServiceImpl;

import java.util.*;
@ExtendWith(MockitoExtension.class)

public class ReservationServiceImplTest {
    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        reservation = new Reservation();
        reservation.setIdReservation("R001");
        reservation.setAnneeUniversitaire(new Date());
        reservation.setEstValide(true);
    }

    @Test
    void testRetrieveAllReservations() {
        // Arrange
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        // Act
        List<Reservation> result = reservationService.retrieveAllReservations();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("R001", result.get(0).getIdReservation());
    }

    @Test
    void testRetrieveReservation() {
        // Arrange
        when(reservationRepository.findById("R001")).thenReturn(java.util.Optional.of(reservation));

        // Act
        Reservation result = reservationService.retrieveReservation("R001");

        // Assert
        assertNotNull(result);
        assertEquals("R001", result.getIdReservation());
    }

    @Test
    void testAddReservation() {
        // Arrange
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        // Act
        Reservation result = reservationService.addReservation(reservation);

        // Assert
        assertNotNull(result);
        assertEquals("R001", result.getIdReservation());
    }

    @Test
    void testModifyReservation() {
        // Arrange
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        // Act
        Reservation result = reservationService.modifyReservation(reservation);

        // Assert
        assertNotNull(result);
        assertEquals("R001", result.getIdReservation());
    }

    @Test
    void testRemoveReservation() {
        // Arrange
        doNothing().when(reservationRepository).deleteById("R001");

        // Act
        reservationService.removeReservation("R001");

        // Assert
        verify(reservationRepository, times(1)).deleteById("R001");
    }

    @Test
    void testTrouverResSelonDateEtStatus() {
        // Arrange
        Date date = new Date();
        when(reservationRepository.findAllByAnneeUniversitaireBeforeAndEstValide(date, true)).thenReturn(List.of(reservation));

        // Act
        List<Reservation> result = reservationService.trouverResSelonDateEtStatus(date, true);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("R001", result.get(0).getIdReservation());
    }
}
