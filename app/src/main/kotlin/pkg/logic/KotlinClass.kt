package pkg.logic

// @file:JvmName("Basic")

class Basic {
//	@JvmField var property1 = 1
//	lateinit var property1 = 1
	var property1 = 1
	fun value() = this.property1 * 10
	fun print() = println(property1)
}
