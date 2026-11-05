package com.javapractice.config;

import com.javapractice.entity.Chapter;
import com.javapractice.entity.KnowledgePoint;
import com.javapractice.entity.Module;
import com.javapractice.repository.ChapterRepository;
import com.javapractice.repository.KnowledgePointRepository;
import com.javapractice.repository.ModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将知识点讲解内容统一同步为 Markdown 格式（按章节名 + 知识点标题幂等覆盖）。
 */
@Component
@Order(3)
public class KnowledgePointMarkdownSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(KnowledgePointMarkdownSeeder.class);

    private final ModuleRepository moduleRepository;
    private final ChapterRepository chapterRepository;
    private final KnowledgePointRepository knowledgePointRepository;

    public KnowledgePointMarkdownSeeder(ModuleRepository moduleRepository,
                                        ChapterRepository chapterRepository,
                                        KnowledgePointRepository knowledgePointRepository) {
        this.moduleRepository = moduleRepository;
        this.chapterRepository = chapterRepository;
        this.knowledgePointRepository = knowledgePointRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Module se = moduleRepository.findByCode("JAVA_SE").orElse(null);
        if (se == null) {
            return;
        }
        Map<String, Long> chapterIds = new HashMap<>();
        List<Chapter> chapters = chapterRepository.findByModuleIdOrderBySortOrderAsc(se.getId());
        for (Chapter chapter : chapters) {
            chapterIds.put(chapter.getName(), chapter.getId());
        }

        update(chapterIds, "Java 基础语法", "基本数据类型与包装类", """
                Java 有 8 种基本类型：

                - `byte`：1 字节，范围 -128 ~ 127
                - `short`：2 字节，范围 -32768 ~ 32767
                - `int`：4 字节，最常用
                - `long`：8 字节，表示大整数
                - `float`：4 字节，单精度浮点
                - `double`：8 字节，双精度浮点
                - `char`：2 字节，Unicode 字符
                - `boolean`：只有 `true` / `false`

                每种基本类型都有对应的包装类（如 `int` -> `Integer`），用于集合等泛型场景。

                自动装箱 / 拆箱：

                - 装箱：`int` 自动转 `Integer`，底层调用 `Integer.valueOf(100)`
                - 拆箱：`Integer` 自动转 `int`，底层调用 `intValue()`

                > 注意：`Integer` 对 -128 ~ 127 有缓存，范围内 `==` 比较为 `true`；超出范围会新建对象，应使用 `equals()`。
                """);

        update(chapterIds, "Java 基础语法", "字符串与不可变性", """
                `String` 是 `final` 类，不可被继承，其内容一经创建不可改变。

                - 任何拼接、替换操作都会产生新的 `String` 对象，原对象不变
                - 不可变性有利于字符串常量池复用和线程安全

                频繁拼接时：

                | 类 | 是否可变 | 线程安全 |
                | --- | --- | --- |
                | `String` | 不可变 | 安全 |
                | `StringBuilder` | 可变 | 不安全（快） |
                | `StringBuffer` | 可变 | 安全（慢） |

                > JDK 9 起，`String` 底层从 `char[]` 改为 `byte[]`，以节省内存。
                """);

        update(chapterIds, "面向对象", "封装、继承与多态", """
                封装：把字段私有化，通过公共的 getter / setter 访问，隐藏内部实现。

                继承：子类 `extends` 父类，复用父类能力；Java 类只支持单继承。

                多态：父类引用指向子类对象时，调用重写方法按运行时实际类型绑定（动态分派）。

                ```java
                Animal a = new Dog();
                a.speak(); // 调用 Dog 重写的 speak()
                ```
                """);

        update(chapterIds, "面向对象", "抽象类与接口", """
                抽象类：

                - 用 `abstract` 修饰，不能被实例化
                - 可以有构造器、成员变量、抽象方法和普通方法

                接口：

                - 字段默认 `public static final`
                - 方法默认 `public abstract`
                - Java 8 起可以有 `default` / `static` 方法
                - 一个类可以实现多个接口

                > 抽象类强调“是什么”，接口强调“能做什么”。
                """);

        update(chapterIds, "集合框架", "ArrayList 与 LinkedList", """
                `ArrayList`：

                - 底层是数组
                - 随机访问快（O(1)）
                - 中间插入 / 删除慢

                `LinkedList`：

                - 底层是双向链表
                - 中间插入 / 删除快（O(1)）
                - 随机访问慢（`get(i)` 需从头遍历）

                > 两者都不是线程安全的。
                """);

        update(chapterIds, "集合框架", "HashMap 原理", """
                `HashMap` 底层是数组 + 链表 + 红黑树。

                - 通过 key 的 `hashCode` 计算桶位置
                - 哈希冲突时用链表；JDK 8 起，链表长度 >= 8 且数组长度 >= 64 时转为红黑树
                - 默认初始容量 16，负载因子 0.75
                - 相同 key 再次 `put` 会覆盖旧值
                - 允许一个 `null` key
                """);

        update(chapterIds, "泛型", "泛型基础与类型擦除", """
                泛型在编译期做类型检查，避免强转和 `ClassCastException`。

                ```java
                List<String> list = new ArrayList<>();
                list.add("a");
                String s = list.get(0); // 无需强转
                ```

                类型擦除：编译后泛型类型信息会被擦除，运行时 `List<String>` 与 `List<Integer>` 是同一个类。

                > 泛型不能使用基本类型，必须用包装类（`List<Integer>` 而不是 `List<int>`）。
                """);

        update(chapterIds, "泛型", "泛型通配符", """
                `?` 表示未知类型。

                - `? extends T`（上界）：只能读不能写
                - `? super T`（下界）：可以写 `T` 及其子类

                ```java
                List<? extends Number> nums = new ArrayList<Integer>(); // 读 OK
                List<? super Integer> ints = new ArrayList<Number>();   // 写 OK
                ```
                """);

        update(chapterIds, "反射", "反射机制与 Class 对象", """
                反射允许程序在运行时获取类信息、创建对象、调用方法。

                获取 `Class` 对象：

                - `类名.class`
                - `对象.getClass()`
                - `Class.forName("全限定名")`

                ```java
                Class<?> clazz = Class.forName("java.lang.String");
                Object obj = clazz.getDeclaredConstructor().newInstance();
                ```

                典型应用：Spring 的 `@Autowired` 注入、MyBatis 结果集映射。
                """);

        update(chapterIds, "反射", "反射常用 API", """
                - `getDeclaredMethods()`：获取所有方法
                - `getDeclaredConstructor()`：获取构造器
                - `Method.invoke()`：调用方法
                - 访问私有成员需先 `setAccessible(true)`

                > 反射调用比直接调用慢；JDK 动态代理就是基于接口 + 反射实现。
                """);

        update(chapterIds, "多线程", "线程的创建与生命周期", """
                创建线程：

                - 继承 `Thread` 重写 `run()`
                - 实现 `Runnable` / `Callable`，交给 `Thread` 执行

                `start()` 启动线程进入就绪态，由调度器决定执行时机；同一个线程只能 `start()` 一次。

                线程状态：`NEW` -> `RUNNABLE` -> `BLOCKED` / `WAITING` / `TIMED_WAITING` -> `TERMINATED`。
                """);

        update(chapterIds, "多线程", "线程安全与同步", """
                `synchronized`：保证互斥与可见性。

                - 实例方法锁 `this`
                - 静态方法锁类的 `Class` 对象

                `volatile`：保证可见性、禁止指令重排，但不保证原子性。

                死锁：多线程互相等待对方持有的锁，形成循环等待。

                > `wait()` 会释放锁，`sleep()` 不会释放锁。
                """);

        update(chapterIds, "IO 流", "字节流与字符流", """
                字节流：

                - `InputStream` / `OutputStream`
                - 处理任意二进制（图片、视频、文件）

                字符流：

                - `Reader` / `Writer`
                - 面向文本，自动处理编码

                ```java
                try (BufferedReader br = new BufferedReader(new FileReader("a.txt"))) {
                    br.lines().forEach(System.out::println);
                }
                ```

                `try-with-resources` 会自动关闭流。
                """);

        update(chapterIds, "IO 流", "序列化与对象流", """
                对象序列化需实现 `Serializable` 接口。

                - `ObjectOutputStream`：写入对象
                - `ObjectInputStream`：读取对象
                - `transient` 修饰的字段不会被序列化
                - `serialVersionUID` 用于版本控制，保证反序列化兼容性
                """);

        update(chapterIds, "网络编程", "Socket 编程基础", """
                TCP：面向连接、可靠。

                - 服务端：`ServerSocket` 监听端口
                - 客户端：`Socket` 发起连接

                UDP：无连接、不可靠。

                - 使用 `DatagramSocket` / `DatagramPacket` 收发数据报

                > BIO 是一个连接一个线程；NIO 通过 `Selector` 实现多路复用。
                """);

        update(chapterIds, "网络编程", "HTTP 基础", """
                HTTP 是无状态协议。

                常用方法：

                - `GET`：查询
                - `POST`：提交数据

                常见状态码：

                - `200` 成功
                - `3xx` 重定向
                - `404` 资源未找到
                - `500` 服务器内部错误
                """);

        update(chapterIds, "Java 8 新特性", "Lambda 与函数式接口", """
                Lambda 表达式简化匿名类写法：

                ```java
                Runnable r = () -> System.out.println("hi");
                ```

                函数式接口：只有一个抽象方法的接口，用 `@FunctionalInterface` 标注。

                常用函数式接口：

                - `Function<T,R>`：转换
                - `Consumer<T>`：消费
                - `Supplier<T>`：提供
                - `Predicate<T>`：判断
                """);

        update(chapterIds, "Java 8 新特性", "Stream API", """
                `Stream` 用于对集合做链式操作。

                - `filter`：过滤
                - `map`：转换
                - `collect`：收集

                ```java
                List<Integer> result = list.stream()
                        .filter(x -> x > 0)
                        .collect(Collectors.toList());
                ```

                > 中间操作（filter / map）是惰性的，遇到终端操作（collect / forEach）才执行。
                """);

        log.info("知识点 Markdown 内容已同步（共 {} 个章节）", chapters.size());
    }

    private void update(Map<String, Long> chapterIds, String chapterName, String kpTitle, String markdown) {
        Long chapterId = chapterIds.get(chapterName);
        if (chapterId == null) {
            return;
        }
        knowledgePointRepository.findByChapterIdOrderBySortOrderAsc(chapterId).stream()
                .filter(kp -> kp.getTitle().equals(kpTitle))
                .findFirst()
                .ifPresent(kp -> {
                    kp.setContent(markdown);
                    knowledgePointRepository.save(kp);
                });
    }
}
