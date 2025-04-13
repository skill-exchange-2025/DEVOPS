package tn.esprit.tpfoyer.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.tpfoyer.entity.Chambre;
import tn.esprit.tpfoyer.entity.TypeChambre;
import tn.esprit.tpfoyer.repository.ChambreRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ChambreServiceImpl implements IChambreService {

    ChambreRepository chambreRepository;

    public List<Chambre> retrieveAllChambres() {
        log.info("Starting method retrieveAllChambres...");
        List<Chambre> listC = chambreRepository.findAll();
        log.debug("Found {} chambres", listC.size());
        log.info("Finished method retrieveAllChambres.");
        return listC;
    }

    public Chambre retrieveChambre(Long chambreId) {
        log.info("Retrieving chambre with ID: {}", chambreId);
        Optional<Chambre> chambre = chambreRepository.findById(chambreId);
        if (chambre.isPresent()) {
            log.debug("Chambre found: {}", chambre.get());
            return chambre.get();
        } else {
            log.error("No chambre found with ID: {}", chambreId);
            return null;
        }
    }

    public Chambre addChambre(Chambre c) {
        log.info("Adding new chambre: {}", c);
        Chambre saved = chambreRepository.save(c);
        log.debug("Chambre saved: {}", saved);
        return saved;
    }

    public Chambre modifyChambre(Chambre c) {
        log.info("Modifying chambre: {}", c);
        Chambre updated = chambreRepository.save(c);
        log.debug("Chambre updated: {}", updated);
        return updated;
    }

    public void removeChambre(Long chambreId) {
        log.info("Deleting chambre with ID: {}", chambreId);
        try {
            chambreRepository.deleteById(chambreId);
            log.info("Successfully deleted chambre with ID: {}", chambreId);
        } catch (Exception e) {
            log.error("Error deleting chambre with ID {}: {}", chambreId, e.getMessage());
        }
    }

    public List<Chambre> recupererChambresSelonTyp(TypeChambre tc) {
        log.info("Fetching chambres by TypeChambre: {}", tc);
        List<Chambre> chambres = chambreRepository.findAllByTypeC(tc);
        log.debug("Found {} chambres for type {}", chambres.size(), tc);
        return chambres;
    }

    public Chambre trouverchambreSelonEtudiant(long cin) {
        log.info("Searching for chambre by student CIN: {}", cin);
        Chambre chambre = chambreRepository.trouverChselonEt(cin);
        if (chambre != null) {
            log.debug("Chambre found for CIN {}: {}", cin, chambre);
        } else {
            log.warn("No chambre found for student CIN: {}", cin);
        }
        return chambre;
    }
}
