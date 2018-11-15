package hw3_7;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {
    private static int beforeAnnotations = 0;  // кол-во аннотаций BeforeSuite
    private static int afterAnnotations = 0;   // кол-во аннотаций AfterSuite
    private static Method beforeMethod = null, afterMethod = null;
    private static Class testClass;
    private static Method[] methods;
    private static ArrayList<Method> testMethods;

    public static void main(String[] args) {
//  Создать класс, который может выполнять «тесты», в качестве тестов выступают классы с наборами методов
//  с аннотациями @Test. Для этого у него должен быть статический метод start(), которому в качестве параметра
//  передается или объект типа Class или имя класса.
//  Из «класса-теста» вначале должен быть запущен метод с аннотацией @BeforeSuite, если такой имеется,
//  далее запущены методы с аннотациями @Test, а по завершению всех тестов – метод с аннотацией @AfterSuite.
//  К каждому тесту необходимо также добавить приоритеты (int числа от 1 до 10), в соответствии с которыми
//  будет выбираться порядок их выполнения, если приоритет одинаковый, то порядок не имеет значения.
//  Методы с аннотациями @BeforeSuite и @AfterSuite должны присутствовать в единственном экземпляре,
//  иначе необходимо бросить RuntimeException при запуске «тестирования».

        start(new TestClass());
    }

    private static void start(Object object) {
        testClass = object.getClass();
        System.out.println(testClass.getName() + " " + testClass.getSimpleName() + " " + testClass.getPackageName());

        try {
            methods = testClass.getDeclaredMethods();
            if (methods.length == 0) throw new RuntimeException("No methods found!");

            testMethods = new ArrayList<>(methods.length);

            printAllMethods();

            sortTestMethods(); // сортировка методов тестирования

            // вывод отсортированных методов
            printSortedMethods();

            // запуск тестовых методов
            runTestMethods(object);
        } catch (Exception e) {
            System.out.println("\n" + e.getMessage());
        }
    }

    static private void printAllMethods() {
        System.out.println("\nAll methods in class " + testClass.getName() + ": ");

        for (Method m : methods) {
            String s = "";

            Annotation[] annotations = m.getDeclaredAnnotations();
            if (annotations.length > 0) {
                Test annotation = m.getAnnotation(Test.class);
                if (annotation != null) {
                    s = " | " + annotation.priority();
                    testMethods.add(m);
                }

                if (m.getAnnotation(BeforeSuit.class) != null) {
                    beforeMethod = m;
                    beforeAnnotations++;
                }

                if (m.getAnnotation(AfterSuit.class) != null) {
                    afterMethod = m;
                    afterAnnotations++;
                }
            }

            // выводим информацию о методе (возр.тип, название, аннотации, приоритет)
            System.out.println(m.getReturnType() + " | " + m.getName() +
                    " | " + Arrays.toString(m.getParameterTypes()) +
                    " | " + Arrays.toString(annotations) + s);
        }

        if (beforeAnnotations > 1) throw new RuntimeException("Should be only one method with BeforeSuit Annotation!");
        if (afterAnnotations > 1) throw new RuntimeException("Should be only one method with AfterSuit Annotation!");
    }

    static private void runTestMethods(Object object) {
        // запуск тестовых методов
        System.out.println("\nRun tests methods: ");
        for (Method m : methods) {

            if (!Modifier.isPrivate(m.getModifiers())) {
                // вызываем метод
                try {
                    m.invoke(object);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static private void printSortedMethods() {
        // вывод отсортированных методов
        System.out.println("\nTest methods sorted by priority: ");

        methods = new Method[testMethods.size()];
        for (int i = 0; i < testMethods.size(); i++) {
            methods[i] = testMethods.get(i);
            System.out.println(methods[i].getReturnType() + " | " + methods[i].getName() +
                    " | " + Arrays.toString(methods[i].getAnnotations()));
        }
    }

    static private void sortTestMethods() {
        if (testMethods.size() > 1) {
            // сортировка методов c аннотацией Test
            for (int i = 0; i < testMethods.size() - 1; i++) {
                for (int j = i + 1; j < testMethods.size(); j++) {

                    if (testMethods.get(i).getAnnotation(Test.class).priority() <
                            testMethods.get(j).getAnnotation(Test.class).priority()) {
                        Method m = testMethods.get(i);
                        testMethods.set(i, testMethods.get(j));
                        testMethods.set(j, m);
                    }
                }
            }
        }

        if (beforeMethod != null) testMethods.add(0, beforeMethod);
        if (afterMethod != null) testMethods.add(afterMethod);
    }
}