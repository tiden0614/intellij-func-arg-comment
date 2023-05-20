# intellij-func-arg-comment

![Build](https://github.com/tiden0614/intellij-func-arg-comment/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/com.github.tiden0614.intellijfuncargcomment.svg)](https://plugins.jetbrains.com/plugin/com.github.tiden0614.intellijfuncargcomment)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/com.github.tiden0614.intellijfuncargcomment.svg)](https://plugins.jetbrains.com/plugin/com.github.tiden0614.intellijfuncargcomment)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [x] Get familiar with the [template documentation][template].
- [x] Adjust the [pluginGroup](./gradle.properties), [plugin ID](./src/main/resources/META-INF/plugin.xml) and [sources package](./src/main/kotlin).
- [x] Adjust the plugin description in `README` (see [Tips][docs:plugin-description])
- [x] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html?from=IJPluginTemplate).
- [x] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [x] Set the `PLUGIN_ID` in the above README badges.
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

<!-- Plugin description -->
# Func Arg Commenter
Add comments to function arguments that are constant expressions. Improves readability for
function calls in code reviews, etc. Once enabled, there will be a new "Annotate Constant Function Arguments with Names"
option in the editor popup menu when you right-click on a function name in a function call expression.

e.g. Given a function definition

```java
class MyClass {
  public void myMethod(int numFoo, List<Integer> listOfBar, boolean shouldOverride, long createdOn) {
      
  }
}
```

For a callsite of a function without any argument annotation:
```java
var obj = new MyClass();
obj.myMethod(10, Collections.emptyList(), false, Long.MAX_VALUE);
```

you can tell the plugin to annotate those arguments for you.
```java
var obj = new MyClass();
obj.myMethod(
  /*numFoo*/ 10,
  /*listOfBar*/ Collections.emptyList(),
  /*shouldOverride*/ false, 
  /*createdOn*/ Long.MAX_VALUE);
```
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "intellij-func-arg-comment"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/tiden0614/intellij-func-arg-comment/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation