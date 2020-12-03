package Auction;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

class ItemSpecs {
    String name;
    String description;
    double minimumBid;
}

public class itemsList {
    protected static ArrayList<ItemSpecs> itemsList;

    /**
     * createItemSpecsList reads a list of names, descriptions and minimum bids
     * from a file.
     */
    public static void createItemSpecsList() {
        try{
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream is = loader.getResourceAsStream("itemSpecs.txt");
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String specsString;
            while((specsString = br.readLine()) != null){
                ItemSpecs specs = new ItemSpecs();
                String[] companies = specsString.split(":");
                specs.name = companies[0];
                specs.description = companies[1];
                specs.minimumBid = companies[2];
                //maybe add ID?
                itemsList.add(specs);
            }
        }catch(Exception e){
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
