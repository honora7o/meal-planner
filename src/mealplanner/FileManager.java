package mealplanner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class FileManager {
    public void savePlanToFile(String fileName, Map<String, Integer> ingredientCountMap) {
        File file = new File(fileName);
        try {
            PrintWriter writer = new PrintWriter(file);
            for (Map.Entry<String, Integer> entry : ingredientCountMap.entrySet()) {
                String ingredient = entry.getKey();
                int ingredientCount = entry.getValue();
                if (ingredientCount > 1) {
                    writer.printf("%s x%d\n", ingredient, ingredientCount);
                } else {
                    writer.printf("%s\n", ingredient);
                }
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
