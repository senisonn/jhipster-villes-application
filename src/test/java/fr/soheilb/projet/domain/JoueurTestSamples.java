package fr.soheilb.projet.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class JoueurTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Joueur getJoueurSample1() {
        return new Joueur().id(1L).pseudo("pseudo1").motDePasse("motDePasse1");
    }

    public static Joueur getJoueurSample2() {
        return new Joueur().id(2L).pseudo("pseudo2").motDePasse("motDePasse2");
    }

    public static Joueur getJoueurRandomSampleGenerator() {
        return new Joueur().id(longCount.incrementAndGet()).pseudo(UUID.randomUUID().toString()).motDePasse(UUID.randomUUID().toString());
    }
}
