package PR1;

import java.util.Scanner;
import java.util.concurrent.*;

//Zverev
public class Ex2 {
    //Zverev
    private static CompletableFuture<Integer> calculateSquare(int number) {
        // Запуск асинхронной задачи
        return CompletableFuture.supplyAsync(() -> {
            int delayInSeconds = ThreadLocalRandom.current().nextInt(1, 6);
            try {
                Thread.sleep(delayInSeconds * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return number * number;
        });
    }

    //Zverev
    public static void main(String[] args) {

        // Создаем пул потоков
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        // Создаем бесконечный цикл для обработки запросов
        while (true) {
            try {
                System.out.println("Введите число (или 'exit' для выхода): ");
                Scanner scanner = new Scanner(System.in);
                String userInput = scanner.nextLine();
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
                int number = Integer.parseInt(userInput);

                // Прием выполнения асинхронной функции в другом потоке
                calculateSquare(number).thenAcceptAsync(result ->
                        System.out.println("Результат: " + result), executorService)
                        .exceptionally(e -> {
                            System.err.println("Ошибка обработки запроса: " + e.getMessage());
                            return null;
                        });

            } catch (NumberFormatException e) {
                System.err.println("Неверный формат числа. Пожалуйста, введите целое число.");
            }
        }

        executorService.shutdown();
        System.out.println("Автор: " + "Зверев А.А. ИКБО-20-21");
    }
}
