package PR1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

//Zverev
public class Ex1 {
    //Zverev
    public static List<Integer> generateArray10000() { // генерация 10000 рандомных чисел типа Integer в листе
        List<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            int randomNumber = random.nextInt();
            list.add(randomNumber);
        }
        return list;
    }
    //Zverev
    public static int findSum(List<Integer> list) throws
            InterruptedException {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Список пуст или равен null");
        }
        int sum = 0;
        for (int number : list) {
            Thread.sleep(1);
            sum += number;
        }
        return sum;
    }
    //Zverev
    public static int findSumMnogopotok(List<Integer> list) throws
            InterruptedException, ExecutionException {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Список пуст или равен null");
        }
        // Определяем количество доступных процессоров
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Количество доступных процессоров: " + numberOfThreads);
        // Создаем пул потоков для выполнения задач
        ExecutorService executorService =
                Executors.newFixedThreadPool(numberOfThreads);
        // Создаем список задач для каждого подсписка
        List<Callable<Integer>> tasks = new ArrayList<>();
        int batchSize = list.size() / numberOfThreads;

        // Разбиваем список на подсписки и создаем задачи для каждого подсписка
        for (int i = 0; i < numberOfThreads; i++) {
            final int startIndex = i * batchSize;
            final int endIndex = (i == numberOfThreads - 1) ? list.size() : (i + 1) * batchSize;
            tasks.add(() -> findSumInRange(list.subList(startIndex, endIndex)));
        }
        // Запускаем все задачи и получаем Future объекты для получения результатов
        List<Future<Integer>> futures = executorService.invokeAll(tasks);
        // Инициализируем переменную для хранения суммы
        int sum = 0;
        // Обходим результаты каждой задачи и находим сумму
        for (Future<Integer> future : futures) {
            int partialSum = future.get();
            Thread.sleep(1);
            sum += partialSum;
        }
        // Завершаем пул потоков
        executorService.shutdown();
        // Возвращаем сумму из всех подсписков
        return sum;
    }
    //Zverev
    // Функция для поиска суммы в подсписке
    private static int findSumInRange(List<Integer> sublist) throws
            InterruptedException {
        int sum = 0;
        for (int number : sublist) {
            Thread.sleep(1);
            sum += number;
        }
        return sum;
    }
    //Zverev
    public static int findSumFork(List<Integer> list) {
        // Проверяем, что список не пуст и не равен null
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Список пуст или равен null");
        }
        // Создаем пул потоков ForkJoin
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        // Создаем корневую задачу SumFinderTask для всего списка
        SumFinderTask task = new SumFinderTask(list, 0, list.size());
        // Выполняем корневую задачу и получаем результат
        return forkJoinPool.invoke(task);
    }
    //Zverev
    // Внутренний класс SumFinderTask, расширяющий RecursiveTask для многопоточного выполнения
    static class SumFinderTask extends RecursiveTask<Integer> {
        private List<Integer> list;
        private int start;
        private int end;
        //Zverev
        // Конструктор SumFinderTask для создания задачи для подсписка
        SumFinderTask(List<Integer> list, int start, int end) {
            this.list = list;
            this.start = start;
            this.end = end;
        }
        //Zverev
        // Метод compute(), выполняющий вычисления для задачи
        @Override
        protected Integer compute() {
            // Если в подсписке не более 1000 элементов, завершаем разбиение
            if (end - start <= 1000) {
                try {
                    return findSumInRange(list.subList(start, end));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // Найдем середину подсписка
            int middle = start + (end - start) / 2;
            // Создаем две подзадачи для левой и правой половин подсписка
            SumFinderTask leftTask = new SumFinderTask(list, start, middle);
            SumFinderTask rightTask = new SumFinderTask(list, middle, end);
            // Запускаем подзадачу для левой половины параллельно
            leftTask.fork();
            // Вычисляем сумму в левой и правой половинах подсписка
            int rightResult = rightTask.compute();
            int leftResult = leftTask.join();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Возвращаем сумму из левой и правой половин
            return leftResult + rightResult;
        }
        //Zverev
        public static void main(String[] args) throws InterruptedException,
                ExecutionException {
            List<Integer> testList = generateArray10000();

            long startTime = System.nanoTime();
            int result = findSum(testList);
            long endTime = System.nanoTime();
            long durationInMilliseconds = (endTime - startTime) / 1_000_000;
            Runtime runtime = Runtime.getRuntime();
            long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Использование памяти: " + memoryUsed / (1024 * 1024)
                    + " МБ");
            System.out.println("Время выполнения последовательной функции: " +
                    durationInMilliseconds + " миллисекунд. Результат - " + result);

            startTime = System.nanoTime();
            result = findSumMnogopotok(testList);
            endTime = System.nanoTime();
            durationInMilliseconds = (endTime - startTime) / 1_000_000;
            runtime = Runtime.getRuntime();
            memoryUsed = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Использование памяти: " + memoryUsed / (1024 * 1024)
                    + " МБ");
            System.out.println("Время выполнения многопоточной функции: " +
                    durationInMilliseconds + " миллисекунд. Результат - " + result);

            startTime = System.nanoTime();
            result = findSumFork(testList);
            endTime = System.nanoTime();
            durationInMilliseconds = (endTime - startTime) / 1_000_000;
            runtime = Runtime.getRuntime();
            memoryUsed = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Использование памяти: " + memoryUsed / (1024 * 1024)
                    + " МБ");
            System.out.println("Время выполнения форк функции: " +
                    durationInMilliseconds + " миллисекунд. Результат - " + result);
            System.out.println("Автор: " + "Зверев А.А. ИКБО-20-21");
        }
    }
}
