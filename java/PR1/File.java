package PR1;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
//Zverev
public class File {
    private String fileType;
    private int fileSize;
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
class FileGenerator implements Runnable {
    private BlockingQueue<File> queue;
    public FileGenerator(BlockingQueue<File> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        Random random = new Random();
        String[] fileTypes = {"XML", "JSON", "XLS"};
        while (true) {
            try {
                Thread.sleep(random.nextInt(901) + 100); // Задержка от 100 до 1000 мс
                String randomFileType =
                        fileTypes[random.nextInt(fileTypes.length)];
                int randomFileSize = random.nextInt(91) + 10; // Размер файла от 10 до 100
                File file = new File(randomFileType, randomFileSize);
                queue.put(file); // Добавляем файл в очередь
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
//Zverev
class FileProcessor implements Runnable {
    private BlockingQueue<File> queue;
    private String allowedFileType;
    public FileProcessor(BlockingQueue<File> queue, String allowedFileType) {
        this.queue = queue;
        this.allowedFileType = allowedFileType;
    }
    //Zverev
    @Override
    public void run() {
        while (true) {
            try {
                File file = queue.take(); // Получаем файл из очереди
                if (file.getFileType().equals(allowedFileType)) {
                    long processingTime = file.getFileSize() * 7; // Время обработки
                    Thread.sleep(processingTime);
                    System.out.println("Обработан файл типа " +
                            file.getFileType() +
                            " с размером " + file.getFileSize() + ". Время обработки: " +
                            processingTime + " мс.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
