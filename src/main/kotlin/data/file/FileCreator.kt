package data.file

import data.repository.SettingsRepository
import data.repository.SourceRootRepository
import model.AndroidComponent
import model.Category
import model.FileType
import model.Module
import javax.inject.Inject

private const val LAYOUT_DIRECTORY = "layout"

interface FileCreator {

    fun createScreenFiles(
        packageName: String,
        screenName: String,
        androidComponent: AndroidComponent,
        module: Module,
        category: Category
    )
}

class FileCreatorImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val sourceRootRepository: SourceRootRepository
) : FileCreator {

    override fun createScreenFiles(
        packageName: String,
        screenName: String,
        androidComponent: AndroidComponent,
        module: Module,
        category: Category
    ) {
        val codeSubdirectory = findCodeSubdirectory(packageName, module)
        val resourcesSubdirectory = findResourcesSubdirectory(module)
        if (codeSubdirectory != null) {
            settingsRepository.loadScreenElements(category.id).apply {
                filter { it.relatedAndroidComponent == AndroidComponent.NONE || it.relatedAndroidComponent == androidComponent }
                    .forEach {
                        if (it.fileType == FileType.LAYOUT_XML) {
                            val file = File(
                                it.fileName(screenName, packageName, androidComponent.displayName),
                                it.body(screenName, packageName, androidComponent.displayName),
                                it.fileType
                            )
                            resourcesSubdirectory.addFile(file)
                        } else {
                            val file = File(
                                it.fileName(screenName, packageName, androidComponent.displayName),
                                it.body(screenName, packageName, androidComponent.displayName),
                                it.fileType
                            )
                            codeSubdirectory.addFile(file)
                        }
                    }
            }
        }
    }

    private fun findCodeSubdirectory(packageName: String, module: Module): Directory? =
        sourceRootRepository.findCodeSourceRoot(module)?.run {
            var subdirectory = directory
            packageName.split(".").forEach {
                subdirectory = subdirectory.findSubdirectory(it) ?: subdirectory.createSubdirectory(it)
            }
            return subdirectory
        }

    private fun findResourcesSubdirectory(module: Module) =
        sourceRootRepository.findResourcesSourceRoot(module).directory.run {
            findSubdirectory(LAYOUT_DIRECTORY) ?: createSubdirectory(LAYOUT_DIRECTORY)
        }
}
