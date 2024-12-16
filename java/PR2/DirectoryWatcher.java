package PR2;

import java.io.*;
import java.nio.file.*;
import java.security.DigestInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class DirectoryWatcher {
    //Zverev
    private static Map<Path, List<String>> fileContentsMap = new HashMap<>();
    private static Map<Path, String> fileHashes = new HashMap<>();
    //Zverev
    public static void main(String[] args) throws IOException,
            InterruptedException {
        System.out.println("Автор: " + "Зверев А.А. ИКБО-20-21");
        Path directory = Paths.get("./pr4_4");
        WatchService watchService = FileSystems.getDefault().newWatchService(); //Класс отслеживания на дефолтной файловой системе
        directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE); // Регистрация кактолога с указанием типов операций
        firstObserve(directory); // Первичная проверка полсе начала работы (для выдачи ответа при первом изменении)
        while (true) {
            WatchKey key = watchService.take();// Ключ, сигнализирующий о событии
            for (WatchEvent<?> event : key.pollEvents()) { // Ждем ивенты
                WatchEvent.Kind<?> kind = event.kind(); // Тип ивента
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    Path filePath = (Path) event.context();
                    System.out.println("Создан новый файл: " + filePath);
                    fileContentsMap.put(filePath,
                            readLinesFromFile(directory.resolve(filePath))); //В хешмапе ключ - файл, значение - список строк (содержание)
                    calculateFileHash(directory.resolve(filePath)); //Считаем хеш, чтобы при удалении вывести
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    Path filePath = (Path) event.context();
                    System.out.println("Файл изменен: " + filePath);
                    detectFileChanges(directory.resolve(filePath)); //resolve объединяет пути (относительный файла + текущий)
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    Path filePath = (Path) event.context();
                    System.out.println("Удален файл: " + filePath);
                    String hash = fileHashes.get(directory.resolve(filePath));
                    if (hash != null) {
                        System.out.println("Хеш-сумма удаленного файла: " + hash);
                    }
                }
            }
            key.reset(); //Сброс сохраненных событий
        }

    }
    //Zverev
    private static void firstObserve(Path directory) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) //Перебор каталога по файлам
        {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    fileContentsMap.put(filePath, readLinesFromFile(filePath));
                    calculateFileHash(filePath);
                }
            }
        }
    }
    //Zverev
    private static void detectFileChanges(Path filePath) throws IOException {
        List<String> newFileContents = readLinesFromFile(filePath);
        List<String> oldFileContents = fileContentsMap.get(filePath);
        if (oldFileContents != null) {
            List<String> addedLines = newFileContents.stream()
                    .filter(line -> !oldFileContents.contains(line))
                    .toList(); //Обработка через stream для операции над каждой строкой
            List<String> deletedLines = oldFileContents.stream()
                    .filter(line -> !newFileContents.contains(line))
                    .toList();
            if (!addedLines.isEmpty()) {
                System.out.println("Добавленные строки в файле " + filePath +
                        ":");
                addedLines.forEach(line -> System.out.println("+ " + line));
            }
            if (!deletedLines.isEmpty()) {
                System.out.println("Удаленные строки из файла " + filePath +
                        ":");
                deletedLines.forEach(line -> System.out.println("- " + line));
            }
        }
        calculateFileHash(filePath);
        fileContentsMap.put(filePath, newFileContents);
    }
    //Zverev
    private static List<String> readLinesFromFile(Path filePath) throws
            IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
    //Zverev
    private static void calculateFileHash(Path filePath) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); //Алгоритм хеширования для вычисления контрольной суммы
            try (InputStream is = Files.newInputStream(filePath);
                 DigestInputStream dis = new DigestInputStream(is, md)) {
                while (dis.read() != -1) ;
                String hash = bytesToHex(md.digest()); //Вычисляем хеш и преобразуем в 16-ое число
                fileHashes.put(filePath, hash);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) { //Если не нашел MD5
            e.printStackTrace();
        }
    }
    //Zverev
    private static String bytesToHex(byte[] bytes) {
        String hashStr = "";
        for (byte b : bytes) {
            hashStr += String.format("%02x", b);
        }
        return hashStr;
    }
}

