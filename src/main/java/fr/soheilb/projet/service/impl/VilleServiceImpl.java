package fr.soheilb.projet.service.impl;

import fr.soheilb.projet.domain.Ville;
import fr.soheilb.projet.repository.VilleRepository;
import fr.soheilb.projet.service.VilleService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link fr.soheilb.projet.domain.Ville}.
 */
@Service
@Transactional
public class VilleServiceImpl implements VilleService {

    private static final Logger LOG = LoggerFactory.getLogger(VilleServiceImpl.class);

    private final VilleRepository villeRepository;

    public VilleServiceImpl(VilleRepository villeRepository) {
        this.villeRepository = villeRepository;
    }

    @Override
    public Ville save(Ville ville) {
        LOG.debug("Request to save Ville : {}", ville);
        return villeRepository.save(ville);
    }

    @Override
    public Ville update(Ville ville) {
        LOG.debug("Request to update Ville : {}", ville);
        return villeRepository.save(ville);
    }

    @Override
    public Optional<Ville> partialUpdate(Ville ville) {
        LOG.debug("Request to partially update Ville : {}", ville);

        return villeRepository
            .findById(ville.getId())
            .map(existingVille -> {
                if (ville.getNom() != null) {
                    existingVille.setNom(ville.getNom());
                }
                if (ville.getCodePostal() != null) {
                    existingVille.setCodePostal(ville.getCodePostal());
                }
                if (ville.getNbHabitants() != null) {
                    existingVille.setNbHabitants(ville.getNbHabitants());
                }

                return existingVille;
            })
            .map(villeRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ville> findAll() {
        LOG.debug("Request to get all Villes");
        return villeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ville> findOne(Long id) {
        LOG.debug("Request to get Ville : {}", id);
        return villeRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Ville : {}", id);
        villeRepository.deleteById(id);
    }
}
