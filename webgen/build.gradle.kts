
plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":config"))
    implementation(project(":analyzer"))
    implementation("com.github.xmlet:htmlflow:3.5")
    testImplementation("junit:junit:4.+")
}


