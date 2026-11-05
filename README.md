# Java 练习系统（Java SE 阶段）

面向 Java 初级开发者的闯关式练习系统。当前实现了 Java SE 模块：知识点讲解 → 章节练习 → 错题复盘 → 加强练习 → 开发等级成长。

## 技术栈

- 后端：Java 21、Spring Boot 3.5.11、Spring Data JPA、MySQL、JJWT
- 前端：React 18、Ant Design Pro、umi 4（@umijs/max）、TypeScript
- 数据库：MySQL（库名 `java_practice`，root / `Jhcknppy!`）

## 目录结构

```
java-practice/
├── backend/    Spring Boot 后端
└── frontend/   Ant Design Pro + umi 前端
```

## 启动

后端（端口 8082）：

```bash
cd backend
/Users/Zhuanz/dev/apache-maven-3.9.12/bin/mvn spring-boot:run
```

前端（端口 8001）：

```bash
cd frontend
npm install
npm run dev
```

浏览器访问 http://localhost:8001，首次使用先注册账号。

## 已实现功能

- 模块 → 章节 → 知识点 → 题目 四层内容结构。
- 三种题型：选择题、判断题、代码理解题（代码阅读，自动判分）。
- 章节顺序解锁，答完并达到正确率（默认 80%）即可通关。
- 答题即时判分并给出解析；答错进入错题本。
- 章节复盘：按知识点展示掌握度，薄弱知识点给出原理讲解。
- 加强练习：针对薄弱知识点重新组卷。
- 开发等级：新用户 0 级，每完成 3 个章节升 1 级。

## 当前内容

Java SE 模块包含 9 个章节、54 道题：

1. Java 基础语法（基本类型与包装类、字符串与不可变性）
2. 面向对象（封装继承多态、抽象类与接口）
3. 集合框架（ArrayList 与 LinkedList、HashMap 原理）
4. 泛型（泛型基础与类型擦除、通配符）
5. 反射（反射机制与 Class 对象、常用 API）
6. 多线程（线程创建与生命周期、线程安全与同步）
7. IO 流（字节流与字符流、序列化）
8. 网络编程（Socket 基础、HTTP 基础）
9. Java 8 新特性（Lambda 与函数式接口、Stream API）

种子数据采用“幂等增量”方式：启动时按章节名判断，只补不存在的章节；面试题按“章节 + 题目标题”去重，不会重复插入。

此外，已从《Java 基础面试题速记通关》PDF 中抽取能对应到现有章节的面试题，转成选择题/判断题并入题库，当前题库共 85 道题。

## 主要接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/register` | 注册 |
| POST | `/api/auth/login` | 登录 |
| GET | `/api/auth/me` | 当前用户（含等级） |
| GET | `/api/modules` | 模块与章节（含进度、解锁状态） |
| GET | `/api/chapters/{id}/knowledge-points` | 章节知识点讲解 |
| GET | `/api/chapters/{id}/questions` | 章节题目（不含答案） |
| POST | `/api/answers` | 提交答案并判分 |
| GET | `/api/wrong-questions` | 错题本 |
| GET | `/api/chapters/{id}/review` | 章节复盘（知识点掌握度） |
| GET | `/api/chapters/{id}/reinforcements` | 薄弱知识点加强练习 |
| POST | `/api/chapters/{id}/settle` | 章节结算与等级更新 |
