plugins {
    id("org.jetbrains.kotlin.jvm") version "1.1.1"
}
dependencies {
    testCompile("junit:junit:4.12")
    compile(kotlinModule("stdlib-jre8"))
}
