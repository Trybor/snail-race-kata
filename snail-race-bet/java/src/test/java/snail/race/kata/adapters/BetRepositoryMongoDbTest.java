package snail.race.kata.adapters;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import snail.race.kata.domain.Bet;
import snail.race.kata.domain.PodiumPronostic;
import java.util.List;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class BetRepositoryMongoDbTest {
    MongoClient mongoClient;
    BetRepositoryMongoDb repository;

    @BeforeEach
    void setUp() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        mongoClient = MongoClients.create("mongodb://localhost");
        MongoDatabase database = mongoClient.getDatabase("contract_testing").withCodecRegistry(pojoCodecRegistry);

        database.getCollection("bet", Bet.class).drop();

        repository = new BetRepositoryMongoDb(database);

    }

    @AfterEach
    void tearDown() {
        mongoClient.close();
    }

    @Test
    void register_a_bets() {
        // register a bet
        // How can we verify the bet is actually inserted ?
        // "Rien n'est plus dangereux qu'une idée quand on en a qu'une" (Emile Chartier)
        // TIPS : Find some (>=3) options before commiting to one
        // Assertions.fail("TODO : write the test, then the implementation😉");
        Bet bet = new Bet("Alice", new PodiumPronostic(1, 2, 3), 42);
        repository.register(bet);

        // find the bet
        List<Bet> found = repository.findByDateRange(0, 100);
        assertThat(found).contains(bet);
    }

    @Test
    void retrieve_only_bets_inside_the_time_range() {
        // given
        Bet beforeRange = new Bet("before", new PodiumPronostic(1, 2, 3), 999L);
        Bet atStart = new Bet("atStart", new PodiumPronostic(1, 2, 3), 1_000L);
        Bet inside = new Bet("inside", new PodiumPronostic(1, 2, 3), 1_500L);
        Bet atEnd = new Bet("atEnd", new PodiumPronostic(1, 2, 3), 2_000L);

        repository.register(beforeRange);
        repository.register(atStart);
        repository.register(inside);
        repository.register(atEnd);

        // when
        List<Bet> found = repository.findByDateRange(1_000L, 2_000L);

        // then: from is included, to is excluded -> from <= timestamp < to
        assertThat(found).hasSize(2).containsOnly(atStart, inside);
    }
}
