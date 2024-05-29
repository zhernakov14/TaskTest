package ru.andr.tasktest;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try(BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
            //BufferedReader reader = new BufferedReader(new FileReader(args[0]))
            //BufferedReader reader = new BufferedReader(new FileReader("lng.txt"))
            List<Set<String>> groups = new ArrayList<>();
            List<Map<String, Integer>> parts = new ArrayList<>();

            String line = reader.readLine();
            while (line != null) {
                String[] columns = getCorrectRows(line);
                Integer groupNumber = null;
                for (int i = 0; i < Math.min(parts.size(), columns.length); i++) {
                    Integer groupNumberTemp = parts.get(i).get(columns[i]);
                    if (groupNumberTemp != null) {
                        if (groupNumber == null) {
                            groupNumber = groupNumberTemp;
                        } else if (!Objects.equals(groupNumber, groupNumberTemp)) {
                            for (String line2 : groups.get(groupNumberTemp)) {
                                groups.get(groupNumber).add(line2);
                                addToMap(getCorrectRows(line2), groupNumber, parts);
                            }
                            groups.set(groupNumberTemp, new HashSet<>());
                        }
                    }
                }
                if (groupNumber == null) {
                    if (Arrays.stream(columns).anyMatch(s -> !s.isEmpty())) {
                        groups.add(new HashSet<>(List.of(line)));
                        addToMap(columns, groups.size() - 1, parts);
                    }
                } else {
                    groups.get(groupNumber).add(line);
                    addToMap(columns, groupNumber, parts);
                }
                line = reader.readLine();
            }
            System.out.println("Групп размера больше 1: " + groups.stream().filter(s -> s.size() > 1).count());
            groups.sort(Comparator.comparingInt(s -> -s.size()));
            String outputFileName = "out.txt";
            try (BufferedWriter writter = new BufferedWriter(new FileWriter(outputFileName))) {
                int i = 0;
                for (Set<String> group : groups) {
                    i++;
                    writter.write("Группа " + i + "\n");
                    for (String val : group) {
                        writter.write(val + "\n");
                    }
                }
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Время выполнения = " + (end - start) + " мс");
    }

    private static String[] getCorrectRows(String line) {
        for (int i = 1; i < line.length() - 1; i++) {
            if (line.charAt(i) == '"' && line.charAt(i - 1) != ';' && line.charAt(i + 1) != ';') {
                return new String[0];
            }
        }
        return line.replaceAll("\"", "").split(";");
    }

    private static void addToMap(String[] newValues, int groupNumber, List<Map<String, Integer>> parts) {
        for (int i = 0; i < newValues.length; i++) {
            if (newValues[i].isEmpty()) {
                continue;
            }
            if (i < parts.size()) {
                parts.get(i).put(newValues[i], groupNumber);
            } else {
                HashMap<String, Integer> map = new HashMap<>();
                map.put(newValues[i], groupNumber);
                parts.add(map);
            }
        }
    }
}