
import com.niyaj.samples.apps.popos.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidRealmConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.realm.kotlin")

            dependencies {

                add("implementation", libs.findLibrary("realm.library.sync").get())
            }
        }
    }
}