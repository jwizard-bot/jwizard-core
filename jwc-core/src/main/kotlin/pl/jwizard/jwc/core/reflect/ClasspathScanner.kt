package pl.jwizard.jwc.core.reflect

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import pl.jwizard.jwl.AppRunner
import kotlin.reflect.KClass

class ClasspathScanner<T : Annotation>(
	private val annotationClazz: KClass<T>,
	private val subpackage: String,
) : ClassPathScanningCandidateComponentProvider(false) {
	init {
		addIncludeFilter(AnnotationTypeFilter(annotationClazz.java))
	}

	// get classes as map, where key is annotation and value is KClass type
	fun findComponents() = findCandidateComponents("${AppRunner.BASE_PACKAGE}.$subpackage")
		.associate {
			val clazz = Class.forName(it.beanClassName)
			clazz.getAnnotation(annotationClazz.java) to clazz
		}
}
