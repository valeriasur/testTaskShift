package com.example.filter_util;

import com.example.filter_util.stats.FloatStats;
import com.example.filter_util.stats.IntegerStats;
import com.example.filter_util.stats.StringStats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

/**
 * Класс, содержащий основную логику по обработке файлов,
 * фильтрации данных и сбору статистики.
 */
public class FileProcessor {
    // Объекты для сбора статистики по каждому типу данных
    private final IntegerStats integerStats = new IntegerStats();
    private final FloatStats floatStats = new FloatStats();
    private final StringStats stringStats = new StringStats();

    // Объекты для записи в файлы
    private BufferedWriter integerWriter;
    private BufferedWriter floatWriter;
    private BufferedWriter stringWriter;

    public void process(CliArguments args) {
        // Проверка на наличие входных файлов
        if (args.inputFiles == null || args.inputFiles.isEmpty()) {
            System.out.println("Не указаны входные файлы для обработки. Используйте --help для справки.");
            return;
        }
        try {
            // Перебираем все входные файлы, которые передал пользователь
            for (File inputFile : args.inputFiles) {
                processSingleFile(inputFile, args);
            }
            // Вывод статистики
            printStatistics(args.isShortStats, args.isFullStats);
        } catch (Exception e) {
            System.err.println("Произошла критическая ошибка во время обработки: " + e.getMessage());
        } finally {
            closeWriters();
        }
    }

    private void processSingleFile(File inputFile, CliArguments args) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            // Читаем файл построчно
            while ((line = reader.readLine()) != null) {
                classifyAndWriteLine(line, args);
            }
        } catch (IOException e) {
            // Если файл не найден или не может быть прочитан
            System.err.println("Ошибка при чтении файла " + inputFile.getName() + ": " + e.getMessage());
            // Продолжаем выполнение, обрабатываем следующий файл
        }
    }

    private void classifyAndWriteLine(String line, CliArguments args) {
        // Пытаемся определить тип данных
        try {
            long longValue = Long.parseLong(line);
            writeToFile("integers.txt", line, args);
            integerStats.update(longValue);
            return; // Успешно, выходим из метода
        } catch (NumberFormatException e) {
            // Это не целое число, пробуем дальше
        }

        try {
            double doubleValue = Double.parseDouble(line);
            writeToFile("floats.txt", line, args);
            floatStats.update(doubleValue);
            return; // Успешно, выходим из метода
        } catch (NumberFormatException e) {
            // Это и не вещественное число, значит, это строка
        }

        writeToFile("strings.txt", line, args);
        stringStats.update(line);
    }

    private void writeToFile(String baseFileName, String line, CliArguments args) {
        try {
            // Формируем полный путь к выходному файлу, используя путь и префикс из
            // аргументов
            Path outputPath = Paths.get(args.outputPath, args.filePrefix + baseFileName);

            // Создаем родительские директории, если их не существует
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }

            // Получаем режим добавления из объекта args
            boolean isAppendMode = args.isAddMode;

            // Создаем BufferedWriter для нужного типа данных, если он еще не создан
            switch (baseFileName) {
                case "integers.txt":
                    if (integerWriter == null) {
                        integerWriter = new BufferedWriter(new FileWriter(outputPath.toFile(), isAppendMode));
                    }
                    integerWriter.write(line);
                    integerWriter.newLine();
                    break;
                case "floats.txt":
                    if (floatWriter == null) {
                        floatWriter = new BufferedWriter(new FileWriter(outputPath.toFile(), isAppendMode));
                    }
                    floatWriter.write(line);
                    floatWriter.newLine();
                    break;
                case "strings.txt":
                    if (stringWriter == null) {
                        stringWriter = new BufferedWriter(new FileWriter(outputPath.toFile(), isAppendMode));
                    }
                    stringWriter.write(line);
                    stringWriter.newLine();
                    break;
            }
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл " + baseFileName + ": " + e.getMessage());
        }
    }

    private void printStatistics(boolean isShort, boolean isFull) {
        if (!isShort && !isFull)
            return; // Если ни один флаг статистики не указан, ничего не выводим

        System.out.println("\n--- Статистика ---");
        // Выводим статистику по каждому типу данных, только если такие данные были
        // найдены
        if (integerStats.getCount() > 0) {
            System.out.println("Целые числа:");
            System.out.println("  Количество: " + integerStats.getCount());
            if (isFull) {
                System.out.println("  Минимальное: " + integerStats.getMin());
                System.out.println("  Максимальное: " + integerStats.getMax());
                System.out.println("  Сумма: " + integerStats.getSum());
                System.out.println("  Среднее: " + new DecimalFormat("#.##").format(integerStats.getAverage()));
            }
        }

        if (floatStats.getCount() > 0) {
            System.out.println("Вещественные числа:");
            System.out.println("  Количество: " + floatStats.getCount());
            if (isFull) {
                System.out.println("  Минимальное: " + floatStats.getMin());
                System.out.println("  Максимальное: " + floatStats.getMax());
                System.out.println("  Сумма: " + floatStats.getSum());
                System.out.println("  Среднее: " + new DecimalFormat("#.##").format(floatStats.getAverage()));
            }
        }

        if (stringStats.getCount() > 0) {
            System.out.println("Строки:");
            System.out.println("  Количество: " + stringStats.getCount());
            if (isFull) {
                System.out.println("  Самая короткая: " + stringStats.getShortestLength());
                System.out.println("  Самая длинная: " + stringStats.getLongestLength());
            }
        }
    }

    // Метод для закрытия всех потоков записи
    private void closeWriters() {
        try {
            if (integerWriter != null)
                integerWriter.close();
            if (floatWriter != null)
                floatWriter.close();
            if (stringWriter != null)
                stringWriter.close();
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии файлов: " + e.getMessage());
        }
    }
}