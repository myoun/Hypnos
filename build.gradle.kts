plugins {
    kotlin("jvm") version "1.5.0"
}

group = "kr.myoung2"
version = project.properties["version"] as String

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("net.kyori:adventure-api:4.7.0")
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
}


val shade = configurations.create("shade")
shade.extendsFrom(configurations.implementation.get())


tasks {

    javadoc {
        options.encoding = "UTF-8"
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "16"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "16"
    }

    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
        }
    }

    create<Jar>("sourceJar") {
        archiveClassifier.set("source")
        from(sourceSets["main"].allSource)
    }

    jar {
        from (shade.map { if (it.isDirectory) it else zipTree(it) })
    }
}