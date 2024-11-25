package fr.soheilb.projet.web.rest;

import static fr.soheilb.projet.domain.JoueurAsserts.*;
import static fr.soheilb.projet.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.soheilb.projet.IntegrationTest;
import fr.soheilb.projet.domain.Joueur;
import fr.soheilb.projet.repository.JoueurRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link JoueurResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JoueurResourceIT {

    private static final String DEFAULT_PSEUDO = "AAAAAAAAAA";
    private static final String UPDATED_PSEUDO = "BBBBBBBBBB";

    private static final String DEFAULT_MOT_DE_PASSE = "AAAAAAAAAA";
    private static final String UPDATED_MOT_DE_PASSE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_INSCRIPTION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_INSCRIPTION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_EST_ADMINISTRATEUR = false;
    private static final Boolean UPDATED_EST_ADMINISTRATEUR = true;

    private static final String ENTITY_API_URL = "/api/joueurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private JoueurRepository joueurRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJoueurMockMvc;

    private Joueur joueur;

    private Joueur insertedJoueur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Joueur createEntity() {
        return new Joueur()
            .pseudo(DEFAULT_PSEUDO)
            .motDePasse(DEFAULT_MOT_DE_PASSE)
            .dateInscription(DEFAULT_DATE_INSCRIPTION)
            .estAdministrateur(DEFAULT_EST_ADMINISTRATEUR);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Joueur createUpdatedEntity() {
        return new Joueur()
            .pseudo(UPDATED_PSEUDO)
            .motDePasse(UPDATED_MOT_DE_PASSE)
            .dateInscription(UPDATED_DATE_INSCRIPTION)
            .estAdministrateur(UPDATED_EST_ADMINISTRATEUR);
    }

    @BeforeEach
    public void initTest() {
        joueur = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedJoueur != null) {
            joueurRepository.delete(insertedJoueur);
            insertedJoueur = null;
        }
    }

    @Test
    @Transactional
    void createJoueur() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Joueur
        var returnedJoueur = om.readValue(
            restJoueurMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(joueur)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Joueur.class
        );

        // Validate the Joueur in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertJoueurUpdatableFieldsEquals(returnedJoueur, getPersistedJoueur(returnedJoueur));

        insertedJoueur = returnedJoueur;
    }

    @Test
    @Transactional
    void createJoueurWithExistingId() throws Exception {
        // Create the Joueur with an existing ID
        joueur.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJoueurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(joueur)))
            .andExpect(status().isBadRequest());

        // Validate the Joueur in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllJoueurs() throws Exception {
        // Initialize the database
        insertedJoueur = joueurRepository.saveAndFlush(joueur);

        // Get all the joueurList
        restJoueurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(joueur.getId().intValue())))
            .andExpect(jsonPath("$.[*].pseudo").value(hasItem(DEFAULT_PSEUDO)))
            .andExpect(jsonPath("$.[*].motDePasse").value(hasItem(DEFAULT_MOT_DE_PASSE)))
            .andExpect(jsonPath("$.[*].dateInscription").value(hasItem(DEFAULT_DATE_INSCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].estAdministrateur").value(hasItem(DEFAULT_EST_ADMINISTRATEUR.booleanValue())));
    }

    @Test
    @Transactional
    void getJoueur() throws Exception {
        // Initialize the database
        insertedJoueur = joueurRepository.saveAndFlush(joueur);

        // Get the joueur
        restJoueurMockMvc
            .perform(get(ENTITY_API_URL_ID, joueur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(joueur.getId().intValue()))
            .andExpect(jsonPath("$.pseudo").value(DEFAULT_PSEUDO))
            .andExpect(jsonPath("$.motDePasse").value(DEFAULT_MOT_DE_PASSE))
            .andExpect(jsonPath("$.dateInscription").value(DEFAULT_DATE_INSCRIPTION.toString()))
            .andExpect(jsonPath("$.estAdministrateur").value(DEFAULT_EST_ADMINISTRATEUR.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingJoueur() throws Exception {
        // Get the joueur
        restJoueurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingJoueur() throws Exception {
        // Initialize the database
        insertedJoueur = joueurRepository.saveAndFlush(joueur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the joueur
        Joueur updatedJoueur = joueurRepository.findById(joueur.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedJoueur are not directly saved in db
        em.detach(updatedJoueur);
        updatedJoueur
            .pseudo(UPDATED_PSEUDO)
            .motDePasse(UPDATED_MOT_DE_PASSE)
            .dateInscription(UPDATED_DATE_INSCRIPTION)
            .estAdministrateur(UPDATED_EST_ADMINISTRATEUR);

        restJoueurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedJoueur.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedJoueur))
            )
            .andExpect(status().isOk());

        // Validate the Joueur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedJoueurToMatchAllProperties(updatedJoueur);
    }

    @Test
    @Transactional
    void putNonExistingJoueur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        joueur.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJoueurMockMvc
            .perform(put(ENTITY_API_URL_ID, joueur.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(joueur)))
            .andExpect(status().isBadRequest());

        // Validate the Joueur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJoueur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        joueur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJoueurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(joueur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Joueur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJoueur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        joueur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJoueurMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(joueur)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Joueur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJoueurWithPatch() throws Exception {
        // Initialize the database
        insertedJoueur = joueurRepository.saveAndFlush(joueur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the joueur using partial update
        Joueur partialUpdatedJoueur = new Joueur();
        partialUpdatedJoueur.setId(joueur.getId());

        partialUpdatedJoueur.pseudo(UPDATED_PSEUDO).estAdministrateur(UPDATED_EST_ADMINISTRATEUR);

        restJoueurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJoueur.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJoueur))
            )
            .andExpect(status().isOk());

        // Validate the Joueur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJoueurUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedJoueur, joueur), getPersistedJoueur(joueur));
    }

    @Test
    @Transactional
    void fullUpdateJoueurWithPatch() throws Exception {
        // Initialize the database
        insertedJoueur = joueurRepository.saveAndFlush(joueur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the joueur using partial update
        Joueur partialUpdatedJoueur = new Joueur();
        partialUpdatedJoueur.setId(joueur.getId());

        partialUpdatedJoueur
            .pseudo(UPDATED_PSEUDO)
            .motDePasse(UPDATED_MOT_DE_PASSE)
            .dateInscription(UPDATED_DATE_INSCRIPTION)
            .estAdministrateur(UPDATED_EST_ADMINISTRATEUR);

        restJoueurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJoueur.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJoueur))
            )
            .andExpect(status().isOk());

        // Validate the Joueur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJoueurUpdatableFieldsEquals(partialUpdatedJoueur, getPersistedJoueur(partialUpdatedJoueur));
    }

    @Test
    @Transactional
    void patchNonExistingJoueur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        joueur.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJoueurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, joueur.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(joueur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Joueur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJoueur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        joueur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJoueurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(joueur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Joueur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJoueur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        joueur.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJoueurMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(joueur)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Joueur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJoueur() throws Exception {
        // Initialize the database
        insertedJoueur = joueurRepository.saveAndFlush(joueur);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the joueur
        restJoueurMockMvc
            .perform(delete(ENTITY_API_URL_ID, joueur.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return joueurRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Joueur getPersistedJoueur(Joueur joueur) {
        return joueurRepository.findById(joueur.getId()).orElseThrow();
    }

    protected void assertPersistedJoueurToMatchAllProperties(Joueur expectedJoueur) {
        assertJoueurAllPropertiesEquals(expectedJoueur, getPersistedJoueur(expectedJoueur));
    }

    protected void assertPersistedJoueurToMatchUpdatableProperties(Joueur expectedJoueur) {
        assertJoueurAllUpdatablePropertiesEquals(expectedJoueur, getPersistedJoueur(expectedJoueur));
    }
}
