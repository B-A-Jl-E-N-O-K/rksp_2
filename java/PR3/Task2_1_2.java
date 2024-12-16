package PR3;

import io.reactivex.Observable;
import java.util.Random;

//Zverev
public class Task2_1_2 { //Номер в списке 10, Вариант 2 (10 % 3 + 1 = 2)

    public static void main(String[] args) {

        Random random = new Random();
        System.out.println("Автор: " + "Зверев А.А. ИКБО-20-21");
        System.out.println("Номер в списке: 10; " + "Вариант: 2");

        //Задача 2.1.2
        Observable<Integer> source = Observable.range(0, 1000) // 1000 значений в потоке
                .map(i -> random.nextInt(1001)) // Генерируем случайные числа от 0 до 1000
                .filter(number -> number > 500); // Применяем оператор filter для фильтрации чисел больше 500

        source.subscribe(num -> System.out.println("Число > 500: " + num));

    }
}
//Zverev
class Task2_2_2 {

    public static void main(String[] args) {

        Random random = new Random();
        System.out.println("Автор: " + "Зверев А.А. ИКБО-20-21");
        System.out.println("Номер в списке: 10; " + "Вариант: 2");

        //Задача 2.2.2
        Observable<Integer> stream1 = Observable.range(0, 1000)
                .map(i -> random.nextInt(101)); //Малые числа для 1 потока
        Observable<Integer> stream2 = Observable.range(0, 1000)
                .map(i -> random.nextInt(901, 1001)); //Большие числа для 2 потока
        Observable<Integer> mergedStream = Observable.concat(stream1, stream2); //Объединяем оба потока последовательно

        mergedStream.subscribe(num -> System.out.println("Объединенный поток: " + num));

    }
}
//Zverev
class Task2_3_2 {

    public static void main(String[] args) {

        Random random = new Random();
        System.out.println("Автор: " + "Зверев А.А. ИКБО-20-21");
        System.out.println("Номер в списке: 10; " + "Вариант: 2");

        //Задача 2.3.2
        Observable<Integer> source = Observable.range(0, 10); //10 последовательных чисел в потоке
        Observable<Integer> result = source.take(5); // Берем только первые пять элементов


        result.subscribe(num -> System.out.println("Первые 5 чисел: " + num));

    }
}

