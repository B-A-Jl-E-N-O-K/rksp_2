package PR3;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import java.util.Random;
// Создаем класс для датчика температуры
class TemperatureSensor extends Observable<Integer> {
    //Zverev
    private final PublishSubject<Integer> subject = PublishSubject.create(); //Создаем датчик температуры
    @Override
    protected void subscribeActual(Observer<? super Integer> observer) //Любой тип, родительский для int
    { //Переопределяем абстрактный класс подписки - бизнес-логика подключения
        subject.subscribe(observer); // Создаем подписку на события датчика температуры
    }
    //Zverev
    public void start() {
        new Thread(() -> { // В отдельном потоке
            while (true) {
                int temperature = new Random().nextInt(15, 31); // Генерируем случайное значение температуры
                subject.onNext(temperature); // Отправляем значение температуры подписчикам
                try {
                    Thread.sleep(1000); // Пауза 1 секунда
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(); // Запускаем поток для симуляции работы датчика
    }
}
//Zverev
// Создаем класс для датчика CO2
class CO2Sensor extends Observable<Integer> {
    private final PublishSubject<Integer> subject = PublishSubject.create();
    @Override
    protected void subscribeActual(Observer<? super Integer> observer) {
        subject.subscribe(observer); // Создаем подписку на события датчика CO2
    }
    public void start() {
        new Thread(() -> {
            while (true) {
                int co2 = new Random().nextInt(30, 101); // Генерируем случайное значение CO2
                subject.onNext(co2); // Отправляем значение CO2 подписчикам
                try {
                    Thread.sleep(1000); // Пауза 1 секунда
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(); // Запускаем поток для симуляции работы датчика
    }
}
//Zverev
// Создаем класс для сигнализации
class Alarm implements Observer<Integer> {
    private final int CO2_LIMIT = 70;
    private final int TEMP_LIMIT = 25;
    private int temperature = 0;
    private int co2 = 0;
    @Override
    public void onSubscribe(Disposable d) { //На вход переменная управления подпиской
        System.out.println(d.hashCode() + " has subscribed");
    }
    @Override
    public void onNext(Integer value) {
        System.out.println("Next value from Observable = " + value);
        if (value <= 30){ //На основании неперекрытия возможных значений источников
            temperature = value;
        } else {
            co2 = value;
        }
        checkingSystem();
    }
    //Zverev
    public void checkingSystem(){
        if (temperature >= TEMP_LIMIT && co2 >= CO2_LIMIT){
            System.out.println("ALARM!!! Temperature/CO2: " + temperature + "/"
                    + co2);
        } else if (temperature >= TEMP_LIMIT){
            System.out.println("Temperature warning: " + temperature);
        } else if (co2 >= CO2_LIMIT){
            System.out.println("CO2 warning: " + co2);
        }
    }
    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }
    @Override
    public void onComplete() {
        System.out.println("Completed");
    }
}
//Zverev
class Main {
    public static void main(String[] args) {
        System.out.println("Автор: " + "Зверев А.А. ИКБО-20-21");
        TemperatureSensor temperatureSensor = new TemperatureSensor(); // Создаем датчик температуры
        CO2Sensor co2Sensor = new CO2Sensor(); // Создаем датчик CO2
        Alarm alarm = new Alarm(); // Создаем сигнализацию
        temperatureSensor.subscribe(alarm); // Подписываем сигнализацию на датчик температуры
        co2Sensor.subscribe(alarm); // Подписываем сигнализацию на датчик CO2
        temperatureSensor.start(); // Запускаем датчик температуры
        co2Sensor.start(); // Запускаем датчик CO2
    }
}

