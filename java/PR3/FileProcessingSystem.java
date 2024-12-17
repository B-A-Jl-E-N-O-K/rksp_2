package PR3;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

//Zverev
// Класс File представляет файлы с типом и размером
class File {
    private final String fileType;
    private final int fileSize;
    public File(String fileType, int fileSize) {
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
    public String getFileType() {
        return fileType;
    }
    public int getFileSize() {
        return fileSize;
    }
}
//Zverev
// Генератор файлов
class FileGenerator {
    // Генерирует файлы асинхронно с задержкой
    public Observable<File> generateFile() {
        return Observable
                .fromCallable(() -> { //Из функции c возвратом значения
                    try {
                        String[] fileTypes = {"XML", "JSON", "XLS"};
                        String fileType = fileTypes[(int) (Math.random() * 3)];
                        int fileSize = (int) (Math.random() * 91) + 10;
                        Thread.sleep((long) (Math.random() * 901) + 100); // Имитация генерации файла
                        return new File(fileType, fileSize);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .repeat() // Повторяем бесконечно
                .subscribeOn(Schedulers.io()) // Выполняется в фоновом потоке
                .observeOn(Schedulers.io()); // Результаты наблюдаются в фоновом потоке
    }
}
//Zverev
// Очередь файлов
class FileQueue {
    private final int capacity;
    private final Observable<File> fileObservable;
    // Создает очередь с заданной вместимостью и подключается к генератору файлов
    public FileQueue(int capacity) {
        this.capacity = capacity;
        this.fileObservable = new FileGenerator().generateFile()
                .replay(capacity) // При подключении обработчика выдает набор последних файлов
                .autoConnect(); // Включается автоматически при подписке обработчика
    }
    // Получает наблюдаемый поток файлов
    public Observable<File> getFileObservable() {
        return fileObservable;
    }
}
//Zverev
// Обработчик файлов
class FileProcessor {
    private final String supportedFileType;
    // Создает обработчик файлов для определенного типа файлов
    public FileProcessor(String supportedFileType) {
        this.supportedFileType = supportedFileType;
    }
    // Обрабатывает файлы асинхронно с задержкой
    public Completable processFiles(Observable<File> fileObservable) {
        return fileObservable
                .filter(file -> file.getFileType().equals(supportedFileType)) // Фильтрует файлы по типу
                    .flatMapCompletable(file -> { // Преобразует в Completable
            long processingTime = file.getFileSize() * 7; // Вычисляет время обработки
            return Completable
                    .fromAction(() -> {
                        Thread.sleep(processingTime); // Имитация обработки файла
                        System.out.println("Processed " +
                                supportedFileType + " file with size " + file.getFileSize());
                    }).subscribeOn(Schedulers.io()) // Выполняется в фоновом потоке
                        .observeOn(Schedulers.io()); // Результаты наблюдаются в фоновом потоке
                }).onErrorComplete(); // Игнорирует ошибки и завершает успешно
    }
}
//Zverev
// Основной класс системы обработки файлов
public class FileProcessingSystem {
    public static void main(String[] args) {
        System.out.println("Автор: " + "Зверев А.А. ИКБО-20-21");
        int queueCapacity = 5;
        FileQueue fileQueue = new FileQueue(queueCapacity);
        String[] supportedFileTypes = {"XML", "JSON", "XLS"};
        for (String fileType : supportedFileTypes) {
            new FileProcessor(fileType)
                    .processFiles(fileQueue.getFileObservable()) // Подключаем обработчик
                    .subscribe(() -> {}, // Обработка успешного завершения (OnNext)
                            throwable -> System.err.println("Error processing file: " + throwable)); //OnError
        }
        // Даем системе время для работы
        try {
            Thread.sleep(10000); // Работает 10 секунд
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
