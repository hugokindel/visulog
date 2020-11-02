
plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":config"))
    implementation(project(":analyzer"))
    testImplementation("junit:junit:4.+")
}


