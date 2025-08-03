package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    enum StatisticsMode { NONE, SHORT, FULL }

    public static void main(String[] args) {

        String resultPath = "";
        String resultPrefix = "";
        boolean isAddOption = false;
        StatisticsMode stats = StatisticsMode.NONE;

        List<String> fileList = new ArrayList<>();

        // Поиск аргументов и названий файлов в args

        for (int i = 0; i < args.length; i++) {

            switch (args[i]) {
                case "-o":
                    try {
                        resultPath = args[++i];
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        resultPath = "";
                    }
                    break;
                case "-p":
                    try {
                        resultPrefix = args[++i];
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        resultPrefix = "";
                    }
                    break;
                case "-a":
                    isAddOption = true;
                    break;
                case "-s":
                    stats = StatisticsMode.SHORT;
                    break;
                case "-f":
                    stats = StatisticsMode.FULL;
                    break;
                default:
                    fileList.add(args[i]);
            }
        }

        // Чтение файлов

        List<Long> intList = new ArrayList<>();
        List<Double> doubleList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();

        for (String fileName : fileList) {

            File file = new File(fileName);

            try (Scanner fileReader = new Scanner(file)){

                while (fileReader.hasNextLine()) {
                    String str = fileReader.nextLine();
                    // Обработка элемента (определяем тип данных)
                    try {
                        intList.add(Long.parseLong(str));
                    } catch (NumberFormatException e) {
                        try {
                            doubleList.add(Double.parseDouble(str));
                        } catch (NumberFormatException ex) {
                            stringList.add(str);
                        }
                    }
                }

                System.out.println("Файл успешно обработан: " + fileName);

            }
            catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }

        // Записываем данные в файлы вывода, предварительно проверив списки на наличие значений, чтобы не создавать пустые файлы

        if (!intList.isEmpty())
            writeToFile(resultPath + resultPrefix + "integers.txt", intList.stream().map(String::valueOf).collect(Collectors.toList()), isAddOption);
        if (!doubleList.isEmpty())
            writeToFile(resultPath + resultPrefix + "floats.txt", doubleList.stream().map(String::valueOf).collect(Collectors.toList()), isAddOption);
        if (!stringList.isEmpty())
            writeToFile(resultPath + resultPrefix + "strings.txt", stringList, isAddOption);

        printStatistics(stats, intList, doubleList, stringList);
    }

    private static void writeToFile(String file, List<String> values, boolean isAddOption) {

        try (FileWriter writer = new FileWriter(file, isAddOption)) {
            writer.write(String.join("\n", values));
            writer.append("\n");
            System.out.printf("Файл %s был успешно записан.\n", file);
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printStatistics(StatisticsMode stats, List<Long> intList, List<Double> doubleList, List<String> stringList) {
        int intCount = intList.size();
        int doubleCount = doubleList.size();
        int stringCount = stringList.size();

        if (stats != StatisticsMode.NONE) {
            System.out.printf("Всего записано %d элементов: целые числа - %d, вещественные числа - %d, строки - %d.\n", intCount + doubleCount + stringCount, intCount, doubleCount, stringCount);
        }
        if (stats == StatisticsMode.FULL) {
            if (!intList.isEmpty())
            {
                long maxValue = Collections.max(intList);
                long minValue = Collections.min(intList);
                double avgValue = intList.stream().mapToLong(Long::longValue).average().getAsDouble();
                System.out.printf("Целые числа: максимальное - %d, минимальное - %d, среднее - %.2f.\n", maxValue, minValue, avgValue);
            }
            if (!doubleList.isEmpty())
            {
                double maxValue = Collections.max(doubleList);
                double minValue = Collections.min(doubleList);
                double avgValue = doubleList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
                System.out.printf("Вещественные числа: максимальное - %f, минимальное - %f, среднее - %f.\n", maxValue, minValue, avgValue);
            }
            if (!stringList.isEmpty()) {
                int maxLength = stringList.stream().map(String::length).max(Integer::compareTo).get();
                int minLength = stringList.stream().map(String::length).min(Integer::compareTo).get();
                System.out.printf("Строки: самая длинная - %d символов, самая короткая - %d\n", maxLength, minLength);
            }
        }
    }

}