package fr.soheilb.projet.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class VilleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Ville getVilleSample1() {
        return new Ville().id(1L).nom("nom1").codePostal("codePostal1").nbHabitants(1);
    }

    public static Ville getVilleSample2() {
        return new Ville().id(2L).nom("nom2").codePostal("codePostal2").nbHabitants(2);
    }

    public static Ville getVilleRandomSampleGenerator() {
        return new Ville()
            .id(longCount.incrementAndGet())
            .nom(UUID.randomUUID().toString())
            .codePostal(UUID.randomUUID().toString())
            .nbHabitants(intCount.incrementAndGet());
    }
}
