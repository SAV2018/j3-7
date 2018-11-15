package hw3_7;


import org.junit.After;

class TestClass {

    @BeforeSuit
    void beforeMethod() {
        // echo(new Throwable().getStackTrace()[0].getMethodName());
        echo("beforeMethod");
    }

//  (!) для тестирования исключения при двух BeforeSuit
//    @BeforeSuit
//    void beforeMethod_1() {
//        echo("beforeMethod_1");
//    }

    @Test(priority = 2)
    void method_1() {
        echo("method_1");
    }

    @Test
    void method_2() {
        echo("method_2");
    }

    @Test
    void method_3() {
        echo("method_3");
    }

    @Test(priority = 5)
    void method_4() {
        echo("method_4");
    }

    @After  // (!) для проверки чтения аннотаций
    @Test(priority = 2)
    void method_5() {
        echo("method_5");
    }

    @AfterSuit
    void afterMethod() {
        echo("afterMethod");
    }

    @Test(priority = 10)
    void method_6() {
        echo("method_6");
    }

    void noAnnotationMethod() {
        echo("noAnnotationMethod");
    }

    private void echo(String name) {
        System.out.println(name + " run.");
    }
}
