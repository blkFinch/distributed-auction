package shared;

import com.github.javafaker.Faker;
import shared.Items.Item;

/**
 * Get Fake items for use in auctions
 */
public class ItemFaker {

    public static Item randomizedPokeon(){
        Faker faker = new Faker();
        String name = faker.pokemon().name();
        String desc = faker.overwatch().quote();
        int min = faker.number().numberBetween(10,50000);

        return  new Item(name,desc,min,0);
    }
}
