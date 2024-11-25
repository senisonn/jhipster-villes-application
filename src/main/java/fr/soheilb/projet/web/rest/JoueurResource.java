package fr.soheilb.projet.web.rest;

import fr.soheilb.projet.domain.Joueur;
import fr.soheilb.projet.repository.JoueurRepository;
import fr.soheilb.projet.service.JoueurService;
import fr.soheilb.projet.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link fr.soheilb.projet.domain.Joueur}.
 */
@RestController
@RequestMapping("/api/joueurs")
public class JoueurResource {

    private static final Logger LOG = LoggerFactory.getLogger(JoueurResource.class);

    private static final String ENTITY_NAME = "joueur";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JoueurService joueurService;

    private final JoueurRepository joueurRepository;

    public JoueurResource(JoueurService joueurService, JoueurRepository joueurRepository) {
        this.joueurService = joueurService;
        this.joueurRepository = joueurRepository;
    }

    /**
     * {@code POST  /joueurs} : Create a new joueur.
     *
     * @param joueur the joueur to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new joueur, or with status {@code 400 (Bad Request)} if the joueur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Joueur> createJoueur(@RequestBody Joueur joueur) throws URISyntaxException {
        LOG.debug("REST request to save Joueur : {}", joueur);
        if (joueur.getId() != null) {
            throw new BadRequestAlertException("A new joueur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        joueur = joueurService.save(joueur);
        return ResponseEntity.created(new URI("/api/joueurs/" + joueur.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, joueur.getId().toString()))
            .body(joueur);
    }

    /**
     * {@code PUT  /joueurs/:id} : Updates an existing joueur.
     *
     * @param id the id of the joueur to save.
     * @param joueur the joueur to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated joueur,
     * or with status {@code 400 (Bad Request)} if the joueur is not valid,
     * or with status {@code 500 (Internal Server Error)} if the joueur couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Joueur> updateJoueur(@PathVariable(value = "id", required = false) final Long id, @RequestBody Joueur joueur)
        throws URISyntaxException {
        LOG.debug("REST request to update Joueur : {}, {}", id, joueur);
        if (joueur.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, joueur.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!joueurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        joueur = joueurService.update(joueur);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, joueur.getId().toString()))
            .body(joueur);
    }

    /**
     * {@code PATCH  /joueurs/:id} : Partial updates given fields of an existing joueur, field will ignore if it is null
     *
     * @param id the id of the joueur to save.
     * @param joueur the joueur to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated joueur,
     * or with status {@code 400 (Bad Request)} if the joueur is not valid,
     * or with status {@code 404 (Not Found)} if the joueur is not found,
     * or with status {@code 500 (Internal Server Error)} if the joueur couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Joueur> partialUpdateJoueur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Joueur joueur
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Joueur partially : {}, {}", id, joueur);
        if (joueur.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, joueur.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!joueurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Joueur> result = joueurService.partialUpdate(joueur);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, joueur.getId().toString())
        );
    }

    /**
     * {@code GET  /joueurs} : get all the joueurs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of joueurs in body.
     */
    @GetMapping("")
    public List<Joueur> getAllJoueurs() {
        LOG.debug("REST request to get all Joueurs");
        return joueurService.findAll();
    }

    /**
     * {@code GET  /joueurs/:id} : get the "id" joueur.
     *
     * @param id the id of the joueur to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the joueur, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Joueur> getJoueur(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Joueur : {}", id);
        Optional<Joueur> joueur = joueurService.findOne(id);
        return ResponseUtil.wrapOrNotFound(joueur);
    }

    /**
     * {@code DELETE  /joueurs/:id} : delete the "id" joueur.
     *
     * @param id the id of the joueur to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJoueur(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Joueur : {}", id);
        joueurService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
