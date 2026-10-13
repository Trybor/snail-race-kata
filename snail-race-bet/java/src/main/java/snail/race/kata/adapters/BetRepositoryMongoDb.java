package snail.race.kata.adapters;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import snail.race.kata.domain.Bet;
import snail.race.kata.domain.BetRepository;

import java.util.ArrayList;
import java.util.List;

public class BetRepositoryMongoDb implements BetRepository {

    private final MongoCollection<Bet> bets;

    public BetRepositoryMongoDb(MongoDatabase database) {
        this.bets = database.getCollection("bet", Bet.class);
    }

    @Override
    public void register(Bet bet) {
        bets.insertOne(bet);
    }

    @Override
    public List<Bet> findByDateRange(long from, long to) {
        return bets.find(Filters.and(
                           Filters.gte("timestamp", from),
                           Filters.lt("timestamp", to)))
                   .into(new ArrayList<>());
    }
}
