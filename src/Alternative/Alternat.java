package Alternative;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

public class Alternat {
        public static void main(String[] args) {
            int[][] crimesWithArrest = new int[7][24];
            int[][] crimesWithoutArrest = new int[7][24];

            long startTime = System.nanoTime();

            try (FileReader fileReader = new FileReader("data/crimes8m.csv");
                 BufferedReader reader = new BufferedReader(fileReader)) {

                // Überschriften überspringen
                reader.readLine();
                String line;

                while ((line = reader.readLine()) != null) {
                    // Schnellerer CSV-Split (Hinweis: falls Werte Kommas in Anführungszeichen enthalten,
                    // braucht man einen robusten Parser, aber standard .split() ist oft schneller,
                    // wenn keine Kommas im Text sind. Dein splitCSV ist aber funktional ok).
                    String[] data = splitCSV(line);

                    // Jahr direkt parsen (Index 17 prüfen)
                    if (data.length > 17) {
                        try {
                            int year = Integer.parseInt(data[17]);
                            if (year >= 2010 && year <= 2020) {
                                LocalDateTime dateTime = LocalDateTime.parse(data[2]);
                                int weekday = dateTime.getDayOfWeek().getValue();
                                int hour = dateTime.getHour();
                                boolean arrest = Boolean.parseBoolean(data[8]);

                                if (arrest) {
                                    crimesWithArrest[weekday - 1][hour]++;
                                } else {
                                    crimesWithoutArrest[weekday - 1][hour]++;
                                }
                            }
                        } catch (NumberFormatException e) {
                            // Ungültige Zeile überspringen
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            long endTime = System.nanoTime();
            double runtime = (endTime - startTime) / 1_000_000.0;
            System.out.println("Laufzeit: " + runtime + " ms");

            String[] weekdays = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"};
            System.out.println("\nWochentag | Stunde | Mit Arrest | Ohne Arrest");
            for (int day = 0; day < 7; day++) {
                for (int hour = 0; hour < 24; hour++) {
                    System.out.println(weekdays[day] + " | " +
                            String.format("%02d", hour) + " | " +
                            crimesWithArrest[day][hour] + " | " +
                            crimesWithoutArrest[day][hour]);
                }
            }
        }

        public static String[] splitCSV(String line) {
            String[] values = new String[22];
            StringBuilder value = new StringBuilder();
            boolean insideQuotes = false;
            int column = 0;

            for (int i = 0; i < line.length() && column < values.length; i++) {
                char character = line.charAt(i);
                if (character == '"') {
                    insideQuotes = !insideQuotes;
                } else if (character == ',' && !insideQuotes) {
                    values[column] = value.toString();
                    value.setLength(0);
                    column++;
                } else {
                    value.append(character);
                }
            }
            if (column < values.length) {
                values[column] = value.toString();
            }
            return values;
        }

}
