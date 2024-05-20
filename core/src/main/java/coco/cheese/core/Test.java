package coco.cheese.core;

public class Test {
    // 定义一个函数式接口
   public interface MyMethod {
        void execute();
    }
    private String name;
    private int age;
    private Runnable  myMethod;

    public Test(String name, int age, Runnable  myMethod) {
        this.name = name;
        this.age = age;
        this.myMethod = myMethod;
    }

    public void triggerMethod() {
        System.out.println("触发，执行了 myMethod");
        // 调用传递进来的方法
        myMethod.run();
    }

    // 一个包含方法参数的类

//
//    // 在其他类中使用
//    public class Main {
//        public static void main(String[] args) {
//            // 使用Lambda表达式创建一个函数式接口的实例
//            MyMethod method = () -> System.out.println("执行了方法的具体实现");
//
//            // 创建一个 Person 对象，并传递方法的具体实现
//            Person person1 = new Person("Alice", 30, method);
//
//            // 调用触发方法
//            person1.triggerMethod();
//        }
//    }

}
