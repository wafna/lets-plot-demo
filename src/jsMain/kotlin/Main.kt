import org.jetbrains.letsPlot.Stat
import org.jetbrains.letsPlot.frontend.JsFrontendUtil
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.letsPlot
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.useEffectOnce
import web.dom.document
import react.dom.html.ReactHTML as h

fun main() {
    document.getElementById("root")?.also { createRoot(it).render(App.create()) }
        ?: error("Couldn't find root container!")
}

val App =
    FC<Props> {
        val plotId = "plotz"
        useEffectOnce {
            createContent(plotId)
        }
        h.div { id = plotId }
    }

fun createContent(id: String) {
    val contentDiv = kotlinx.browser.document.getElementById(id) ?: error("Couldn't find element $id")

    val data = mapOf("Names" to listOf("Huey", "Dewey", "Louie"), "Scores" to listOf(-10.0, 20.0, 0.0))

    val p = letsPlot(data) + geomBar(
        stat = Stat.identity,
        color = "dark-green",
        fill = "green",
        alpha = .3,
        size = 2.0
    ) {
        x = "Names"
        y = "Scores"
    }

    val plotDiv = JsFrontendUtil.createPlotDiv(p)
    contentDiv.appendChild(plotDiv)
}
