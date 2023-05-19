# Intellij Function Argument Commenting Plugin
The goal of this plug in is to provide auto commenting feature for function calls to increase readability.

e.g. given a function

```java
class MyClass {
  public void myMethod(int numFoo, int numBar, boolean shouldOverride, long createdOn) {
    ...
  }
}
```

When calling the function, people would so something like

```java
var obj = new MyClass();
obj.myMethod(10, 20, false, 1000001);
```

The plugin automatically turns this function call into


```java
var obj = new MyClass();
obj.myMethod(
  /*numFoo*/ 10,
  /*numBar*/ 20,
  /*shouldOverride*/ false, 
  /*createdOn*/ 1000001);
```
