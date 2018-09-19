/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.configuration

import org.jetbrains.plugins.gradle.frameworkSupport.BuildScriptDataBuilder

class KotlinGradleMobileMultiplatformModuleBuilder : KotlinGradleAbstractMultiplatformModuleBuilder() {

    private val commonName: String = "common"
    private var jvmTargetName: String = "android"
    private var nativeTargetName: String = "ios"

    private val commonSourceName get() = "$commonName$productionSuffix"
    private val commonTestName get() = "$commonName$testSuffix"
    private val nativeSourceName get() = "$nativeTargetName$productionSuffix"
    private val nativeTestName get() = "$nativeTargetName$testSuffix"

    override fun getBuilderId() = "kotlin.gradle.multiplatform.mobile"

    // TODO: local.properties
    // TODO: why Android source set is not created?

    override fun getPresentableName() = "Kotlin (Mobile Android/iOS)"

    override fun getDescription() =
        "Multiplatform Gradle projects allow reusing the same Kotlin code between Android and iOS mobile platforms."

    override fun BuildScriptDataBuilder.setupAdditionalDependencies() {
        addBuildscriptDependencyNotation("classpath 'com.android.tools.build:gradle:3.2.0-beta03'")
        addBuildscriptRepositoriesDefinition("google()")
        addRepositoriesDefinition("google()")
    }

    override fun buildMultiPlatformPart(): String {
        return """
            apply plugin: 'com.android.application'

            android {
                compileSdkVersion 28
                defaultConfig {
                    applicationId "org.jetbrains.kotlin.mpp_app_android"
                    minSdkVersion 15
                    targetSdkVersion 28
                    versionCode 1
                    versionName "1.0"
                    testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
                }
                buildTypes {
                    release {
                        minifyEnabled false
                    }
                }
            }

            dependencies {
                implementation 'com.android.support:appcompat-v7:28.0.0-rc02'
                implementation 'com.android.support.constraint:constraint-layout:1.1.3'
                testImplementation 'junit:junit:4.12'
                androidTestImplementation 'com.android.support.test:runner:1.0.2'

                implementation 'org.jetbrains.kotlin:kotlin-stdlib'
            }

            kotlin {
                targets {
                    // For ARM, preset should be changed to presets.android_arm32 or presets.android_arm64
                    fromPreset(presets.android, '$jvmTargetName')
                    // For ARM, preset should be changed to presets.iosArm32 or presets.iosArm64
                    fromPreset(presets.iosX64, '$nativeTargetName')
                }
                sourceSets {
                    $commonSourceName {
                        dependencies {
                            implementation 'org.jetbrains.kotlin:kotlin-stdlib-common'
                        }
                    }
                    $commonTestName {
                        dependencies {
                    		implementation 'org.jetbrains.kotlin:kotlin-test-common'
                    		implementation 'org.jetbrains.kotlin:kotlin-test-annotations-common'
                        }
                    }
                    $nativeSourceName {
                    }
                    $nativeTestName {
                    }
                }
            }
        """.trimIndent()
    }
}