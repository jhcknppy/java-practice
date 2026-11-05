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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从《Java 基础面试题速记通关》中抽取的、能对应到现有章节的面试题。
 * 按“章节 + 题目标题”幂等插入，不会重复。
 */
@Component
@Order(2)
public class InterviewQuestionSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InterviewQuestionSeeder.class);

    private final ModuleRepository moduleRepository;
    private final ChapterRepository chapterRepository;
    private final KnowledgePointRepository knowledgePointRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;

    private Map<String, Long> chapterIds = new HashMap<>();
    private Map<Long, Map<String, Long>> kpIds = new HashMap<>();

    public InterviewQuestionSeeder(ModuleRepository moduleRepository,
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
        Module se = moduleRepository.findByCode("JAVA_SE").orElse(null);
        if (se == null) {
            return;
        }
        List<Chapter> chapters = chapterRepository.findByModuleIdOrderBySortOrderAsc(se.getId());
        for (Chapter chapter : chapters) {
            chapterIds.put(chapter.getName(), chapter.getId());
            Map<String, Long> kps = new HashMap<>();
            knowledgePointRepository.findByChapterIdOrderBySortOrderAsc(chapter.getId())
                    .forEach(kp -> kps.put(kp.getTitle(), kp.getId()));
            kpIds.put(chapter.getId(), kps);
        }

        // Java 基础语法
        choice("Java 基础语法", "基本数据类型与包装类", "Java 中占用 2 个字节的基本类型是？", "B",
                "byte 占 1 字节，short 占 2 字节，int/float 占 4 字节。", new String[]{"byte", "short", "int", "float"});
        choice("Java 基础语法", "基本数据类型与包装类", "Integer i = 100; 编译后等价于？", "B",
                "自动装箱在编译后调用 Integer.valueOf(100)。", new String[]{"new Integer(100)", "Integer.valueOf(100)", "(Integer) 100", "i.intValue()"});
        choice("Java 基础语法", "基本数据类型与包装类", "Integer 缓存池的默认范围是？", "A",
                "Integer 对 -128~127 之间的值有缓存，范围内装箱复用同一对象。", new String[]{"-128~127", "0~255", "-127~128", "全部整数"});
        choice("Java 基础语法", "字符串与不可变性", "线程安全且内容可变的字符串类是？", "C",
                "String 不可变；StringBuilder 可变但线程不安全；StringBuffer 可变且线程安全。", new String[]{"String", "StringBuilder", "StringBuffer", "char[]"});
        choice("Java 基础语法", "字符串与不可变性", "JDK 9 将 String 底层从 char[] 改为 byte[] 的主要目的是？", "A",
                "大多数字符串以拉丁字符为主，byte 足够表示，可节省约一半内存。", new String[]{"节省内存", "提升拼接速度", "支持更大字符集", "提升线程安全"});
        trueFalse("Java 基础语法", "字符串与不可变性", "String 不可变的特性有利于字符串常量池的复用和线程安全。", "true",
                "不可变使字符串可安全共享到常量池，也天然线程安全。");

        // 面向对象
        trueFalse("面向对象", "封装、继承与多态", "子类可以继承父类的 private 成员。", "false",
                "private 成员只在父类内部可见，子类无法直接访问，更不能继承。");
        choice("面向对象", "封装、继承与多态", "Java 不支持多重继承的主要原因是？", "A",
                "多重继承会带来菱形继承的二义性问题。", new String[]{"菱形继承二义性", "性能差", "语法复杂", "无法实现"});
        choice("面向对象", "封装、继承与多态", "同一个包内可访问、跨包不可访问的访问修饰符是？", "B",
                "默认（不写修饰符）是包级访问；private 仅类内，protected 跨包子类可见，public 全部可见。", new String[]{"private", "默认（不写修饰符）", "protected", "public"});
        choice("面向对象", "封装、继承与多态", "静态方法不能直接访问的是？", "C",
                "静态方法没有 this，不能直接访问实例变量和实例方法。", new String[]{"静态变量", "静态方法", "实例变量 / this", "类名"});
        choice("面向对象", "封装、继承与多态", "方法重载的区分依据是？", "A",
                "重载依据参数列表（个数、类型、顺序）不同；仅返回值不同不能构成重载。", new String[]{"参数列表", "返回值类型", "方法名", "访问修饰符"});
        choice("面向对象", "封装、继承与多态", "final 修饰一个类，表示？", "A",
                "final 修饰类表示该类不能被继承。", new String[]{"不能被继承", "不能被实例化", "方法不能重写", "变量不能修改"});
        choice("面向对象", "抽象类与接口", "关于抽象类，正确的是？", "A",
                "抽象类可以有构造器（供子类调用）、成员变量和普通方法，但不能实例化。", new String[]{"抽象类可以有构造器", "抽象类不能有成员变量", "抽象类不能有普通方法", "抽象类可以被实例化"});

        // 集合框架
        choice("集合框架", "ArrayList 与 LinkedList", "遍历 LinkedList 时，增强 for 比传统 for(get(i)) 更高效，原因是？", "A",
                "LinkedList 的 get(i) 每次都要从头遍历 O(n)，传统 for 会退化为 O(n²)。", new String[]{"get(i) 每次都从头遍历", "增强 for 跳过索引", "传统 for 会并发修改", "两者效率一样"});
        trueFalse("集合框架", "ArrayList 与 LinkedList", "ArrayList 实现 RandomAccess 接口，标识其支持快速随机访问。", "true",
                "RandomAccess 是标记接口，表明该 List 支持 O(1) 随机访问。");
        choice("集合框架", "HashMap 原理", "关于 hashCode 和 equals，正确的是？", "A",
                "equals 相等的两个对象，hashCode 必须相等；反之不一定成立。", new String[]{"equals 相等则 hashCode 必相等", "hashCode 相等则 equals 必相等", "equals 相等 hashCode 可以不同", "二者没有关系"});
        choice("集合框架", "HashMap 原理", "== 比较引用类型时，比较的是什么？", "A",
                "== 比较引用类型时比较对象的内存地址；比较内容应用 equals。", new String[]{"内存地址", "对象内容", "hashCode", "类名"});

        // 泛型
        choice("泛型", "泛型基础与类型擦除", "Java 泛型的类型擦除发生在哪个阶段？", "A",
                "类型擦除发生在编译期，编译后泛型信息被擦除。", new String[]{"编译期", "运行期", "类加载期", "JIT 编译期"});
        choice("泛型", "泛型通配符", "关于泛型通配符，正确的是？", "A",
                "? extends T 是上界通配符，只能读不能写；? super T 是下界通配符，可以写。", new String[]{"? extends T 只能读不能写", "? super T 只能读不能写", "? 可以向任意类型写入", "通配符不能作为参数类型"});

        // 反射
        trueFalse("反射", "反射机制与 Class 对象", "Spring 的 @Autowired 依赖注入底层主要使用反射实现。", "true",
                "Spring 通过反射读取注解、获取构造器和字段完成依赖注入。");
        choice("反射", "反射常用 API", "JDK 动态代理基于什么实现？", "A",
                "JDK 动态代理基于接口 + 反射（java.lang.reflect.Proxy）；CGLIB 基于继承生成子类。", new String[]{"接口 + 反射", "继承 + 字节码", "静态代理", "注解"});
        trueFalse("反射", "反射常用 API", "Java 注解本身不产生行为，需要配合反射解析才会生效。", "true",
                "注解是元数据，框架通过反射读取注解并处理。");

        // 多线程
        choice("多线程", "线程的创建与生命周期", "同一个 Thread 对象第二次调用 start() 会？", "B",
                "线程只能从 NEW 状态启动一次，再次 start 抛 IllegalThreadStateException。", new String[]{"再次执行 run", "抛 IllegalThreadStateException", "被忽略", "抛 InterruptedException"});
        choice("多线程", "线程安全与同步", "关于 wait() 和 sleep()，正确的是？", "A",
                "wait 会释放锁并进入等待，sleep 不释放锁。", new String[]{"wait 释放锁，sleep 不释放锁", "sleep 释放锁，wait 不释放锁", "都释放锁", "都不释放锁"});

        // IO 流
        choice("IO 流", "字节流与字符流", "关于字节流与字符流，正确的是？", "A",
                "字节流处理二进制数据，字符流面向文本并处理编码。", new String[]{"字节流处理二进制，字符流处理文本", "字符流处理二进制", "两者完全一样", "字节流只能处理文本"});
        choice("IO 流", "序列化与对象流", "serialVersionUID 的主要作用是？", "A",
                "serialVersionUID 用于序列化版本控制，保证反序列化时的兼容性。", new String[]{"序列化版本控制保证兼容性", "加密数据", "压缩数据", "标识类名"});

        // 网络编程
        choice("网络编程", "Socket 编程基础", "关于 BIO 与 NIO，正确的是？", "A",
                "BIO 是一个连接一个线程，NIO 通过 Selector 实现多路复用。", new String[]{"BIO 一连接一线程，NIO 多路复用", "NIO 一连接一线程", "BIO 多路复用", "两者相同"});
        trueFalse("网络编程", "Socket 编程基础", "UDP 是面向连接、可靠的传输协议。", "false",
                "UDP 是无连接、不可靠的传输协议，TCP 才面向连接、可靠。");
        choice("网络编程", "HTTP 基础", "HTTP 状态码 500 表示？", "A",
                "500 表示服务器内部错误；404 是资源未找到。", new String[]{"服务器内部错误", "资源未找到", "重定向", "请求成功"});

        // Java 8 新特性
        choice("Java 8 新特性", "Lambda 与函数式接口", "Lambda 表达式主要用于简化？", "A",
                "Lambda 用于简化函数式接口的匿名实现。", new String[]{"函数式接口的匿名实现", "抽象类的继承", "普通类的实例化", "数组遍历"});
        choice("Java 8 新特性", "Stream API", "下面哪个是 Stream 的中间操作？", "C",
                "filter 是中间操作（惰性）；collect/forEach/count 是终端操作。", new String[]{"collect", "forEach", "filter", "count"});

        log.info("面试题已同步：当前题库共 {} 道题", questionRepository.count());
    }

    private void choice(String chapterName, String kpTitle, String title, String answer, String analysis, String[] options) {
        addQuestion(chapterName, kpTitle, Question.TYPE_CHOICE, title, answer, analysis, options);
    }

    private void trueFalse(String chapterName, String kpTitle, String title, String answer, String analysis) {
        addQuestion(chapterName, kpTitle, Question.TYPE_TRUE_FALSE, title, answer, analysis, null);
    }

    private void addQuestion(String chapterName, String kpTitle, String type, String title, String answer, String analysis, String[] options) {
        Long chapterId = chapterIds.get(chapterName);
        if (chapterId == null) {
            return;
        }
        Long kpId = kpIds.getOrDefault(chapterId, Map.of()).get(kpTitle);
        if (kpId == null) {
            return;
        }
        if (questionRepository.existsByChapterIdAndTitle(chapterId, title)) {
            return;
        }
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
