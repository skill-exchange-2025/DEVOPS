package tn.esprit.tpfoyer.service.tpfoyer.service;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import tn.esprit.tpfoyer.service.tpfoyer.entity.Etudiant;
import tn.esprit.tpfoyer.service.tpfoyer.repository.EtudiantRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class EtudiantServiceImpl implements IEtudiantService {

    private static final Logger logger = LogManager.getLogger(EtudiantServiceImpl.class);

    EtudiantRepository etudiantRepository;

    public List<Etudiant> retrieveAllEtudiants() {
        logger.info("Retrieving all students");
        List<Etudiant> etudiants = etudiantRepository.findAll();
        logger.info("Found {} students", etudiants.size());
        return etudiants;
    }

    public Etudiant retrieveEtudiant(Long etudiantId) {
        logger.info("Retrieving student with ID: {}", etudiantId);
        Etudiant etudiant = etudiantRepository.findById(etudiantId).get();
        logger.info("Found student: {}", etudiant);
        return etudiant;
    }

    public Etudiant addEtudiant(Etudiant c) {
        logger.info("Adding new student: {}", c);
        Etudiant savedEtudiant = etudiantRepository.save(c);
        logger.info("Successfully added student with ID: {}", savedEtudiant.getIdEtudiant());
        return savedEtudiant;
    }

    public Etudiant modifyEtudiant(Etudiant c) {
        logger.info("Updating student with ID: {}", c.getIdEtudiant());
        Etudiant updatedEtudiant = etudiantRepository.save(c);
        logger.info("Successfully updated student: {}", updatedEtudiant);
        return updatedEtudiant;
    }

    public void removeEtudiant(Long etudiantId) {
        logger.info("Removing student with ID: {}", etudiantId);
        etudiantRepository.deleteById(etudiantId);
        logger.info("Successfully removed student with ID: {}", etudiantId);
    }

    public Etudiant recupererEtudiantParCin(long cin) {
        logger.info("Searching for student with CIN: {}", cin);
        Etudiant etudiant = etudiantRepository.findEtudiantByCinEtudiant(cin);
        if (etudiant != null) {
            logger.info("Found student by CIN: {}", etudiant);
        } else {
            logger.warn("No student found with CIN: {}", cin);
        }
        return etudiant;
    }
}