package aufgabeZwei;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        // Zähler für die Verbrechen erstellen
        int[][] crimesWithArrest = new int[7][24];
        int[][] crimesWithoutArrest = new int[7][24];

        long startTime = System.nanoTime();

        FileReader fileReader = new FileReader("data/crimes8m.csv");
        BufferedReader reader = new BufferedReader(fileReader);

        // Überschriften überspringen
        reader.readLine();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = splitCSV(line);

            LocalDateTime dateTime = LocalDateTime.parse(data[2]);
            int year = Integer.parseInt(data[17]);

            // Nur Jahre 2010 bis 2020 berücksichtigen
            if (year >= 2010 && year <= 2020) {
                // getValue() liefert: Montag=1 ... Samstag=6, Sonntag=7
                // Mit "% 7" wird daraus: Sonntag=0, Montag=1 ... Samstag=6
                int weekday = dateTime.getDayOfWeek().getValue() % 7;
                int hour = dateTime.getHour();
                boolean arrest = Boolean.parseBoolean(data[8]);

                if (arrest) {
                    crimesWithArrest[weekday][hour]++;
                } else {
                    crimesWithoutArrest[weekday][hour]++;
                }
            }
        }

        reader.close();

        long endTime = System.nanoTime();
        double runtime = (endTime - startTime) / 1_000_000.0;

        // Laufzeit ausgeben
        System.out.println("Laufzeit: " + runtime + " ms");

        // Wochentage beginnend mit Sonntag
        String[] weekdays = {"Sonntag", "Montag", "Dienstag", "Mittwoch",
                "Donnerstag", "Freitag", "Samstag"};

        // Ergebnisse ausgeben
        System.out.println("\nWochentag | Stunde | Mit Arrest | Ohne Arrest");
        for (int day = 0; day < 7; day++) {
            for (int hour = 0; hour < 24; hour++) {
                System.out.println(weekdays[day] + " | "
                        + String.format("%02d", hour) + " | "
                        + crimesWithArrest[day][hour] + " | "
                        + crimesWithoutArrest[day][hour]);
            }
        }
    }

    // CSV-Zeile zerlegen und Anführungszeichen beachten
    public static String[] splitCSV(String line) {
        String[] values = new String[22];
        StringBuilder value = new StringBuilder();
        boolean insideQuotes = false;
        int column = 0;

        for (int i = 0; i < line.length(); i++) {
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
        values[column] = value.toString();
        return values;
    }
}
