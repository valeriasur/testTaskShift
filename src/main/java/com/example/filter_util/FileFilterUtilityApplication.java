package com.example.filter_util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import picocli.CommandLine;

@SpringBootApplication
public class FileFilterUtilityApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FileFilterUtilityApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		CliArguments cliArguments = new CliArguments();

		CommandLine commandLine = new CommandLine(cliArguments);
		commandLine.setUsageHelpWidth(120);
		try {
			// Запуск парсинга
			commandLine.parseArgs(args);

			// Обработка запросов --help
			if (commandLine.isUsageHelpRequested()) {
				System.out.println(commandLine.getUsageMessage());
				return;
			}
			// Создание и запуск основного обработчика
			FileProcessor processor = new FileProcessor();
			processor.process(cliArguments);

		} catch (CommandLine.ParameterException e) {
			System.err.println(e.getMessage());
			commandLine.usage(System.err);
		}
	}
}
