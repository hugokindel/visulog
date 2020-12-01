
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
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.8.1.202007141445-r")
    testImplementation("junit:junit:4.+")
}


