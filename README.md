# 2025-4-23
> Ide 없이 코틀린 코드를 자바에서 호출하려면 어떻게 해야 해?

Kotlin 코드를 Java에서 호출하려면, IDE 없이도 명령줄에서 컴파일하고 실행할 수 있습니다. 전체적인 과정은 Kotlin 코드를 바이트코드로 컴파일하여 클래스 파일(또는 jar)을 생성한 다음, Java 코드에서 해당 클래스와 메서드를 호출하는 것입니다. 다음은 그 과정을 단계별로 설명한 내용입니다.

## 1. Kotlin 코드 작성 및 컴파일
Kotlin 파일(MyKotlin.kt 등)을 작성합니다. 예를 들어, 다음과 같은 간단한 Kotlin 함수를 작성했다고 가정해 봅니다.
```kotlin
// MyKotlin.kt
package my.kotlin.pkg

fun greet(name: String): String {
    return "Hello, $name!"
}

```

Kotlin 컴파일러(`kotlinc`)를 사용하여 이 파일을 컴파일합니다. 만약 실행 파일(jar)이 필요하다면 `-include-runtime` 옵션을 사용할 수 있지만, Java와 함께 사용할 경우 Kotlin 표준 라이브러리만 클래스패스에 추가해도 충분합니다.

```Bash
kotlinc MyKotlin.kt -d MyKotlinClasses
```

이 명령은 `MyKotlinClasses` 디렉터리에 클래스 파일들을 생성합니다. 또는 jar 파일로 만들고 싶다면:

```Bash
kotlinc MyKotlin.kt -include-runtime -d MyKotlin.jar
```

## 2. Java 코드에서 Kotlin 코드 호출하기
Kotlin의 최상위 함수는 컴파일 과정에서 파일 이름에 `Kt`가 붙은 클래스에 정적 메서드로 생성됩니다. 위 예제의 경우, `MyKotlin.kt`에서 정의된 `greet` 함수는 `MyKotlinKt`라는 클래스의 정적 메서드가 됩니다.

예를 들어, Java에서는 다음과 같이 호출할 수 있습니다.

```java
// MyJava.java
package my.java.pkg;

public class MyJava {
    public static void main(String[] args) {
        // 패키지가 있다면 풀 패키지 이름을 사용합니다.
        String message = my.kotlin.pkg.MyKotlinKt.greet("World");
        System.out.println(message);
    }
}
```

## 3. java 코드 컴파일
Java 코드를 컴파일할 때는 Kotlin에서 생성된 클래스 파일들이 있는 디렉터리(또는 jar 파일)와 Kotlin 표준 라이브러리(kotlin-stdlib.jar)를 클래스패스에 포함시켜야 합니다.

### 클래스 파일을 디렉터리로 관리하는 경우:
```Bash
javac -cp MyKotlinClasses:<path_to_kotlin_stdlib>/kotlin-stdlib.jar MyJava.java
```

### jar 파일을 사용하는 경우
```Bash
javac -cp MyKotlin.jar:<path_to_kotlin_stdlib>/kotlin-stdlib.jar MyJava.java
```

**> 참고:** 윈도우에서는 클래스패스 구분자로 세미콜론(;)을 사용합니다.

## 4. 실행하기
컴파일된 Java 코드를 실행할 때에도 Kotlin 클래스 파일 또는 jar와 Kotlin 표준 라이브러리를 클래스패스에 포함시키면 됩니다.

```Bash
java -cp .:MyKotlinClasses:<path_to_kotlin_stdlib>/kotlin-stdlib.jar my.java.pkg.MyJava
```

또는 jar 파일을 사용하는 경우

```Bash
java -cp .:MyKotlin.jar:<path_to_kotlin_stdlib>/kotlin-stdlib.jar my.java.pkg.MyJava
```

### 추가 팁
**패키지 관리:** Kotlin과 Java 코드 모두 동일한 패키지를 사용하면, 클래스패스 관리가 더 쉬워집니다.

