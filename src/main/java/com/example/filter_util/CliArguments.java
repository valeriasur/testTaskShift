package com.example.filter_util;

import java.io.File;
import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "file-filter-util", mixinStandardHelpOptions = true)

public class CliArguments {

    // Справка
    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Справочное сообщение")
    private boolean helpRequested;

    // Опция для указания пути вывода, по умолчанию - текущая директория
    @Option(names = { "-o", "--output" }, description = "Путь для выходных файлов", paramLabel = "PATH")
    public String outputPath = ".";

    // Опция для указания префикса имен файлов, по умолчанию - без префикса
    @Option(names = { "-p", "--prefix" }, description = "Префикс для имен выходных файлов", paramLabel = "PREFIX")
    public String filePrefix = "";

    // Флаг, включающий режим добавления данных в существующие файлы
    @Option(names = { "-a", "--add" }, description = "Режим добавления в существующие файлы")
    public boolean isAddMode = false;

    // Флаг, включающий вывод краткой статистики
    @Option(names = { "-s", "--short-stats" }, description = "Вывести краткую статистику")
    public boolean isShortStats = false;

    // Флаг, включающий вывод полной статистики
    @Option(names = { "-f", "--full-stats" }, description = "Вывести полную статистику")
    public boolean isFullStats = false;

    @Parameters(index = "0..*", description = "Входные файлы для обработки")
    public List<File> inputFiles;
}
