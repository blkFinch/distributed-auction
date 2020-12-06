package shared.Items;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class ItemSpecsList {
    protected static ArrayList<ItemSpecs> itemsList;

    /**
     * createItemSpecsList reads a list of names, descriptions and minimum bids
     * from a file line by line until .
     */
    public static void createItemSpecsList() {
        itemsList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("Resources/itemSpecs.txt"));
            String specsString = br.readLine();
            do {
                ItemSpecs specs = new ItemSpecs();
                String[] companies = specsString.split(":");
                specs.name = companies[0];
                specs.description = companies[1];
                specs.minimumBid = Integer.parseInt(companies[2]);
                //maybe add ID?
                itemsList.add(specs);
            } while((specsString = br.readLine()) != null);
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * getRandomElement
     * @return "" ItemSpecs
     */
    public static ItemSpecs getRandomElement() {
        Random random = new Random();
        int size = itemsList.size();
        ItemSpecs itemSpec = itemsList.get(random.nextInt(size));
        itemsList.remove(itemSpec);
        return itemSpec;
    }
}
