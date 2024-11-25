package fr.soheilb.projet.service.impl;

import fr.soheilb.projet.domain.Joueur;
import fr.soheilb.projet.repository.JoueurRepository;
import fr.soheilb.projet.service.JoueurService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link fr.soheilb.projet.domain.Joueur}.
 */
@Service
@Transactional
public class JoueurServiceImpl implements JoueurService {

    private static final Logger LOG = LoggerFactory.getLogger(JoueurServiceImpl.class);

    private final JoueurRepository joueurRepository;

    public JoueurServiceImpl(JoueurRepository joueurRepository) {
        this.joueurRepository = joueurRepository;
    }

    @Override
    public Joueur save(Joueur joueur) {
        LOG.debug("Request to save Joueur : {}", joueur);
        return joueurRepository.save(joueur);
    }

    @Override
    public Joueur update(Joueur joueur) {
        LOG.debug("Request to update Joueur : {}", joueur);
        return joueurRepository.save(joueur);
    }

    @Override
    public Optional<Joueur> partialUpdate(Joueur joueur) {
        LOG.debug("Request to partially update Joueur : {}", joueur);

        return joueurRepository
            .findById(joueur.getId())
            .map(existingJoueur -> {
                if (joueur.getPseudo() != null) {
                    existingJoueur.setPseudo(joueur.getPseudo());
                }
                if (joueur.getMotDePasse() != null) {
                    existingJoueur.setMotDePasse(joueur.getMotDePasse());
                }
                if (joueur.getDateInscription() != null) {
                    existingJoueur.setDateInscription(joueur.getDateInscription());
                }
                if (joueur.getEstAdministrateur() != null) {
                    existingJoueur.setEstAdministrateur(joueur.getEstAdministrateur());
                }

                return existingJoueur;
            })
            .map(joueurRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Joueur> findAll() {
        LOG.debug("Request to get all Joueurs");
        return joueurRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Joueur> findOne(Long id) {
        LOG.debug("Request to get Joueur : {}", id);
        return joueurRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Joueur : {}", id);
        joueurRepository.deleteById(id);
    }
}
