package snail.race.kata.adapters;

import snail.race.kata.domain.BetRepository;
import snail.race.kata.domain.BetRepositoryContract;

/**
 * runs the BetRepository contract against the in memory fake
 */
public class BetRepositoryFakeTest extends BetRepositoryContract {

    @Override
    protected BetRepository createRepository() {
        return new BetRepositoryFake();
    }
}
