package de.hausbrand.libdir

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class LibDirPluginTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void applyPluginNoJavaPluginAvailable() {
		Project project = ProjectBuilder.builder().build()
		exception.expectMessage("Failed to apply plugin [id 'de.hausbrand.libdir']")
		project.pluginManager.apply 'de.hausbrand.libdir'
	}

	@Test
	public void applyPluginWithJava() {
		Project project = ProjectBuilder.builder().build()
		project.pluginManager.apply 'java'
		project.pluginManager.apply 'de.hausbrand.libdir'
	}

	@Test
	public void applyPluginWithGroovy() {
		Project project = ProjectBuilder.builder().build()
		project.pluginManager.apply 'groovy'
		project.pluginManager.apply 'de.hausbrand.libdir'
	}
}
