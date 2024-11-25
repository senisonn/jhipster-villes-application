package fr.soheilb.projet.web.rest;

import static fr.soheilb.projet.domain.VilleAsserts.*;
import static fr.soheilb.projet.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.soheilb.projet.IntegrationTest;
import fr.soheilb.projet.domain.Ville;
import fr.soheilb.projet.repository.VilleRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link VilleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VilleResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_CODE_POSTAL = "AAAAAAAAAA";
    private static final String UPDATED_CODE_POSTAL = "BBBBBBBBBB";

    private static final Integer DEFAULT_NB_HABITANTS = 1;
    private static final Integer UPDATED_NB_HABITANTS = 2;

    private static final String ENTITY_API_URL = "/api/villes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVilleMockMvc;

    private Ville ville;

    private Ville insertedVille;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ville createEntity() {
        return new Ville().nom(DEFAULT_NOM).codePostal(DEFAULT_CODE_POSTAL).nbHabitants(DEFAULT_NB_HABITANTS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ville createUpdatedEntity() {
        return new Ville().nom(UPDATED_NOM).codePostal(UPDATED_CODE_POSTAL).nbHabitants(UPDATED_NB_HABITANTS);
    }

    @BeforeEach
    public void initTest() {
        ville = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedVille != null) {
            villeRepository.delete(insertedVille);
            insertedVille = null;
        }
    }

    @Test
    @Transactional
    void createVille() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Ville
        var returnedVille = om.readValue(
            restVilleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ville)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Ville.class
        );

        // Validate the Ville in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertVilleUpdatableFieldsEquals(returnedVille, getPersistedVille(returnedVille));

        insertedVille = returnedVille;
    }

    @Test
    @Transactional
    void createVilleWithExistingId() throws Exception {
        // Create the Ville with an existing ID
        ville.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVilleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ville)))
            .andExpect(status().isBadRequest());

        // Validate the Ville in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVilles() throws Exception {
        // Initialize the database
        insertedVille = villeRepository.saveAndFlush(ville);

        // Get all the villeList
        restVilleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ville.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].codePostal").value(hasItem(DEFAULT_CODE_POSTAL)))
            .andExpect(jsonPath("$.[*].nbHabitants").value(hasItem(DEFAULT_NB_HABITANTS)));
    }

    @Test
    @Transactional
    void getVille() throws Exception {
        // Initialize the database
        insertedVille = villeRepository.saveAndFlush(ville);

        // Get the ville
        restVilleMockMvc
            .perform(get(ENTITY_API_URL_ID, ville.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ville.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.codePostal").value(DEFAULT_CODE_POSTAL))
            .andExpect(jsonPath("$.nbHabitants").value(DEFAULT_NB_HABITANTS));
    }

    @Test
    @Transactional
    void getNonExistingVille() throws Exception {
        // Get the ville
        restVilleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVille() throws Exception {
        // Initialize the database
        insertedVille = villeRepository.saveAndFlush(ville);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ville
        Ville updatedVille = villeRepository.findById(ville.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVille are not directly saved in db
        em.detach(updatedVille);
        updatedVille.nom(UPDATED_NOM).codePostal(UPDATED_CODE_POSTAL).nbHabitants(UPDATED_NB_HABITANTS);

        restVilleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVille.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedVille))
            )
            .andExpect(status().isOk());

        // Validate the Ville in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVilleToMatchAllProperties(updatedVille);
    }

    @Test
    @Transactional
    void putNonExistingVille() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ville.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVilleMockMvc
            .perform(put(ENTITY_API_URL_ID, ville.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ville)))
            .andExpect(status().isBadRequest());

        // Validate the Ville in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVille() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ville.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVilleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ville))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ville in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVille() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ville.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVilleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ville)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ville in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVilleWithPatch() throws Exception {
        // Initialize the database
        insertedVille = villeRepository.saveAndFlush(ville);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ville using partial update
        Ville partialUpdatedVille = new Ville();
        partialUpdatedVille.setId(ville.getId());

        partialUpdatedVille.codePostal(UPDATED_CODE_POSTAL).nbHabitants(UPDATED_NB_HABITANTS);

        restVilleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVille.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVille))
            )
            .andExpect(status().isOk());

        // Validate the Ville in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVilleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVille, ville), getPersistedVille(ville));
    }

    @Test
    @Transactional
    void fullUpdateVilleWithPatch() throws Exception {
        // Initialize the database
        insertedVille = villeRepository.saveAndFlush(ville);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ville using partial update
        Ville partialUpdatedVille = new Ville();
        partialUpdatedVille.setId(ville.getId());

        partialUpdatedVille.nom(UPDATED_NOM).codePostal(UPDATED_CODE_POSTAL).nbHabitants(UPDATED_NB_HABITANTS);

        restVilleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVille.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVille))
            )
            .andExpect(status().isOk());

        // Validate the Ville in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVilleUpdatableFieldsEquals(partialUpdatedVille, getPersistedVille(partialUpdatedVille));
    }

    @Test
    @Transactional
    void patchNonExistingVille() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ville.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVilleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ville.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ville))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ville in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVille() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ville.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVilleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ville))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ville in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVille() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ville.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVilleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ville)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ville in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVille() throws Exception {
        // Initialize the database
        insertedVille = villeRepository.saveAndFlush(ville);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ville
        restVilleMockMvc
            .perform(delete(ENTITY_API_URL_ID, ville.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return villeRepository.count();
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

    protected Ville getPersistedVille(Ville ville) {
        return villeRepository.findById(ville.getId()).orElseThrow();
    }

    protected void assertPersistedVilleToMatchAllProperties(Ville expectedVille) {
        assertVilleAllPropertiesEquals(expectedVille, getPersistedVille(expectedVille));
    }

    protected void assertPersistedVilleToMatchUpdatableProperties(Ville expectedVille) {
        assertVilleAllUpdatablePropertiesEquals(expectedVille, getPersistedVille(expectedVille));
    }
}
