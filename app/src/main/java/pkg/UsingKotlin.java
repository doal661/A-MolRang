package pkg;

import pkg.logic.Basic;

public class UsingKotlin {
	public static void main(String[] args) {
		String message = AppKt.greet("Java and Kotlin");
		System.out.println("H");
		System.out.println(message);

		Basic b = new pkg.logic.Basic();
		b.print();
		b.value();
		b.value();
		b.print();
	}
}