**어노테이션 활용:** 만약 Kotlin에서 객체 또는 함수들을 Java에서 더 편리하게 사용하고 싶다면, `@JvmStatic`, `@JvmOverloads` 등의 어노테이션을 적절히 이용하여 Java 친화적인 API를 구성하세요.

**빌드 도구 활용:** 비록 IDE를 사용하지 않더라도 Maven, Gradle, 또는 Ant와 같은 빌드 도구를 사용하면 컴파일 및 의존성 관리를 보다 체계적으로 할 수 있습니다.

이렇게 함으로써 IDE 없이도 명령줄에서 Kotlin 코드를 컴파일하고, 그 결과물을 Java 코드에서 호출할 수 있습니다.

---

> Java 코드 컴파일 부분 설명이 좀 부실한 것 같은데. 디렉터리로 관리하는 클래스 파일 쪽이 특히.

디렉터리로 관리되는 Kotlin 클래스 파일을 Java 코드에서 잘 사용하려면, 클래스 파일들이 올바른 패키지 구조를 유지하며 디렉터리 내에 배치되어 있다는 점이 핵심입니다. 여기서 디렉터리 방식으로 컴파일된 Kotlin 클래스 파일을 Java 컴파일러가 잘 찾을 수 있도록 클래스패스(classpath)를 올바르게 설정해야 합니다. 아래에 자세한 설명과 예제를 첨부합니다.

---

## 1. Kotlin 코드 컴파일 및 클래스 파일의 배치

예를 들어, 다음과 같이 `MyKotlin.kt` 파일을 작성했다고 가정해 봅시다.

```kotlin
// MyKotlin.kt
package my.kotlin.pkg

fun greet(name: String): String {
    return "Hello, $name!"
}
```

위 코드를 명령줄에서 Kotlin 컴파일러를 사용해 디렉터리 형태로 컴파일하면,  
명령어는 다음과 같습니다:

```bash
kotlinc MyKotlin.kt -d MyKotlinClasses
```

이 명령은 `MyKotlinClasses`라는 디렉터리 내부에 `my/kotlin/pkg/MyKotlinKt.class` 파일을 생성합니다.  
즉, 컴파일된 Kotlin 코드의 패키지 구조는 실제 디렉터리 구조와 일치하게 됩니다.

---

## 2. Java 소스 코드에서 Kotlin 클래스 호출

Java에서 Kotlin의 최상위 함수는 파일 이름에 "Kt"가 붙은 클래스의 정적 메서드로 변환됩니다.  
예를 들어, 다음과 같은 Java 코드를 생각해 봅니다:

```java
// MyJava.java
package my.java.pkg;

public class MyJava {
    public static void main(String[] args) {
        // Kotlin 코드의 패키지와 클래스 이름을 완전히 명시해 호출합니다.
        String message = my.kotlin.pkg.MyKotlinKt.greet("World");
        System.out.println(message);
    }
}
```

---

## 3. Java 코드 컴파일하기 (디렉터리 방식)

Java 컴파일 시에는 Kotlin 컴파일러가 생성한 클래스 파일이 있는 디렉터리(`MyKotlinClasses`)와 Kotlin 표준 라이브러리(`kotlin-stdlib.jar`)를 클래스패스에 포함시켜야 합니다.

### **클래스 파일의 디렉터리 구조 예시**

만약 프로젝트의 디렉터리 구조가 아래와 같다면:

```
project/
├─ MyKotlinClasses/
│   └─ my/
│       └─ kotlin/
│           └─ pkg/
│               └─ MyKotlinKt.class
├─ my/
│   └─ java/
│       └─ pkg/
│           └─ MyJava.java
└─ kotlin-stdlib.jar
```

`MyKotlinClasses` 폴더는 Kotlin 컴파일러에 의해 생성된 클래스 파일들의 **루트(root)** 역할을 합니다.

