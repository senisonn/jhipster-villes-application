package fr.soheilb.projet.domain;

import static fr.soheilb.projet.domain.JoueurTestSamples.*;
import static fr.soheilb.projet.domain.RegionTestSamples.*;
import static fr.soheilb.projet.domain.VilleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import fr.soheilb.projet.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class VilleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ville.class);
        Ville ville1 = getVilleSample1();
        Ville ville2 = new Ville();
        assertThat(ville1).isNotEqualTo(ville2);

        ville2.setId(ville1.getId());
        assertThat(ville1).isEqualTo(ville2);

        ville2 = getVilleSample2();
        assertThat(ville1).isNotEqualTo(ville2);
    }

    @Test
    void joueursTest() {
        Ville ville = getVilleRandomSampleGenerator();
        Joueur joueurBack = getJoueurRandomSampleGenerator();

        ville.addJoueurs(joueurBack);
        assertThat(ville.getJoueurs()).containsOnly(joueurBack);
        assertThat(joueurBack.getVille()).isEqualTo(ville);

        ville.removeJoueurs(joueurBack);
        assertThat(ville.getJoueurs()).doesNotContain(joueurBack);
        assertThat(joueurBack.getVille()).isNull();

        ville.joueurs(new HashSet<>(Set.of(joueurBack)));
        assertThat(ville.getJoueurs()).containsOnly(joueurBack);
        assertThat(joueurBack.getVille()).isEqualTo(ville);

        ville.setJoueurs(new HashSet<>());
        assertThat(ville.getJoueurs()).doesNotContain(joueurBack);
        assertThat(joueurBack.getVille()).isNull();
    }

    @Test
    void regionTest() {
        Ville ville = getVilleRandomSampleGenerator();
        Region regionBack = getRegionRandomSampleGenerator();

        ville.setRegion(regionBack);
        assertThat(ville.getRegion()).isEqualTo(regionBack);

        ville.region(null);
        assertThat(ville.getRegion()).isNull();
    }
}
