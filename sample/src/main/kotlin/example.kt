
/**
 * This function is documented, and Ank will check that it complies
 *
 * ```kotlin:ank
 * listOf(1, 2, 3).map { it + 1 }
 * ```
 */
fun example(): Unit = Unit

/**
 * This function is documented, and Ank can correctly handle the exception
 *
 * ```kotlin:ank:fail
 * throw RuntimeException("MOTHERFUCKING SNAKES ON A PLANE")
 * ```
 */
fun exampleException(): Unit = Unit

/**
 * This function is documented, and Ank can correctly handle the exception
 *
 * ```kotlin:ank:replace
 * fun hello(name: String) = "Hello $name!"
 *
 * hello("Λrrow")
 * ```
 */
fun exampleReplace(): Unit = Unit

///**
// * This function is documented, and Ank can correctly handle the exception
// *
// * ```java:ank
// * public String hello(String name) {
// *   return "Hello " + name + "!"
// * }
// *
// * hello("Λrrow")
// * ```
// */
//fun exampleJava(): Unit = Unit

/**
 * This function is documented, and I have access to my classpath from docs
 *
 * ```kotlin:ank
 * import arrow.core.Either
 *
 * Either.Right(1)
 * ```
 */
fun exampleDependency(): Unit = Unit
