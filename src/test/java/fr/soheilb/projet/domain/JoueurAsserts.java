package fr.soheilb.projet.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class JoueurAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertJoueurAllPropertiesEquals(Joueur expected, Joueur actual) {
        assertJoueurAutoGeneratedPropertiesEquals(expected, actual);
        assertJoueurAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertJoueurAllUpdatablePropertiesEquals(Joueur expected, Joueur actual) {
        assertJoueurUpdatableFieldsEquals(expected, actual);
        assertJoueurUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertJoueurAutoGeneratedPropertiesEquals(Joueur expected, Joueur actual) {
        assertThat(expected)
            .as("Verify Joueur auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertJoueurUpdatableFieldsEquals(Joueur expected, Joueur actual) {
        assertThat(expected)
            .as("Verify Joueur relevant properties")
            .satisfies(e -> assertThat(e.getPseudo()).as("check pseudo").isEqualTo(actual.getPseudo()))
            .satisfies(e -> assertThat(e.getMotDePasse()).as("check motDePasse").isEqualTo(actual.getMotDePasse()))
            .satisfies(e -> assertThat(e.getDateInscription()).as("check dateInscription").isEqualTo(actual.getDateInscription()))
            .satisfies(e -> assertThat(e.getEstAdministrateur()).as("check estAdministrateur").isEqualTo(actual.getEstAdministrateur()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertJoueurUpdatableRelationshipsEquals(Joueur expected, Joueur actual) {
        assertThat(expected)
            .as("Verify Joueur relationships")
            .satisfies(e -> assertThat(e.getVille()).as("check ville").isEqualTo(actual.getVille()));
    }
}
