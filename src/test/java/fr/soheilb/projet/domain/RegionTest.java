package fr.soheilb.projet.domain;

import static fr.soheilb.projet.domain.RegionTestSamples.*;
import static fr.soheilb.projet.domain.VilleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import fr.soheilb.projet.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RegionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Region.class);
        Region region1 = getRegionSample1();
        Region region2 = new Region();
        assertThat(region1).isNotEqualTo(region2);

        region2.setId(region1.getId());
        assertThat(region1).isEqualTo(region2);

        region2 = getRegionSample2();
        assertThat(region1).isNotEqualTo(region2);
    }

    @Test
    void villesTest() {
        Region region = getRegionRandomSampleGenerator();
        Ville villeBack = getVilleRandomSampleGenerator();

        region.addVilles(villeBack);
        assertThat(region.getVilles()).containsOnly(villeBack);
        assertThat(villeBack.getRegion()).isEqualTo(region);

        region.removeVilles(villeBack);
        assertThat(region.getVilles()).doesNotContain(villeBack);
        assertThat(villeBack.getRegion()).isNull();

        region.villes(new HashSet<>(Set.of(villeBack)));
        assertThat(region.getVilles()).containsOnly(villeBack);
        assertThat(villeBack.getRegion()).isEqualTo(region);

        region.setVilles(new HashSet<>());
        assertThat(region.getVilles()).doesNotContain(villeBack);
        assertThat(villeBack.getRegion()).isNull();
    }
}