### **컴파일 명령어 예제 (리눅스/유닉스 계열)**

Java 파일이 프로젝트 루트 또는 적절한 위치에 있다면, 다음과 같이 컴파일합니다.

```bash
javac -cp MyKotlinClasses:kotlin-stdlib.jar my/java/pkg/MyJava.java
```

- **-cp 옵션**: `MyKotlinClasses` 디렉터리를 추가하여, Java 컴파일러가 `my.kotlin.pkg.MyKotlinKt` 등 Kotlin의 컴파일된 클래스를 클래스패스에서 찾을 수 있게 합니다.  
- `kotlin-stdlib.jar`도 포함시켜야 Kotlin 표준 라이브러리 관련 클래스들이 누락되지 않습니다.

> **Note:**  
> - 만약 Windows 환경이라면 클래스패스 구분자로 콜론(`:`) 대신 세미콜론(`;`)을 사용해야 합니다.
> - 프로젝트 내 다른 디렉터리나 추가적인 라이브러리가 있다면, 클래스패스에 모두 포함시켜주어야 합니다.

---

## 4. Java 애플리케이션 실행

컴파일한 후, 실행할 때도 클래스패스에 동일하게 Kotlin 클래스 파일 디렉터리와 Kotlin 표준 라이브러리를 포함시켜 줍니다.

```bash
java -cp .:MyKotlinClasses:kotlin-stdlib.jar my.java.pkg.MyJava
```

여기서:
- `.` (현재 디렉터리)는 Java 애플리케이션의 기본 클래스를 찾기 위해 포함합니다.
- `MyKotlinClasses` 디렉터리와 `kotlin-stdlib.jar`를 포함시켜 **실행 시**에도 Kotlin 코드를 알맞게 로드할 수 있도록 합니다.

---

## 5. 컴파일 과정 상세 설명

- **패키지 구조 확인:**  
  컴파일 후 생성된 클래스 파일은 원래의 Kotlin 소스 코드에 명시한 패키지(`my.kotlin.pkg`)를 반영합니다. 따라서 Java 컴파일러는 클래스를 찾기 위해 클래스패스에서 `MyKotlinClasses/my/kotlin/pkg/MyKotlinKt.class` 경로를 탐색하게 됩니다.

- **클래스패스의 역할:**  
  `-cp` 옵션은 컴파일러나 JVM이 클래스 파일을 찾는 기준 경로를 지정합니다.  
  Kotlin에서 컴파일된 클래스 파일이 특정 디렉터리 아래에 있다면, 그 디렉터리를 루트로 지정해야 파일의 상대 경로(패키지 구조)가 올바르게 해석됩니다.  
  예를 들어, `javac -cp MyKotlinClasses:...` 명령은 `MyKotlinClasses`를 기준으로 패키지 구조를 찾게 돕습니다.

- **Kotlin 표준 라이브러리 포함:**  
  Kotlin 코드는 실행 시 Kotlin 표준 라이브러리(`kotlin-stdlib.jar`)에 정의된 여러 클래스와 함수를 사용합니다. 때문에, 이를 클래스패스에 추가하지 않으면 런타임 에러가 발생할 수 있습니다.

---

이와 같이 클래스 파일이 디렉터리 형태로 관리될 때, **Java 컴파일러에게 명시적으로 해당 디렉터리를 클래스패스에 포함**시켜 Kotlin에서 컴파일된 클래스들을 찾을 수 있게 해주는 것이 핵심입니다.

혹시 여기에 대해 더 상세한 빌드 스크립트 설정이나, Gradle 혹은 Maven과 통합하는 방법에 관심이 있나요? 추가적인 사항으로, Java와 Kotlin의 상호 운용 시 주의할 점이나 어노테이션 사용법 등도 이야기해볼 수 있습니다.

---

---

# 소감
```java
package pkg;

// import pkg.logic.Basic; (이거 없으면 컴파일 에러 뜸)

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
```


기초 공부나 더 하자.
