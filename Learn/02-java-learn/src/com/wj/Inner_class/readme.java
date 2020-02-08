package com.wj.Inner_class;

public class readme {
	// 编译时常量：编译是能确定值的常量（不需要函数调用）
	// 常量需要final修饰
	public static final int a1 = 10; 			// 编译时常量
	public static final int a2 = 10 * 20 + 3; 	// 编译时常量(简单计算，不调用方法)
	public static final int a3 = a3(); 			// 非编译时常量(调用方法)
	static int a3() {return 0;}
	
	/// 嵌套类
	// 严格来说，只有动态嵌套类才属于内部类
	// 使用嵌套类场景
	// 1.当一个类A只用在C内部时，可以考虑使用嵌套类
	// 2.如果类A经常访问类C的非公开成员时，可以考虑使用嵌套类
	// 优点：封装性、可读性和维护性；程序包更加简化；隐藏类A不对外暴露
	
	// 通常设计为静态内部类satic class，只有A经常需要访问类C非公共的实例成员或方法时才设计为动态内部类
	
	
	/// 局部类
	// 定义在代码块中（方法、if、for等中的类），实际上他们都只存在于方法中，其它的还有初始化块(构造方法前)和静态初始化块(类初始化)中
	// 局部类中不能定义除编译时常量以外的静态成员（成员变量或成员方法）
	// 局部类只能访问final或者有效final的局部变量（有效final：只赋值一次，不会再修改，可以理解为捕获，不能捕获会被修改的局部变量）
	// 定义在实例方法中，才能直接访问外部类的实例成员（外部类的类成员可没有限制的访问）

	/// 匿名类
	// 格式： new 接口 { 类的定义 }
	// 匿名类只能定义编译时常量static final
	// 只能捕获final常量或者有效final常量
	// 特性类似局部类
	
	/****************************************
	 * 注意：lamda没有引入新的作用域,与匿名类有区别
	 ****************************************/
	// 即lamda内部，使用this指针，指代的是所处方法的实例
	// 而匿名类内部使用this，指代的是当前匿名类实例
	// lamda底层虽然通过匿名类实现，但是在lamda表达式内使用this成员不会指代lamda实例，仍然使用外部实例
	
}
