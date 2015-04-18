package de.hausbrand.libdir;

import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.plugins.InvalidPluginException
import org.gradle.api.tasks.*

class LibDirPlugin implements Plugin<Project> {
	void apply(Project project) {
		//check if compile and testCompile configurations exists
		if(!project.configurations.find { it.name == 'compile' || it.name == 'testCompile' })
		{
			throw new InvalidPluginException("no compile or testCompile configuration was found. Maybe java plugin is missing?")
		}

		// add plugin specific properties
		project.extensions.create("libdir", LibDirPluginExtension)

		// add two additional configurations for declaring dependencies which will be resolved into the lib folders
		project.configurations.create('compileDependencies')
		project.configurations.create('testCompileDependencies')
		project.configurations.testCompileDependencies.extendsFrom(project.configurations.compileDependencies)

		// create tasks which will do the resolving of dependencies
		project.task('resolveCompileDependencies', type: Copy)
		{
			description 'resolve and copy all compile dependencies'
			into "$project.libdir.libDirPathName/$project.libdir.compilePathName"
			from project.configurations.compileDependencies
			outputs.dir "$project.libdir.libDirPathName/$project.libdir.compilePathName"
		}
		project.task('resolveTestCompileDependencies', type: Copy)
		{
			description 'resolve and copy all test dependencies'
			into "$project.libdir.libDirPathName/$project.libdir.testPathName"
			from project.configurations.testCompileDependencies - project.configurations.compileDependencies
			outputs.dir "$project.libdir.libDirPathName/$project.libdir.testPathName"
		}
		project.task('resolveDependencies', type: Copy)
		{
			group 'Update dependencies into lib'
			description 'resolves and copies all dependencies into lib folder'
			dependsOn project.tasks.resolveCompileDependencies, project.tasks.resolveTestCompileDependencies
		}

		// add dependency to lib folders for the java compile and testCompile configurations
		project.dependencies.add('compile', project.fileTree(dir: "$project.libdir.libDirPathName/$project.libdir.compilePathName"))
		project.dependencies.add('testCompile', project.fileTree(dir: "$project.libdir.libDirPathName/$project.libdir.testPathName"))

		// add clean tasks
		project.task('cleanResolveCompileDependencies', type: Delete)
		{
			description 'deletes all compile dependencies from lib folder'
			delete project.tasks.resolveCompileDependencies.outputs
		}
		project.task('cleanResolveTestCompileDependencies', type: Delete)
		{
			description 'deletes all test dependencies from lib folder'
			delete project.tasks.resolveTestCompileDependencies.outputs
		}
		project.task('cleanResolveDependencies', type: Delete)
		{
			group 'Update dependencies into lib'
			description 'deletes all dependencies from lib folder'
			dependsOn project.tasks.cleanResolveCompileDependencies, project.tasks.cleanResolveTestCompileDependencies
		}

		if(project.hasProperty('eclipse'))
		{
			// make sure eclipse classpath uses relative paths
			project.eclipse.classpath.file {
				whenMerged { classpath ->
					classpath.entries.each { entry -> entry.path=entry.path.replace( "$project.rootDir", ".") }
				}
			}
		}
	}
}
