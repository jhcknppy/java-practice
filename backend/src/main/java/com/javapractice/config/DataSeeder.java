package com.javapractice.config;

import com.javapractice.entity.Chapter;
import com.javapractice.entity.KnowledgePoint;
import com.javapractice.entity.Module;
import com.javapractice.entity.Question;
import com.javapractice.entity.QuestionOption;
import com.javapractice.repository.ChapterRepository;
import com.javapractice.repository.KnowledgePointRepository;
import com.javapractice.repository.ModuleRepository;
import com.javapractice.repository.QuestionOptionRepository;
import com.javapractice.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ModuleRepository moduleRepository;
    private final ChapterRepository chapterRepository;
    private final KnowledgePointRepository knowledgePointRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;

    public DataSeeder(ModuleRepository moduleRepository,
                      ChapterRepository chapterRepository,
                      KnowledgePointRepository knowledgePointRepository,
                      QuestionRepository questionRepository,
                      QuestionOptionRepository optionRepository) {
        this.moduleRepository = moduleRepository;
        this.chapterRepository = chapterRepository;
        this.knowledgePointRepository = knowledgePointRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Module se = moduleRepository.findByCode("JAVA_SE")
                .orElseGet(() -> seedModule("JAVA_SE", "Java SE", "Java 语言基础，是学习后续所有技术栈的基石。"));
        Set<String> existing = new HashSet<>();
        chapterRepository.findByModuleIdOrderBySortOrderAsc(se.getId()).forEach(c -> existing.add(c.getName()));

        Chapter c1 = ensureChapter(se.getId(), "Java 基础语法", "基本类型、包装类、字符串与不可变性。", 1, existing);
        if (c1 != null) {
            Long kp11 = seedKp(c1.getId(), "基本数据类型与包装类",
                    "Java 有 8 种基本类型：byte/short/int/long/float/double/char/boolean。\n每种类型都有对应包装类（如 int -> Integer）。\n自动装箱/拆箱由编译器完成；Integer 对 -128~127 有缓存，范围内 == 比较为 true。", 1);
            Long kp12 = seedKp(c1.getId(), "字符串与不可变性",
                    "String 是 final 类，不可被继承，内容一经创建不可改变。\n拼接/替换都会产生新对象；频繁拼接请用 StringBuilder（快，线程不安全）或 StringBuffer（线程安全）。", 2);
            seedChoice(c1.getId(), kp11, "下列选项中，哪个属于 Java 的基本数据类型？", "C", "boolean 是 8 种基本类型之一；String 是引用类型，Integer/Double 是包装类。", new String[]{"String", "Integer", "boolean", "Double"});
            seedCode(c1.getId(), kp11, "下面代码输出是什么？\nInteger a = 127;\nInteger b = 127;\nSystem.out.println(a == b);", "A", "Integer 对 -128~127 有缓存，127 命中缓存，a 与 b 指向同一对象，所以 == 为 true。", new String[]{"true", "false", "编译错误", "运行时异常"});
            seedTrueFalse(c1.getId(), kp11, "Java 中 int 类型占 4 个字节，取值范围约为 -2^31 ~ 2^31-1。", "true", "int 是 32 位有符号整数。");
            seedChoice(c1.getId(), kp12, "String 类被哪个关键字修饰，因此不能被继承？", "B", "String 被 final 修饰，是 final 类。", new String[]{"static", "final", "abstract", "synchronized"});
            seedCode(c1.getId(), kp12, "下面代码执行后，变量 s 的值是什么？\nString s = \"a\";\ns = s + \"b\";", "C", "拼接产生新对象 \"ab\" 并重新赋值；原 \"a\" 未改变。", new String[]{"a", "b", "ab", "编译错误"});
            seedTrueFalse(c1.getId(), kp12, "String 是可变字符串，修改后原对象内容会被改变。", "false", "String 不可变，拼接/替换会产生新对象。");
        }

        Chapter c2 = ensureChapter(se.getId(), "面向对象", "封装、继承、多态，以及抽象类与接口。", 2, existing);
        if (c2 != null) {
            Long kp21 = seedKp(c2.getId(), "封装、继承与多态", "封装：私有化字段，通过 getter/setter 访问。\n继承：子类 extends 父类，类只支持单继承。\n多态：父类引用指向子类对象，调用重写方法时按运行时实际类型绑定。", 1);
            Long kp22 = seedKp(c2.getId(), "抽象类与接口", "抽象类：abstract 修饰，可有构造器、成员变量、抽象/普通方法，不能实例化。\n接口：字段默认 public static final，方法默认 public abstract；Java 8 起可有 default/static 方法；类可多实现接口。", 2);
            seedChoice(c2.getId(), kp21, "下列哪个不是面向对象的三大特性？", "D", "三大特性是封装、继承、多态；重载是多态的一种表现形式，但不是三大特性之一。", new String[]{"封装", "继承", "多态", "重载"});
            seedCode(c2.getId(), kp21, "已知 Dog 继承 Animal 并重写了 speak()，下面代码调用的是哪个方法？\nAnimal a = new Dog();\na.speak();", "B", "多态：运行时按实际对象类型绑定，调用 Dog 的 speak()。", new String[]{"Animal 的 speak", "Dog 的 speak", "编译错误", "运行时异常"});
            seedTrueFalse(c2.getId(), kp21, "Java 中一个类可以同时继承多个父类。", "false", "Java 类只支持单继承；接口可多实现。");
            seedChoice(c2.getId(), kp22, "关于接口的说法，正确的是？", "C", "Java 8 起接口可有 default 方法；接口无构造器，字段默认 public static final。", new String[]{"接口可以有构造方法", "接口可以有实例字段", "Java 8 起接口可以有 default 方法", "一个类只能实现一个接口"});
            seedTrueFalse(c2.getId(), kp22, "抽象类不能有构造方法。", "false", "抽象类可以有构造方法，供子类构造时调用。");
            seedCode(c2.getId(), kp22, "类 A 声明为 class A implements I，接口 I 定义 void f();。要编译通过，类 A 必须？", "A", "实现接口必须实现其抽象方法 f()，除非 A 自身也是抽象类。", new String[]{"实现 f 方法", "继承接口 I", "声明为 final", "什么都不用做"});
        }

        Chapter c3 = ensureChapter(se.getId(), "集合框架", "List 的实现差异与 HashMap 底层原理。", 3, existing);
        if (c3 != null) {
            Long kp31 = seedKp(c3.getId(), "ArrayList 与 LinkedList", "ArrayList 底层是数组，随机访问 O(1)，中间增删慢。\nLinkedList 底层是双向链表，中间增删 O(1)，随机访问慢。\n两者都不是线程安全的。", 1);
            Long kp32 = seedKp(c3.getId(), "HashMap 原理", "HashMap 底层是数组 + 链表 + 红黑树。\n通过 key 的 hashCode 定位桶；链表长度 >= 8 且数组 >= 64 时转红黑树。\n默认容量 16、负载因子 0.75；相同 key 覆盖；允许一个 null key。", 2);
            seedChoice(c3.getId(), kp31, "ArrayList 底层使用的数据结构是？", "B", "ArrayList 基于数组实现，支持 O(1) 随机访问。", new String[]{"链表", "数组", "哈希表", "红黑树"});
            seedChoice(c3.getId(), kp31, "需要频繁在集合中间插入和删除元素，以下哪个更合适？", "B", "LinkedList 是双向链表，中间增删只需调整指针。", new String[]{"ArrayList", "LinkedList", "HashMap", "HashSet"});
            seedTrueFalse(c3.getId(), kp31, "ArrayList 是线程安全的。", "false", "ArrayList 非线程安全；可用 CopyOnWriteArrayList 或 Collections.synchronizedList。");
            seedChoice(c3.getId(), kp32, "HashMap 的默认初始容量是？", "B", "HashMap 默认初始容量 16，负载因子 0.75。", new String[]{"8", "16", "32", "64"});
            seedTrueFalse(c3.getId(), kp32, "HashMap 允许 null 作为 key。", "true", "HashMap 允许一个 null key；Hashtable 不允许。");
            seedCode(c3.getId(), kp32, "下面代码输出是什么？\nMap<String,Integer> map = new HashMap<>();\nmap.put(\"a\", 1);\nmap.put(\"a\", 2);\nSystem.out.println(map.get(\"a\"));", "B", "相同 key 再次 put 会覆盖旧值。", new String[]{"1", "2", "null", "抛出异常"});
        }

        Chapter c4 = ensureChapter(se.getId(), "泛型", "泛型基础、类型擦除与通配符。", 4, existing);
        if (c4 != null) {
            Long kp41 = seedKp(c4.getId(), "泛型基础与类型擦除", "泛型在编译期做类型检查，避免强转和 ClassCastException。\n泛型通过类型擦除实现：编译后泛型类型信息会被擦除。\n泛型不能使用基本类型，必须用包装类。", 1);
            Long kp42 = seedKp(c4.getId(), "泛型通配符", "? 表示未知类型。\n? extends T（上界）：只能读不能写。\n? super T（下界）：可以写 T 及其子类。", 2);
            seedChoice(c4.getId(), kp41, "关于 Java 泛型，说法正确的是？", "B", "泛型在编译期检查类型，减少运行期 ClassCastException；类型擦除后运行时信息丢失。", new String[]{"泛型可以用基本类型", "泛型通过编译期检查减少类型转换错误", "运行时可以通过对象获取泛型具体类型", "List<int> 是合法的"});
            seedCode(c4.getId(), kp41, "下面代码编译报错的原因是什么？\nList<String> list = new ArrayList<>();\nlist.add(123);", "A", "add 参数必须是 String，123 是 int，编译期类型检查不通过。", new String[]{"类型不匹配，编译期检查", "运行时异常", "代码不会报错", "数组越界"});
            seedTrueFalse(c4.getId(), kp41, "Java 泛型通过类型擦除实现，编译后泛型类型信息会被擦除。", "true", "泛型信息主要存在于编译期，运行时会擦除。");
            seedChoice(c4.getId(), kp42, "泛型 `? super Integer` 表示？", "B", "? super T 是下界通配符，表示 Integer 或其父类。", new String[]{"只能存 Integer", "Integer 或其父类", "Integer 或其子类", "任意类型"});
            seedTrueFalse(c4.getId(), kp42, "List<? extends Number> 可以安全地向其中添加元素。", "false", "上界通配符只能读不能写，因为不知道具体子类型。");
            seedCode(c4.getId(), kp42, "下面哪个声明是合法的？", "A", "List<? extends Number> 可接收 List<Integer>；泛型不支持基本类型，List<String> 不能赋给 List<Object>。", new String[]{"List<? extends Number> l = new ArrayList<Integer>();", "List<int> l = new ArrayList<>();", "List<String> l = new ArrayList<Object>();", "List<String> l = new ArrayList<Integer>();"});
        }

        Chapter c5 = ensureChapter(se.getId(), "反射", "反射机制、Class 对象与常用 API。", 5, existing);
        if (c5 != null) {
            Long kp51 = seedKp(c5.getId(), "反射机制与 Class 对象", "反射允许程序在运行时获取类的信息、创建对象、调用方法。\n获取 Class 对象：类名.class、对象.getClass()、Class.forName(\"全限定名\")。", 1);
            Long kp52 = seedKp(c5.getId(), "反射常用 API", "getDeclaredMethods 获取所有方法；getDeclaredConstructor 获取构造器；Method.invoke 调用方法。\n访问私有成员需先 setAccessible(true)。反射调用比直接调用慢。", 2);
            seedChoice(c5.getId(), kp51, "获取 Class 对象的方式，错误的是？", "D", "Foo.getClass() 不合法，getClass() 是实例方法；正确方式是 Foo.class、obj.getClass()、Class.forName()。", new String[]{"Foo.class", "new Foo().getClass()", "Class.forName(\"Foo\")", "Foo.getClass()"});
            seedTrueFalse(c5.getId(), kp51, "通过反射可以在运行时获取并修改类的私有字段。", "true", "配合 setAccessible(true) 可以访问私有成员。");
            seedCode(c5.getId(), kp51, "Class.forName(\"java.lang.String\") 的作用是？", "B", "Class.forName 加载类并返回其 Class 对象，不创建实例。", new String[]{"创建 String 实例", "加载并获取 String 的 Class 对象", "调用 String 的构造器", "获取 String 的字节码文件"});
            seedChoice(c5.getId(), kp52, "通过反射调用某个 Method 对象的方法，需要使用？", "A", "Method 对象用 invoke() 执行。", new String[]{"invoke()", "call()", "run()", "apply()"});
            seedTrueFalse(c5.getId(), kp52, "反射调用方法通常比直接调用方法效率低。", "true", "反射需要动态解析，存在额外开销。");
            seedCode(c5.getId(), kp52, "使用反射获取并调用私有构造器前，需要先？", "A", "获取私有构造器后需 setAccessible(true) 才能访问。", new String[]{"setAccessible(true)", "newInstance", "不需要任何操作", "无法获取私有构造器"});
        }

        Chapter c6 = ensureChapter(se.getId(), "多线程", "线程创建、生命周期、同步与线程安全。", 6, existing);
        if (c6 != null) {
            Long kp61 = seedKp(c6.getId(), "线程的创建与生命周期", "创建线程：继承 Thread 重写 run，或实现 Runnable/Callable 后交给 Thread。\nstart() 启动线程进入就绪态，由调度器决定执行时机；状态包括 NEW/RUNNABLE/BLOCKED/WAITING/TIMED_WAITING/TERMINATED。", 1);
            Long kp62 = seedKp(c6.getId(), "线程安全与同步", "synchronized 保证互斥与可见性；volatile 保证可见性并禁止指令重排，但不保证原子性。\n静态 synchronized 方法锁 Class 对象，实例方法锁 this。死锁：多线程互相等待对方持有的锁。", 2);
            seedChoice(c6.getId(), kp61, "创建线程的正确方式是？", "C", "继承 Thread 重写 run，或实现 Runnable 交给 Thread，两种都可以。", new String[]{"继承 Thread 并重写 run", "实现 Runnable 并交给 Thread", "两种都可以", "两种都不行"});
            seedTrueFalse(c6.getId(), kp61, "调用线程的 start() 方法会立即执行 run() 方法。", "false", "start() 只是让线程进入就绪态，何时执行由调度器决定。");
            seedCode(c6.getId(), kp61, "下面代码中 main 与 run 的输出顺序？\nThread t = new Thread(() -> System.out.println(\"run\"));\nt.start();\nSystem.out.println(\"main\");", "C", "线程调度不确定，main 和 run 的输出顺序不可预知。", new String[]{"一定先 main 后 run", "一定先 run 后 main", "顺序不确定", "编译错误"});
            seedChoice(c6.getId(), kp62, "下列哪个关键字用于保证可见性并禁止指令重排？", "B", "volatile 保证可见性、禁止指令重排，但不保证原子性。", new String[]{"synchronized", "volatile", "static", "final"});
            seedTrueFalse(c6.getId(), kp62, "synchronized 修饰静态方法时，锁的是类的 Class 对象。", "true", "静态方法属于类，锁对象是 Class 对象。");
            seedCode(c6.getId(), kp62, "两个线程各自持有对方需要的锁并互相等待，这种现象叫？", "A", "互相等待对方持有的锁形成死锁。", new String[]{"死锁", "活锁", "饥饿", "竞态条件"});
        }

        Chapter c7 = ensureChapter(se.getId(), "IO 流", "字节流、字符流与对象序列化。", 7, existing);
        if (c7 != null) {
            Long kp71 = seedKp(c7.getId(), "字节流与字符流", "字节流 InputStream/OutputStream 处理任意二进制；字符流 Reader/Writer 处理文本。\nBufferedReader 提供缓冲读取；try-with-resources 可自动关闭流。", 1);
            Long kp72 = seedKp(c7.getId(), "序列化与对象流", "对象序列化需实现 Serializable 接口。\nObjectOutputStream 写入对象，ObjectInputStream 读取对象。\ntransient 修饰的字段不会被序列化。", 2);
            seedChoice(c7.getId(), kp71, "用于读取文本文件的常用流是？", "B", "BufferedReader/FileReader 是字符流，适合读文本。", new String[]{"FileInputStream", "BufferedReader/FileReader", "ObjectOutputStream", "DataOutputStream"});
            seedTrueFalse(c7.getId(), kp71, "字节流可以处理任意类型文件，字符流更适合处理文本。", "true", "字节流按字节读写通用，字符流面向文本并处理编码。");
            seedCode(c7.getId(), kp71, "下面写法的好处是什么？\ntry (FileInputStream in = new FileInputStream(\"a.txt\")) { ... }", "A", "try-with-resources 会在结束时自动关闭流。", new String[]{"自动关闭流", "加快读取速度", "提供缓冲", "编码转换"});
            seedChoice(c7.getId(), kp72, "对象要实现序列化，需要实现哪个接口？", "B", "实现 Serializable 标记接口即可序列化。", new String[]{"Cloneable", "Serializable", "Comparable", "Runnable"});
            seedTrueFalse(c7.getId(), kp72, "被 transient 修饰的字段在序列化时会被忽略。", "true", "transient 字段不参与序列化。");
            seedCode(c7.getId(), kp72, "使用 ObjectInputStream 读取对象时，可能抛出的受检异常是？", "A", "反序列化可能抛 IOException 和 ClassNotFoundException。", new String[]{"IOException / ClassNotFoundException", "NullPointerException", "ArithmeticException", "不抛异常"});
        }

        Chapter c8 = ensureChapter(se.getId(), "网络编程", "Socket 编程与 HTTP 基础。", 8, existing);
        if (c8 != null) {
            Long kp81 = seedKp(c8.getId(), "Socket 编程基础", "TCP 面向连接、可靠；服务端用 ServerSocket 监听，客户端用 Socket 连接。\nUDP 无连接，用 DatagramSocket/DatagramPacket 收发数据报。", 1);
            Long kp82 = seedKp(c8.getId(), "HTTP 基础", "HTTP 是无状态协议。\n常用方法：GET（查询）、POST（提交数据）。\n状态码：200 成功、404 未找到、500 服务器错误、3xx 重定向。", 2);
            seedChoice(c8.getId(), kp81, "Java 中实现 TCP 服务端监听，使用哪个类？", "B", "ServerSocket 用于 TCP 服务端监听。", new String[]{"Socket", "ServerSocket", "DatagramSocket", "URLConnection"});
            seedTrueFalse(c8.getId(), kp81, "TCP 是面向连接、可靠的传输协议。", "true", "TCP 通过三次握手建立连接，保证可靠传输。");
            seedCode(c8.getId(), kp81, "使用 UDP 通信时，主要使用哪个类收发数据报？", "C", "UDP 使用 DatagramSocket/DatagramPacket。", new String[]{"Socket", "ServerSocket", "DatagramSocket", "HttpURLConnection"});
            seedChoice(c8.getId(), kp82, "HTTP 状态码 404 表示？", "B", "404 Not Found 表示资源未找到。", new String[]{"服务器内部错误", "资源未找到", "重定向", "请求成功"});
            seedTrueFalse(c8.getId(), kp82, "HTTP 是无状态协议。", "true", "HTTP 本身不保存请求之间的状态，需靠 Cookie/Session 等机制。");
            seedCode(c8.getId(), kp82, "向服务器提交表单数据，通常使用哪种请求方法？", "B", "POST 用于提交数据，GET 用于查询。", new String[]{"GET", "POST", "DELETE", "OPTIONS"});
        }

        Chapter c9 = ensureChapter(se.getId(), "Java 8 新特性", "Lambda、函数式接口与 Stream API。", 9, existing);
        if (c9 != null) {
            Long kp91 = seedKp(c9.getId(), "Lambda 与函数式接口", "Lambda 表达式简化匿名类写法：(参数) -> { 方法体 }。\n函数式接口：只有一个抽象方法的接口，用 @FunctionalInterface 标注。\n常用函数式接口：Function、Consumer、Supplier、Predicate。", 1);
            Long kp92 = seedKp(c9.getId(), "Stream API", "Stream 用于对集合做链式操作：filter 过滤、map 转换、collect 收集。\n中间操作（filter/map）是惰性的，遇到终端操作（collect/forEach）才执行。", 2);
            seedChoice(c9.getId(), kp91, "下列哪个注解用于标记函数式接口？", "B", "@FunctionalInterface 标记只有一个抽象方法的接口。", new String[]{"@Override", "@FunctionalInterface", "@Deprecated", "@SuppressWarnings"});
            seedTrueFalse(c9.getId(), kp91, "函数式接口只能有一个抽象方法。", "true", "函数式接口的抽象方法数量必须为 1（default/static 方法不算）。");
            seedCode(c9.getId(), kp91, "Runnable r = () -> System.out.println(\"hi\"); 这种写法是？", "A", "() -> ... 是 Lambda 表达式，实现函数式接口 Runnable。", new String[]{"Lambda 表达式", "匿名内部类", "方法引用", "泛型"});
            seedChoice(c9.getId(), kp92, "下面哪个是 Stream 的终端操作？", "C", "collect 是终端操作；filter/map/distinct 是中间操作。", new String[]{"filter", "map", "collect", "distinct"});
            seedTrueFalse(c9.getId(), kp92, "Stream 的中间操作是惰性的，只有遇到终端操作才执行。", "true", "中间操作只构建流水线，终端操作触发执行。");
            seedCode(c9.getId(), kp92, "list.stream().filter(x -> x > 0).collect(Collectors.toList()) 的作用是？", "A", "过滤出大于 0 的元素并收集为新的 List。", new String[]{"过滤出大于 0 的元素组成新 List", "排序", "去重", "求和"});
        }

        log.info("Java SE 模块已就绪：{} 个章节、{} 道题目",
                chapterRepository.findByModuleIdOrderBySortOrderAsc(se.getId()).size(), questionRepository.count());
    }

    private Chapter ensureChapter(Long moduleId, String name, String description, int sortOrder, Set<String> existing) {
        if (existing.contains(name)) {
            return null;
        }
        Chapter chapter = new Chapter();
        chapter.setModuleId(moduleId);
        chapter.setName(name);
        chapter.setDescription(description);
        chapter.setSortOrder(sortOrder);
        chapter.setPassRate(0.8);
        existing.add(name);
        return chapterRepository.save(chapter);
    }

    private Module seedModule(String code, String name, String description) {
        Module module = new Module();
        module.setCode(code);
        module.setName(name);
        module.setDescription(description);
        module.setSortOrder(1);
        return moduleRepository.save(module);
    }

    private Long seedKp(Long chapterId, String title, String content, int sortOrder) {
        KnowledgePoint kp = new KnowledgePoint();
        kp.setChapterId(chapterId);
        kp.setTitle(title);
        kp.setContent(content);
        kp.setSortOrder(sortOrder);
        return knowledgePointRepository.save(kp).getId();
    }

    private void seedChoice(Long chapterId, Long kpId, String title, String answer, String analysis, String[] options) {
        seedQuestion(chapterId, kpId, Question.TYPE_CHOICE, title, answer, analysis, options);
    }

    private void seedCode(Long chapterId, Long kpId, String title, String answer, String analysis, String[] options) {
        seedQuestion(chapterId, kpId, Question.TYPE_CODE, title, answer, analysis, options);
    }

    private void seedTrueFalse(Long chapterId, Long kpId, String title, String answer, String analysis) {
        seedQuestion(chapterId, kpId, Question.TYPE_TRUE_FALSE, title, answer, analysis, null);
    }

    private void seedQuestion(Long chapterId, Long kpId, String type, String title, String answer, String analysis, String[] options) {
        Question question = new Question();
        question.setChapterId(chapterId);
        question.setKnowledgePointId(kpId);
        question.setType(type);
        question.setTitle(title);
        question.setAnswer(answer);
        question.setAnalysis(analysis);
        question.setDifficulty(1);
        questionRepository.save(question);
        if (options != null) {
            String[] labels = {"A", "B", "C", "D", "E"};
            for (int i = 0; i < options.length; i++) {
                QuestionOption option = new QuestionOption();
                option.setQuestionId(question.getId());
                option.setLabel(labels[i]);
                option.setContent(options[i]);
                option.setSortOrder(i);
                optionRepository.save(option);
            }
        }
    }
}
