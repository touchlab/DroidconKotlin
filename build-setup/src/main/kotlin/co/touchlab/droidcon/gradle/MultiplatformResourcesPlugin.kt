package co.touchlab.droidcon.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskContainer
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.KotlinCocoapodsPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBinary
import org.jetbrains.kotlin.konan.file.recursiveCopyTo
import org.jetbrains.kotlin.konan.target.CompilerOutputKind
import java.io.File
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems

class MultiplatformResourcesPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.modifyKlibCompilationToIncludeResources()

        target.registerExtractResourcesTasksForAllBinaries()
    }

    private fun Project.modifyKlibCompilationToIncludeResources() {
        configureKotlinNativeCompilations {
            configureResourceBundlingToKlib()
        }
    }

    private fun KotlinNativeCompilation.configureResourceBundlingToKlib() {
        compileKotlinTaskProvider.configure {
            if (outputKind != CompilerOutputKind.LIBRARY) {
                return@configure
            }

            val processResourcesTaskProvider = project.tasks.named(processResourcesTaskName)

            inputs.files(processResourcesTaskProvider.map { it.outputs })

            doLast {
                copyResourcesToKlib(outputFile.get(), processResourcesTaskProvider.get())
            }
        }
    }

    private fun copyResourcesToKlib(klib: File, processResourcesTask: Task) {
        val allResources = processResourcesTask.outputs.files.singleFile

        if (!allResources.exists()) {
            return
        }

        klib.writeToZip { fileSystem ->
            val klibResourceDirectory = fileSystem.getPath("/$KLIB_RESOURCES_FOLDER")

            allResources.toPath().recursiveCopyTo(klibResourceDirectory)
        }
    }

    private fun File.writeToZip(write: (FileSystem) -> Unit) {
        val uri = URI.create("jar:file:" + this.absolutePath)

        val fileSystem = try {
            FileSystems.getFileSystem(uri)
        } catch (_: FileSystemNotFoundException) {
            FileSystems.newFileSystem(uri, mapOf("create" to true))
        }

        fileSystem.use(write)
    }

    private fun Project.registerExtractResourcesTasksForAllBinaries() {
        configureKotlinNativeBinaries {
            registerExtractResourcesTask(this)
        }
    }

    private fun Project.registerExtractResourcesTask(nativeBinary: NativeBinary) {
        project.tasks.register(nativeBinary.extractResourcesTaskName, ExtractResourcesTask::class.java) {
            klibs.set(nativeBinary.klibs)
            output.set(nativeBinary.outputDirectory.resolve("resources"))
        }
    }

    private val NativeBinary.extractResourcesTaskName: String
        get() {
            val binaryNameComponent = this.name.capitalized()
            val targetNameComponent = this.target.targetName.capitalized()
            return "extractResources$binaryNameComponent$targetNameComponent"
        }

    private val NativeBinary.klibs: Provider<FileCollection>
        get() = compilation.compileKotlinTaskProvider.map { compileKotlinTask ->
            val currentModuleKlib = project.files(compileKotlinTask.outputFile)

            (compilation.compileDependencyFiles + currentModuleKlib)
                .filter { it.extension == "klib" }
        }

    private fun Project.configureKotlinNativeCompilations(configuration: KotlinNativeCompilation.() -> Unit) {
        configureKotlinNativeTargets {
            compilations.configureEach {
                configuration()
            }
        }
    }

    private fun Project.configureKotlinNativeBinaries(configuration: NativeBinary.() -> Unit) {
        configureKotlinNativeTargets {
            binaries.configureEach {
                configuration()
            }
        }
    }

    private fun Project.configureKotlinNativeTargets(configuration: KotlinNativeTarget.() -> Unit) {
        configureKotlinExtension {
            targets.withType(KotlinNativeTarget::class.java).configureEach {
                configuration()
            }
        }
    }

    private fun Project.configureKotlinExtension(configuration: KotlinMultiplatformExtension.() -> Unit) {
        pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            extensions.configure(KotlinMultiplatformExtension::class.java) {
                configuration()
            }
        }
    }

    private fun <T : Task> TaskContainer.configureIfExists(name: String, type: Class<T>, configuration: T.() -> Unit) {
        withType(type).configureEach {
            if (this.name == name) {
                configuration(this)
            }
        }
    }
}

abstract class ExtractResourcesTask : DefaultTask() {

    @get:InputFiles
    abstract val klibs: Property<FileCollection>

    @get:OutputDirectory
    abstract val output: Property<File>

    @TaskAction
    fun extract() {
        val temporaryDirectory = project.unzipKlibsToTemporaryDirectory()

        project.copyResourcesToCorrectDirectory(temporaryDirectory)
    }

    private fun Project.unzipKlibsToTemporaryDirectory(): File {
        val temporaryDirectory = temporaryDir.resolve("extractedResources")

        copy {
            klibs.get()
                .filter { it.exists() }
                .forEach {
                    from(zipTree(it)) {
                        include("$KLIB_RESOURCES_FOLDER/**")
                    }
                }

            into(temporaryDirectory)
        }

        return temporaryDirectory
    }

    private fun Project.copyResourcesToCorrectDirectory(temporaryDirectory: File) {
        delete(output)

        copy {
            from(temporaryDirectory.resolve(KLIB_RESOURCES_FOLDER))
            into(output)
        }
    }
}

private const val KLIB_RESOURCES_FOLDER: String = "default/resources"
